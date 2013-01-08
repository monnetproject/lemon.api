/**
 * ********************************************************************************
 * Copyright (c) 2011, Monnet Project All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met: *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. * Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials provided
 * with the distribution. * Neither the name of the Monnet Project nor the names
 * of its contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE MONNET PROJECT BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 ********************************************************************************
 */
package eu.monnetproject.lemon.impl.io.xml;

import eu.monnetproject.lemon.LemonModel;
import eu.monnetproject.lemon.LinguisticOntology;
import eu.monnetproject.lemon.AbstractVisitor;
import eu.monnetproject.lemon.impl.LemonElementImpl;
import eu.monnetproject.lemon.model.LemonElement;
import eu.monnetproject.lemon.model.LemonElementOrPredicate;
import eu.monnetproject.lemon.model.Text;
import java.io.StringWriter;
import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import net.lexinfo.LexInfo;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 *
 * @author John McCrae
 */
public class RDFXMLWriter extends AbstractVisitor {

    private final Document document;
    private final Element root;
    private final Map<Object, Node> nodes = new HashMap<Object, Node>();
    private final HashSet<LemonElementImpl<?>> visited = new HashSet<LemonElementImpl<?>>();
    private final static String RDF = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
    private final Class<?> rootClass;

    public RDFXMLWriter(LinguisticOntology lo, Class<?> rootClass) throws ParserConfigurationException {
        super(lo);
        this.rootClass = rootClass;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        DocumentBuilder db = dbf.newDocumentBuilder();
        final DOMImplementation dOMImplementation = db.getDOMImplementation();
        document = dOMImplementation.createDocument(RDF, "RDF", null);
        document.setXmlStandalone(true);
        root = document.getDocumentElement();
        root.setPrefix("rdf");
        //document.appendChild(root);
    }

    @Override
    public void visit(LemonElement _element) {
        if (!(_element instanceof LemonElementImpl)) {
            throw new IllegalArgumentException();
        }
        final LemonElementImpl<?> element = (LemonElementImpl) _element;
        if (!nodes.containsKey(element)) {
            final Element node = document.createElementNS(LemonModel.LEMON_URI, element.getModelName());
            node.setPrefix("lemon");
            nodes.put(element, node);
            if (rootClass.isAssignableFrom(element.getClass())) {
                root.appendChild(node);
            }
        }
        final Node node = nodes.get(element);
        for (Object entry : element.getElements().entrySet()) {
            @SuppressWarnings("unchecked")
            URI uri = ((Map.Entry<URI, Object>) entry).getKey();
            @SuppressWarnings("unchecked")
            Collection<Object> objs = ((Map.Entry<URI, Collection<Object>>) entry).getValue();
            String prefix, suffix;
            if (uri.toString().startsWith(LemonModel.LEMON_URI)) {
                prefix = LemonModel.LEMON_URI;
                suffix = uri.toString().substring(LemonModel.LEMON_URI.length());
            } else {
                final int idx = Math.max(uri.toString().lastIndexOf("/"), uri.toString().lastIndexOf("#"));
                if (idx >= 0) {
                    prefix = uri.toString().substring(0, idx + 1);
                    suffix = uri.toString().substring(idx + 1);
                } else {
                    prefix = null;
                    suffix = uri.toString();
                }
            }
            for (Object obj : objs) {
                if (uri.toString().equals(RDF + "type") && obj instanceof URI && obj.toString().equals(LemonModel.LEMON_URI + element.getModelName())) {
                    continue;
                }
                final Element predNode = prefix != null ? document.createElementNS(prefix, suffix) : document.createElement(suffix);
                if (prefix != null && prefix.equals(LemonModel.LEMON_URI)) {
                    predNode.setPrefix("lemon");
                } else if (prefix != null && prefix.equals(LexInfo.LEXINFO_URI)) {
                    predNode.setPrefix("lexinfo");
                } else if (prefix != null && prefix.equals(RDF)) {
                    predNode.setPrefix("rdf");
                }
                node.appendChild(predNode);
                if (obj instanceof LemonElementImpl) {
                    LemonElementImpl<?> element2 = (LemonElementImpl) obj;
                    Node childNode;
                    if (!nodes.containsKey(element2)) {
                        childNode = document.createElementNS(LemonModel.LEMON_URI, element2.getModelName());
                        childNode.setPrefix("lemon");
                        nodes.put(element2, childNode);
                        predNode.appendChild(childNode);
                    } else {
                        if (nodes.get(element2).getParentNode() == null) {
                            predNode.appendChild(nodes.get(element2));
                        } else if (element2.getURI() != null) {
                            final Attr attr = document.createAttributeNS(RDF, "resource");
                            attr.setPrefix("rdf");
                            attr.setTextContent(element2.getURI().toString());
                            predNode.getAttributes().setNamedItemNS(attr);
                        } else {
                            final Attr attr = document.createAttributeNS(RDF, "nodeID");
                            attr.setPrefix("rdf");
                            attr.setTextContent(element2.getID());
                            predNode.getAttributes().setNamedItemNS(attr);
                        }
                    }
                } else if (obj instanceof Text) {
                    Text text = (Text) obj;
                    final org.w3c.dom.Text textNode = document.createTextNode(text.value);
                    predNode.appendChild(textNode);
                    final Attr xmlLang = document.createAttribute("xml:lang");
                    xmlLang.setTextContent(text.language.toString());
                    predNode.getAttributes().setNamedItem(xmlLang);
                } else if (obj instanceof String) {
                    String str = (String) obj;
                    final org.w3c.dom.Text textNode = document.createTextNode(str);
                    predNode.appendChild(textNode);
                } else if (obj instanceof List) {
                    @SuppressWarnings("unchecked")
                    List<LemonElementImpl<?>> elems = (List<LemonElementImpl<?>>) obj;
                    final Attr parseType = document.createAttributeNS(RDF, "parseType");
                    parseType.setTextContent("Collection");
                    predNode.getAttributes().setNamedItemNS(parseType);
                    for (LemonElementImpl<?> element2 : elems) {
                        Node childNode;
                        if (!nodes.containsKey(element2)) {
                            childNode = document.createElementNS(LemonModel.LEMON_URI, element2.getModelName());
                            childNode.setPrefix("lemon");
                            nodes.put(element2, childNode);
                            predNode.appendChild(childNode);
                        } else {
                            if (nodes.get(element2).getParentNode() == null) {
                                predNode.appendChild(nodes.get(element2));
                            } else if (element2.getURI() != null) {
                                childNode = document.createElementNS(LemonModel.LEMON_URI, element2.getModelName());
                                final Attr attr = document.createAttributeNS(RDF, "resource");
                                attr.setPrefix("rdf");
                                attr.setTextContent(element2.getURI().toString());
                                childNode.getAttributes().setNamedItemNS(attr);
                                predNode.appendChild(childNode);
                            } else {
                                childNode = document.createElementNS(LemonModel.LEMON_URI, element2.getModelName());
                                final Attr attr = document.createAttributeNS(RDF, "nodeID");
                                attr.setPrefix("rdf");
                                attr.setTextContent(element2.getID());
                                childNode.getAttributes().setNamedItemNS(attr);
                                predNode.appendChild(childNode);
                            }
                        }
                    }
                } else if (obj instanceof URI) {
                    final Attr resource = document.createAttributeNS(RDF, "resource");
                    resource.setPrefix("rdf");
                    resource.setTextContent(obj.toString());
                    predNode.getAttributes().setNamedItemNS(resource);
                } else if (obj instanceof LemonElementOrPredicate) {
                    final Attr resource = document.createAttributeNS(RDF, "resource");
                    resource.setPrefix("rdf");
                    resource.setTextContent(((LemonElementOrPredicate) obj).getURI().toString());
                    predNode.getAttributes().setNamedItemNS(resource);
                } else {
                    throw new RuntimeException("Unexpected lemon element member " + (obj == null ? (uri + " had null object") : obj.getClass().toString()));
                }
            }
        }

        if (element.getURI() == null) {
            final Attr nodeID = document.createAttributeNS(RDF, "nodeID");
            nodeID.setPrefix("rdf");
            nodeID.setTextContent(element.getID());
            node.getAttributes().setNamedItemNS(nodeID);
        } else if (element.getURI() != null) {
            final Attr about = document.createAttributeNS(RDF, "about");
            about.setPrefix("rdf");
            about.setTextContent(element.getURI().toString());
            node.getAttributes().setNamedItemNS(about);
        }
        visited.add(element);
    }

    @Override
    public boolean visitFirst() {
        return true;
    }

    @Override
    public boolean hasVisited(LemonElement element) {
        if (element instanceof LemonElementImpl) {
            return visited.contains((LemonElementImpl<?>) element);
        } else {
            return false;
        }
    }
    private Transformer transformer;

    private Transformer getTransformer() throws TransformerConfigurationException {
        if (transformer == null) {
            final TransformerFactory factory = TransformerFactory.newInstance();
            factory.setAttribute("indent-number", 2);
            transformer = factory.newTransformer();
        }
        return transformer;
    }

    public String getDocument() throws TransformerException {
        Transformer trans = getTransformer();
        trans.setOutputProperty(OutputKeys.INDENT, "yes");
        //trans.setOutputProperty(OutputKeys.ENCODING, "ascii");
        trans.setOutputProperty(OutputKeys.ENCODING,System.getProperty("lemon.api.xml.encoding","us-ascii"));
        StreamResult result = new StreamResult(new StringWriter());
        DOMSource source = new DOMSource(document);
        trans.transform(source, result);
        return result.getWriter().toString();
    }
}
