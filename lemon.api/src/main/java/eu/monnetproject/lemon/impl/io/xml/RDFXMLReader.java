/****************************************************************************
 * Copyright (c) 2011, Monnet Project
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the Monnet Project nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE MONNET PROJECT BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 ********************************************************************************/
package eu.monnetproject.lemon.impl.io.xml;

import eu.monnetproject.lemon.LemonModel;
import eu.monnetproject.lemon.impl.LemonModelImpl;
import eu.monnetproject.lemon.impl.io.ReaderVisitor;
import eu.monnetproject.lemon.impl.io.Visitor;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Iterator;
import javax.xml.namespace.QName;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;


/**
 * RDFHandler
 * RDF reader calling the method 'found' each time it finds a
 * new RDFStatement
 * @author John McCrae modified from Pierre Lindenbaum PhD plindenbaum@yahoo.fr
 *
 */
public class RDFXMLReader {
    private final Visitor defaultAccepter;
    private static long ID_GENERATOR = System.currentTimeMillis();
    private XMLInputFactory factory = null;
    private XMLEventReader parser = null;
    private URI base = null;
    private static String RDFNS = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";

    protected static class RDFEvent {

        URI subject = null;
        URI predicate = null;
        Object value = null;
        URI valueType = null;
        String lang = null;
        int listIndex = -1;
    }

    /**
     * constructor
     * initialize the XMLInputFactory
     */
    public RDFXMLReader(LemonModelImpl model) {
        this.factory = XMLInputFactory.newInstance();
        this.factory.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, Boolean.TRUE);
        this.factory.setProperty(XMLInputFactory.IS_COALESCING, Boolean.TRUE);
        this.factory.setProperty(XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES, Boolean.TRUE);
        this.defaultAccepter = new ReaderVisitor(model);
    }
    
    public RDFXMLReader(Visitor visitor) {
        this.factory = XMLInputFactory.newInstance();
        this.factory.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, Boolean.TRUE);
        this.factory.setProperty(XMLInputFactory.IS_COALESCING, Boolean.TRUE);
        this.factory.setProperty(XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES, Boolean.TRUE);
        this.defaultAccepter = visitor;
    }

    /**
     * called when this handler finds a new RDF triple
    
     * @param event
     */
    protected void found(
            RDFEvent event) throws XMLStreamException {
        try {
            found(event.subject, event.predicate, event.value, event.valueType, event.lang, event.listIndex);
        } catch (IOException err) {
            throw new XMLStreamException(err);
        }
    }

    /**
     * This method should be overridden by the user
     * @param subject
     * @param predicate
     * @param value
     * @param dataType
     * @param lang
     * @param index
     * @throws IOException
     */
    public void found(
            URI subject,
            URI predicate,
            Object value,
            URI dataType,
            String lang,
            int index) throws IOException {
        if(subject.getScheme() == null) {
            if(value instanceof URI) {
                URI valueURI = (URI)value;
                if(valueURI.getScheme() == null) {
                    defaultAccepter.accept(subject.toString(), predicate, value.toString());
                } else {
                    defaultAccepter.accept(subject.toString(), predicate, valueURI);
                }
            } else {
                defaultAccepter.accept(subject.toString(), predicate, value.toString(), lang);
            }
        } else {
            if(value instanceof URI) {
                URI valueURI = (URI)value;
                if(valueURI.getScheme() == null) {
                    defaultAccepter.accept(subject, predicate, value.toString());
                } else {
                    defaultAccepter.accept(subject, predicate, valueURI);
                }
            } else {
                defaultAccepter.accept(subject, predicate, value.toString(), lang);
            }
        }
    }

    public LemonModel getModel() { return ((ReaderVisitor)defaultAccepter).getModel(); }
    
    public void setBase(URI base) {
        this.base = base;
    }

    /** return XMLEventReader */
    protected XMLEventReader getReader() {
        return this.parser;
    }

    /** do the parsing */
    private void read(Reader in) throws XMLStreamException {
        try {
            /** create a XML Event parser */
            this.parser = this.factory.createXMLEventReader(in);
            /** loop until we find a rdf:RDF element */
            while (getReader().hasNext()) {
                XMLEvent event = getReader().nextEvent();
                if (event.isStartElement()) {
                    StartElement start = (StartElement) event;
                    if (name2string(start).equals(RDFNS + "RDF")) {
                        parseRDF();
                    }
                }
            }
        } catch (URISyntaxException e) {
            this.parser = null;
            throw new XMLStreamException(e);
        }
        this.parser = null;
    }

    private void parseRDF() throws XMLStreamException, URISyntaxException {
        while (getReader().hasNext()) {
            XMLEvent event = getReader().nextEvent();

            if (event.isEndElement()) {
                return;
            } else if (event.isStartElement()) {
                parseDescription(event.asStartElement());
            } else if (event.isProcessingInstruction()) {
                throw new XMLStreamException("Found Processing Instruction in RDF ???");
            } else if (event.isCharacters()
                    && event.asCharacters().getData().trim().length() > 0) {
                throw new XMLStreamException("Found text in RDF ???");
            }
        }
    }

    /**
     * Parse description of a Resource
     * @param description
     * @return
     * @throws URISyntaxException
     * @throws XMLStreamException
     */
    private URI parseDescription(StartElement description) throws URISyntaxException, XMLStreamException {
        URI descriptionURI = null;
        Attribute att = description.getAttributeByName(new QName(RDFNS, "about"));
        if (att != null) {
            descriptionURI = createURI(att.getValue());
        }
        if (descriptionURI == null) {
            att = description.getAttributeByName(new QName(RDFNS, "nodeID"));
            if (att != null) {
                descriptionURI = createURI(att.getValue());
            }
        }
        if (descriptionURI == null) {
            att = description.getAttributeByName(new QName(RDFNS, "ID"));
            if (att != null) {
                descriptionURI = resolveBase(att.getValue());
            }
        }

        if (descriptionURI == null) {
            descriptionURI = createAnonymousURI();
        }

        QName qn = description.getName();
        if (!(qn.getNamespaceURI().equals(RDFNS)
                && qn.getLocalPart().equals("Description"))) {
            RDFEvent evt = new RDFEvent();
            evt.subject = descriptionURI;
            evt.predicate = createURI(RDFNS + "type");
            evt.value = name2uri(qn);
            found(evt);
        }

        /** loop over attributes */
        for (Iterator<?> i = description.getAttributes();
                i.hasNext();) {
            att = (Attribute) i.next();
            qn = att.getName();
            String local = qn.getLocalPart();
            if (qn.getNamespaceURI().equals(RDFNS)
                    && (local.equals("about")
                    || local.equals("ID")
                    || local.equals("nodeID"))) {
                continue;
            }
            RDFEvent evt = new RDFEvent();
            evt.subject = descriptionURI;
            evt.predicate = name2uri(qn);
            evt.value = att.getValue();
            found(evt);
        }


        while (getReader().hasNext()) {
            XMLEvent event = getReader().nextEvent();

            if (event.isEndElement()) {
                return descriptionURI;
            } else if (event.isStartElement()) {
                parsePredicate(descriptionURI, event.asStartElement());
            } else if (event.isProcessingInstruction()) {
                throw new XMLStreamException("Found Processing Instruction in RDF ???");
            } else if (event.isCharacters()
                    && event.asCharacters().getData().trim().length() > 0) {
                throw new XMLStreamException("Found text in RDF ??? \""
                        + event.asCharacters().getData() + "\"");
            }
        }

        return descriptionURI;
    }

    /**
     * parse predicate
     * @param descriptionURI
     * @param predicate 
     * @throws URISyntaxException
     * @throws XMLStreamException
     */
    private void parsePredicate(URI descriptionURI, StartElement predicate) throws URISyntaxException, XMLStreamException {
        String parseType = null;
        String lang = null;
        URI datatype = null;
        Attribute att;
        QName qn = null;
        URI resource = null;

        URI predicateURI = name2uri(predicate.getName());

        /** collect attributes */
        for (int loop = 0; loop < 2; ++loop) {
            for (Iterator<?> i = predicate.getAttributes();
                    i.hasNext();) {
                att = (Attribute) i.next();
                qn = att.getName();
                String local = qn.getLocalPart();
                if (qn.getPrefix().equals("xml")
                        && local.equals("lang")) {
                    if (loop == 0) {
                        lang = att.getValue();
                    }
                    continue;
                } else if (qn.getNamespaceURI().equals(RDFNS)) {
                    if (local.equals("parseType")) {
                        if (loop == 0) {
                            parseType = att.getValue();
                        }
                        continue;
                    } else if (local.equals("datatype")) {
                        if (loop == 0) {
                            datatype = createURI(att.getValue());
                        }
                        continue;
                    } else if (local.equals("resource")) {
                        if (loop == 0) {
                            resource = createURI(att.getValue());
                        }
                        continue;
                    } else if (local.equals("nodeID")) {
                        if (loop == 0) {
                            resource = createURI(att.getValue());
                        }
                        continue;
                    } else if (local.equals("ID")) {
                        if (loop == 0) {
                            resource = resolveBase(att.getValue());
                        }
                        continue;
                    }
                }

                if (loop == 1) {
                    if (resource != null) {
                        RDFEvent evt = new RDFEvent();
                        evt.subject = resource;
                        evt.predicate = name2uri(att.getName());
                        evt.value = att.getValue();
                        found(evt);
                    } else {
                        throw new XMLStreamException("Cannot handle attribute " + att);
                    }
                }

            }
        }




        if (resource != null) {
            RDFEvent evt = new RDFEvent();
            evt.subject = descriptionURI;
            evt.predicate = predicateURI;
            evt.value = resource;
            found(evt);
            XMLEvent event = getReader().peek();
            if (event != null && event.isEndElement()) {
                getReader().nextEvent();
                return;
            }

            throw new XMLStreamException("Expected a EndElement for this element");
        }

        if (parseType == null) {
            parseType = "default";
        }

        if (parseType.equals("Literal")) {
            StringBuilder b = parseLiteral();

            RDFEvent evt = new RDFEvent();
            evt.subject = descriptionURI;
            evt.predicate = predicateURI;
            evt.value = b.toString();
            evt.lang = lang;
            evt.valueType = datatype;
            found(evt);
        } else if (parseType.equals("Resource")) {
            URI blanck = createAnonymousURI();

            RDFEvent evt = new RDFEvent();
            evt.subject = descriptionURI;
            evt.predicate = predicateURI;
            evt.value = blanck;
            evt.lang = lang;
            evt.valueType = datatype;



            found(evt);
            while (getReader().hasNext()) {
                XMLEvent event = getReader().nextEvent();
                if (event.isStartElement()) {
                    parsePredicate(blanck, event.asStartElement());
                } else if (event.isEndElement()) {
                    return;
                }
            }

        } else if (parseType.equals("Collection")) {
            int index = 0;
            while (getReader().hasNext()) {
                XMLEvent event = getReader().nextEvent();
                if (event.isStartElement()) {
                    URI value = parseDescription(event.asStartElement());

                    RDFEvent evt = new RDFEvent();
                    evt.subject = descriptionURI;
                    evt.predicate = predicateURI;
                    evt.value = value;
                    evt.lang = lang;
                    evt.valueType = datatype;
                    evt.listIndex = (++index);

                    found(evt);
                } else if (event.isEndElement()) {
                    return;
                }
            }
        } else {
            boolean foundResourceAsChild = false;
            StringBuilder b = new StringBuilder();
            while (getReader().hasNext()) {
                XMLEvent event = getReader().nextEvent();
                if (event.isStartElement()) {
                    if (b.toString().trim().length() != 0) {
                        throw new XMLStreamException(
                                "Bad text \"" + b + "\" before "
                                + event.asStartElement().getName());
                    }
                    URI childURI = parseDescription(event.asStartElement());
                    RDFEvent evt = new RDFEvent();
                    evt.subject = descriptionURI;
                    evt.predicate = predicateURI;
                    evt.value = childURI;
                    found(evt);
                    b.setLength(0);
                    foundResourceAsChild = true;
                } else if (event.isCharacters()) {
                    b.append(event.asCharacters().getData());
                } else if (event.isEndElement()) {
                    if (!foundResourceAsChild) {
                        RDFEvent evt = new RDFEvent();
                        evt.subject = descriptionURI;
                        evt.predicate = predicateURI;
                        evt.value = b.toString();
                        evt.lang = lang;
                        evt.valueType = datatype;
                        found(evt);
                    } else {
                        if (b.toString().trim().length() != 0) {
                            throw new XMLStreamException("Found bad text " + b);
                        }
                    }
                    return;
                }
            }
        }

    }

    private URI resolveBase(String ID) throws URISyntaxException {
        if (this.base == null) {
            return createURI(ID);
        }
        return this.base.resolve(ID);
    }

    private StringBuilder parseLiteral() throws XMLStreamException {
        StringBuilder b = new StringBuilder();
        QName qn;
        int depth = 0;
        while (getReader().hasNext()) {
            XMLEvent event = getReader().nextEvent();
            if (event.isCharacters()) {
                b.append(escapeXML(event.asCharacters().getData()));
            } else if (event.isProcessingInstruction()) {
                b.append("<?").append(event.asCharacters()).append("?>");
            } else if (event.isEndElement()) {
                if (depth == 0) {
                    return b;
                }
                qn = event.asEndElement().getName();
                b.append("</").append(qn.getPrefix()).append(":").append(qn.getLocalPart()).append(">");
                depth--;
            } else if (event.isStartElement()) {
                qn = event.asEndElement().getName();
                b.append("<").append(qn.getPrefix()).append(":").append(qn.getLocalPart());

                for (Iterator<?> i = event.asStartElement().getAttributes();
                        i.hasNext();) {
                    Attribute att = (Attribute) i.next();
                    qn = att.getName();
                    b.append(" ").append(qn.getPrefix()).append(":").
                            append(qn.getLocalPart()).
                            append("=\"").
                            append(escapeXML(att.getValue())).
                            append("\"");
                }
                event = getReader().peek();
                if (event != null && event.isEndElement()) {
                    getReader().nextEvent();
                    b.append("/>");
                } else {
                    b.append(">");
                    depth++;
                }
            }
        }

        return b;
    }

    protected URI createAnonymousURI() throws URISyntaxException {
        return createURI("_" + (++ID_GENERATOR));
    }

    public void parse(InputStream in) throws XMLStreamException {
        read(new InputStreamReader(in));
    }

    public void parse(Reader in) throws XMLStreamException {
        read(in);
    }

    public void parse(File in) throws XMLStreamException {
        try {
            FileReader fin = new FileReader(in);
            read(fin);
            fin.close();
        } catch (IOException e) {
            throw new XMLStreamException(e);
        }
    }

    public void parse(URL in) throws XMLStreamException {
        try {
            InputStream fin = in.openStream();
            read(new InputStreamReader(fin));
            fin.close();
        } catch (IOException e) {
            throw new XMLStreamException(e);
        }
    }

    private String name2string(StartElement e) {
        return name2string(e.getName());
    }

    private String name2string(QName name) {
        return name.getNamespaceURI() + name.getLocalPart();
    }

    private URI name2uri(QName name) throws URISyntaxException {
        return createURI(name2string(name));
    }

    private URI createURI(String uriAsString) throws URISyntaxException {
        return new URI(uriAsString);
    }

    private static String escapeXML(String s) {
        if (s == null) {
            throw new NullPointerException("XML.escape(null)");
        }
        int needed = -1;
        for (int i = 0; i < s.length(); ++i) {
            switch (s.charAt(i)) {
                case '\'':
                case '\"':
                case '&':
                case '<':
                case '>':
                    needed = i;
                    break;

                default:
                    break;
            }
            if (needed != -1) {
                break;
            }
        }
        if (needed == -1) {
            return s.toString();
        }
        StringBuilder buffer = new StringBuilder(s.subSequence(0, needed));
        for (int i = needed; i < s.length(); ++i) {
            switch (s.charAt(i)) {
                case '\'':
                    buffer.append("&apos;");
                    break;
                case '\"':
                    buffer.append("&quot;");
                    break;
                case '&':
                    buffer.append("&amp;");
                    break;
                case '<':
                    buffer.append("&lt;");
                    break;
                case '>':
                    buffer.append("&gt;");
                    break;
                default:
                    buffer.append(s.charAt(i));
                    break;
            }
        }
        return buffer.toString();
    }
}
