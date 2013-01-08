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
 * *******************************************************************************
 */
package eu.monnetproject.lemon.impl;

import eu.monnetproject.lemon.LinguisticOntology;
import eu.monnetproject.lemon.impl.io.ReaderAccepter;
import eu.monnetproject.lemon.model.Argument;
import eu.monnetproject.lemon.model.Component;
import eu.monnetproject.lemon.model.Condition;
import eu.monnetproject.lemon.model.Constituent;
import eu.monnetproject.lemon.model.Definition;
import eu.monnetproject.lemon.model.Example;
import eu.monnetproject.lemon.model.Frame;
import eu.monnetproject.lemon.model.LemonElementOrPredicate;
import eu.monnetproject.lemon.model.LexicalEntry;
import eu.monnetproject.lemon.model.LexicalForm;
import eu.monnetproject.lemon.model.LexicalSense;
import eu.monnetproject.lemon.model.LexicalTopic;
import eu.monnetproject.lemon.model.Lexicon;
import eu.monnetproject.lemon.model.MorphPattern;
import eu.monnetproject.lemon.model.MorphTransform;
import eu.monnetproject.lemon.model.Part;
import eu.monnetproject.lemon.model.Phrase;
import eu.monnetproject.lemon.model.Prototype;
import eu.monnetproject.lemon.model.SenseContext;
import eu.monnetproject.lemon.model.Word;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.AbstractList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author John McCrae
 */
public class SPARQLResolver implements RemoteResolver, LexiconResolver {

    private static final Pattern POEXTRACTOR = Pattern.compile("[po](\\d+)");
    private final URL endpoint;
    private final Set<URI> graphs;
    private final LinguisticOntology lingOnto;
    private final HashMap<Object, ReaderAccepter> accepters = new HashMap<Object, ReaderAccepter>();

    public SPARQLResolver(URL endpoint, Set<URI> graphs, LinguisticOntology lingOnto) {
        this.endpoint = endpoint;
        this.graphs = graphs;
        this.lingOnto = lingOnto;
    }

    @Override
    public void resolveRemote(LemonModelImpl model, LemonElementImpl<?> element, int depth) {
        final AccepterFactory accepterFactory = new AccepterFactory(accepters, lingOnto, model);
        final Map<URI, Map<Object,Object>> result = sparqlAll(element.getURI(), element.getID(), depth);
        insertTriples(result, element, accepterFactory);
    }

    private void insertTriples(final Map<URI, Map<Object,Object>> result, ReaderAccepter element, final AccepterFactory accepterFactory) throws RuntimeException {
        if(accepterFactory == null)
            throw new RuntimeException("null accepter factory");
        for (Map.Entry<URI, Map<Object,Object>> entry : result.entrySet()) {
            for (Object o : entry.getValue().keySet()) {
                if (o instanceof URI) {
                    element.accept(entry.getKey(), (URI) o, lingOnto, accepterFactory);
                } else if (o instanceof String) {
                    element.accept(entry.getKey(), (String) o, lingOnto, accepterFactory);
                } else if (o instanceof StringLang) {
                    final StringLang sl = (StringLang) o;
                    element.accept(entry.getKey(), sl.value, sl.lang, lingOnto, accepterFactory);
                } else if (o instanceof TripleNode) {
                    final TripleNode node = (TripleNode) o;
                    final ReaderAccepter accepter;
                    if(node.resource instanceof URI) {
                        accepter = element.accept(entry.getKey(), (URI)node.resource, lingOnto, accepterFactory);
                    } else {
                        accepter = element.accept(entry.getKey(), (String)node.resource, lingOnto, accepterFactory);
                    }
                    if(accepter != null) {
                        insertTriples(node.triples, accepter, accepterFactory);
                    }
                } else {
                    throw new RuntimeException(o.toString());
                }
            }
        }
    }

    @Override
    public void resolveRemoteFiltered(LemonModelImpl model, URI property, LemonElementImpl<?> element) {
        final AccepterFactory accepterFactory = new AccepterFactory(accepters, lingOnto, model);
        final Map<URI, Map<Object,Object>> result = sparqlFiltered(element.getURI(), element.getID(), property);
        insertTriples(result, element, accepterFactory);
    }

    @Override
    public <T> List<T> resolveRemoteList(Object identifier, Class<T> clazz, LemonModelImpl model) {
        return new SPARQLList<T>(identifier,clazz,model);
    }

    @Override
    public int resolveRemoteEntryCount(LemonModelImpl model, LexiconImpl lexicon) {
        try {
            final StringBuilder query = new StringBuilder("SELECT DISTINCT ?entry");
            for (URI graph : graphs) {
                query.append(" FROM <").append(graph.toString()).append(">");
            }
            query.append(" WHERE { <").append(lexicon.getURI().toString()).append("> <http://www.monnet-project.eu/lemon#entry> ?entry }");
            final URL queryURL = new URL(endpoint + "?query=" + URLEncoder.encode(query.toString(), "UTF-8"));
            final URLConnection connection = queryURL.openConnection();
            connection.setRequestProperty("Accept", "application/sparql-results+xml");
            final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            final DocumentBuilder db = dbf.newDocumentBuilder();
            final InputStream in = connection.getInputStream();
            final Document document = db.parse(in);
            in.close();
            final NodeList resultsTags = document.getElementsByTagName("result");
            return resultsTags.getLength();
        } catch (Exception x) {
            throw new RuntimeException(x);
        }
    }
    
    

    @Override
    public Set<URI> getLexica() {
        try {
            final StringBuilder query = new StringBuilder("SELECT *");
            for (URI graph : graphs) {
                query.append(" FROM <").append(graph.toString()).append(">");
            }
            query.append(" WHERE { ?lexicon a <http://www.monnet-project.eu/lemon#Lexicon> }");
            final URL queryURL = new URL(endpoint + "?query=" + URLEncoder.encode(query.toString(), "UTF-8"));
            final URLConnection connection = queryURL.openConnection();
            connection.setRequestProperty("Accept", "application/sparql-results+xml");
            final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            final DocumentBuilder db = dbf.newDocumentBuilder();
            final InputStream in = connection.getInputStream();
            final Document document = db.parse(in);
            in.close();
            final Set<URI> lexicaURIs = new HashSet<URI>();
            final NodeList resultsTags = document.getElementsByTagName("result");
            for (int i = 0; i < resultsTags.getLength(); i++) {
                final Node node = resultsTags.item(i);
                if (node instanceof Element) {
                    final Element element = (Element) node;
                    final NodeList resultTags = element.getElementsByTagName("binding");
                    for (int j = 0; j < resultTags.getLength(); j++) {
                        final Element resultElem = (Element) resultTags.item(j);
                        final String varName = resultElem.getAttribute("name");
                        if (varName == null) {
                            throw new RuntimeException("SPARQL results had <binding> without name attribute");
                        } else if (varName.equals("lexicon")) {
                            final Object r = readResult(resultElem);
                            if (r instanceof URI) {
                                lexicaURIs.add((URI) r);
                            } else {
                                throw new RuntimeException("SPARQL results had non-URI tag as predicate");
                            }
                        } else {
                            throw new RuntimeException("Unexpected variable name " + varName);
                        }
                    }
                }
            }
            return lexicaURIs;
        } catch (Exception x) {
            throw new RuntimeException(x);
        }
    }

    private void buildDeepQuery(StringBuilder builder, int depth, int max) {
        if (depth < max) {
            builder.append("optional { ?o").append(depth - 1).append(" ?p").append(depth).append(" ?o").append(depth).append(" . ");
            buildDeepQuery(builder, depth + 1, max);
            builder.append("} ");
        }
    }

    private Map<URI, Map<Object, Object>> sparqlAll(URI uri, String bNodeId, int depth) {
        try {
            final StringBuilder query = new StringBuilder("SELECT *");
            for (URI graph : graphs) {
                query.append(" FROM <").append(graph.toString()).append(">");
            }
            query.append(" WHERE { ");
            if (uri != null) {
                query.append("<").append(uri.toString()).append(">");
            } else {
                query.append("filter(str(?x) = \"_:").append(bNodeId).append("\"). ?x");
            }
            query.append(" ?p0 ?o0 ");
            buildDeepQuery(query, 1, depth);
            query.append("}");
            final URL queryURL = new URL(endpoint + "?query=" + URLEncoder.encode(query.toString(), "UTF-8"));
            System.err.println(query.toString());
            final URLConnection connection = queryURL.openConnection();
            connection.setRequestProperty("Accept", "application/sparql-results+xml");
            final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            final DocumentBuilder db = dbf.newDocumentBuilder();
            final InputStream in = connection.getInputStream();
            final Document document = db.parse(in);
            in.close();
            final Map<URI, Map<Object, Object>> result = new HashMap<URI, Map<Object, Object>>();
            final NodeList resultsTags = document.getElementsByTagName("result");
            for (int i = 0; i < resultsTags.getLength(); i++) {
                final Node node = resultsTags.item(i);
                if (node instanceof Element) {
                    final Element element = (Element) node;
                    final NodeList resultTags = element.getElementsByTagName("binding");
                    URI[] pred = new URI[depth];
                    Object[] value = new Object[depth];
                    for (int j = 0; j < resultTags.getLength(); j++) {
                        final Element resultElem = (Element) resultTags.item(j);
                        final String varName = resultElem.getAttribute("name");
                        final Matcher matcher = POEXTRACTOR.matcher(varName);
                        if (varName == null || (!matcher.matches() && !varName.equals("x"))) {
                            throw new RuntimeException("SPARQL results had <binding> without name attribute " + varName + " " + query);
                        } else if (varName.matches("p\\d+")) {
                            final int idx = Integer.parseInt(matcher.group(1));
                            final Object r = readResult(resultElem);
                            if (r instanceof URI) {
                                pred[idx] = (URI) r;
                            } else {
                                throw new RuntimeException("SPARQL results had non-URI tag as predicate");
                            }
                        } else if (varName.matches("o\\d+")) {
                            final int idx = Integer.parseInt(matcher.group(1));
                            value[idx] = readResult(resultElem);
                        } else if(varName.equals("x")) {
                            // no op
                        } else {
                            throw new RuntimeException("Unexpected variable name " + varName);
                        }
                    }
                    if (pred[0] == null || value[0] == null) {
                        throw new RuntimeException("Query results lacked predicate or object");
                    }
                    Map<URI, Map<Object, Object>> map = result;
                    for (int d = 0; d < depth && pred[d] != null; d++) {
                        if (!map.containsKey(pred[d])) {
                            map.put(pred[d], new HashMap<Object, Object>());
                        }
                        if (d + 1 == depth || pred[d + 1] == null) {
                            map.get(pred[d]).put(value[d], value[d]);
                        } else {
                            if(value[d] == null) {
                                throw new RuntimeException("Pred without value!");
                            }
                            TripleNode tripleNode = new TripleNode(value[d]);
                            if (map.get(pred[d]).containsKey(tripleNode)) {
                                tripleNode = (TripleNode) map.get(pred[d]).get(tripleNode);
                            } else {
                                map.get(pred[d]).put(tripleNode,tripleNode);
                            }
                            map = tripleNode.triples;
                        }
                    }
                }
            }
            return result;
        } catch (Exception x) {
            throw new RuntimeException(x);
        }
    }

    private Map<URI, Map<Object,Object>> sparqlFiltered(URI uri, String bNodeId, URI property) {
        try {
            final StringBuilder query = new StringBuilder("SELECT *");
            for (URI graph : graphs) {
                query.append(" FROM <").append(graph.toString()).append(">");
            }
            query.append(" WHERE { ");
            if (uri != null) {
                query.append("<").append(uri.toString()).append(">");
            } else {
                query.append("filter(str(?x) = \"_:").append(bNodeId).append("\"). ?x");
            }
            query.append(" <").append(property).append("> ?o }");
            final URL queryURL = new URL(endpoint + "?query=" + URLEncoder.encode(query.toString(), "UTF-8"));
            System.err.println(query.toString());
            final URLConnection connection = queryURL.openConnection();
            connection.setRequestProperty("Accept", "application/sparql-results+xml");
            final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            final DocumentBuilder db = dbf.newDocumentBuilder();
            final InputStream in = connection.getInputStream();
            final Document document = db.parse(in);
            in.close();
            final Map<URI, Map<Object,Object>> result = new HashMap<URI, Map<Object,Object>>();
            final NodeList resultsTags = document.getElementsByTagName("result");
            for (int i = 0; i < resultsTags.getLength(); i++) {
                final Node node = resultsTags.item(i);
                if (node instanceof Element) {
                    final Element element = (Element) node;
                    final NodeList resultTags = element.getElementsByTagName("binding");
                    Object value = null;
                    for (int j = 0; j < resultTags.getLength(); j++) {
                        final Element resultElem = (Element) resultTags.item(j);
                        final String varName = resultElem.getAttribute("name");
                        if (varName == null) {
                            throw new RuntimeException("SPARQL results had <binding> without name attribute");
                        } else if (varName.equals("o")) {
                            value = readResult(resultElem);
                        } else if (varName.equals("x")) {
                            // no op
                        } else {
                            throw new RuntimeException("Unexpected variable name " + varName);
                        }
                    }
                    if (value == null) {
                        throw new RuntimeException("Query results lacked predicate or object");
                    }
                    if (!result.containsKey(property)) {
                        result.put(property, new HashMap<Object,Object>());
                    }
                    result.get(property).put(value,value);
                }
            }
            return result;
        } catch (Exception x) {
            throw new RuntimeException(x);
        }
    }

    private Object readResult(Element resultTag) {
        final NodeList childNodes = resultTag.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            final Node child = childNodes.item(i);
            if (child instanceof Element) {
                final Element c = (Element) child;
                if (c.getTagName().equals("uri")) {
                    if(c.getTextContent().startsWith("_:")) {
                        return c.getTextContent().substring(2);
                    } else {
                        return URI.create(c.getTextContent());
                    }
                } else if (c.getTagName().equals("literal")) {
                    if (c.getAttribute("xml:lang") != null) {
                        return new StringLang(c.getTextContent(), c.getAttribute("xml:lang"));
                    } else {
                        return new StringLang(c.getTextContent(), null);
                    }
                } else if (c.getTagName().equals("bnode")) {
                    if (c.getTextContent() != null && c.getTextContent().startsWith("nodeID://")) {
                        // Virtuoso does this... for various reasons it is now better to treat this like
                        // it was the URI all along
                        return URI.create(c.getTextContent().substring(9, c.getTextContent().length()));
                    } else {
                        return c.getTextContent();
                    }
                } else {
                    throw new RuntimeException("Unexpected tag in binding " + c);
                }
            }
        }
        throw new RuntimeException("No tag in result set");
    }

    @SuppressWarnings("unchecked")
    public <Elem extends LemonElementOrPredicate> Iterator<Elem> query(Class<Elem> target, String query, LemonModelImpl model) throws IOException, ParserConfigurationException, SAXException {
        System.err.println(query);
        final URL queryURL = new URL(endpoint + "?query=" + URLEncoder.encode(query.toString(), "UTF-8"));
        final URLConnection connection = queryURL.openConnection();
        connection.setRequestProperty("Accept", "application/sparql-results+xml");
        final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        final DocumentBuilder db = dbf.newDocumentBuilder();
        final InputStream in = connection.getInputStream();
        final Document document = db.parse(in);
        in.close();
        final NodeList resultsTags = document.getElementsByTagName("result");
        final HashSet<Elem> elems = new HashSet<Elem>();
        for (int i = 0; i < resultsTags.getLength(); i++) {
            final Node node = resultsTags.item(i);
            if (node instanceof Element) {
                final Element element = (Element) node;
                final NodeList resultTags = element.getElementsByTagName("binding");
                for (int j = 0; j < resultTags.getLength(); j++) {
                    final Element resultElem = (Element) resultTags.item(j);
                    final Object result = readResult(resultElem);
                    if (result instanceof URI) {
                        elems.add(make(target, (URI) result, model));
                    } else {
                        elems.add(make(target, result.toString(), model));
                    }
                }
            }
        }
        return elems.iterator();
    }

    @SuppressWarnings("unchecked")
    private static <E> E make(Class<E> clazz, URI uri, LemonModelImpl model) {
        if (clazz.equals(Argument.class)) {
            return (E) new ArgumentImpl(uri, model);
        } else if (clazz.equals(Component.class)) {
            return (E) new ComponentImpl(uri, model);
        } else if (clazz.equals(Condition.class)) {
            return (E) new ConditionImpl(uri, model);
        } else if (clazz.equals(Constituent.class)) {
            return (E) new ConstituentImpl(uri, model);
        } else if (clazz.equals(SenseContext.class)) {
            return (E) new ContextImpl(uri, model);
        } else if (clazz.equals(Definition.class)) {
            return (E) new DefinitionImpl(uri, model);
        } else if (clazz.equals(Example.class)) {
            return (E) new ExampleImpl(uri, model);
        } else if (clazz.equals(LexicalForm.class)) {
            return (E) new FormImpl(uri, model);
        } else if (clazz.equals(Frame.class)) {
            return (E) new FrameImpl(uri, model);
        } else if (clazz.equals(LexicalEntry.class)) {
            return (E) new LexicalEntryImpl(uri, model);
        } else if (clazz.equals(LexicalSense.class)) {
            return (E) new LexicalSenseImpl(uri, model);
        } else if (clazz.equals(MorphPattern.class)) {
            return (E) new MorphPatternImpl(uri, model);
        } else if (clazz.equals(MorphTransform.class)) {
            return (E) new MorphTransformImpl(uri, model);
        } else if (clazz.equals(Node.class)) {
            return (E) new NodeImpl(uri, model);
        } else if (clazz.equals(Part.class)) {
            return (E) new PartImpl(uri, model);
        } else if (clazz.equals(Phrase.class)) {
            return (E) new PhraseImpl(uri, model);
        } else if (clazz.equals(Prototype.class)) {
            return (E) new PrototypeImpl(uri, model);
        } else if (clazz.equals(LexicalTopic.class)) {
            return (E) new TopicImpl(uri, model);
        } else if (clazz.equals(Word.class)) {
            return (E) new WordImpl(uri, model);
        } else if (clazz.equals(Lexicon.class)) {
            return (E) new LexiconImpl(uri, model);
        } else {
            //return null;
            throw new RuntimeException("Unknown type");
        }
    }

    @SuppressWarnings("unchecked")
    private static <E> E make(Class<E> clazz, String bNode, LemonModelImpl model) {
        if (clazz.equals(Argument.class)) {
            return (E) new ArgumentImpl(bNode, model);
        } else if (clazz.equals(Component.class)) {
            return (E) new ComponentImpl(bNode, model);
        } else if (clazz.equals(Condition.class)) {
            return (E) new ConditionImpl(bNode, model);
        } else if (clazz.equals(Constituent.class)) {
            return (E) new ConstituentImpl(bNode, model);
        } else if (clazz.equals(SenseContext.class)) {
            return (E) new ContextImpl(bNode, model);
        } else if (clazz.equals(Definition.class)) {
            return (E) new DefinitionImpl(bNode, model);
        } else if (clazz.equals(Example.class)) {
            return (E) new ExampleImpl(bNode, model);
        } else if (clazz.equals(LexicalForm.class)) {
            return (E) new FormImpl(bNode, model);
        } else if (clazz.equals(Frame.class)) {
            return (E) new FrameImpl(bNode, model);
        } else if (clazz.equals(LexicalEntry.class)) {
            return (E) new LexicalEntryImpl(bNode, model);
        } else if (clazz.equals(LexicalSense.class)) {
            return (E) new LexicalSenseImpl(bNode, model);
        } else if (clazz.equals(MorphPattern.class)) {
            return (E) new MorphPatternImpl(bNode, model);
        } else if (clazz.equals(MorphTransform.class)) {
            return (E) new MorphTransformImpl(bNode, model);
        } else if (clazz.equals(Node.class)) {
            return (E) new NodeImpl(bNode, model);
        } else if (clazz.equals(Part.class)) {
            return (E) new PartImpl(bNode, model);
        } else if (clazz.equals(Phrase.class)) {
            return (E) new PhraseImpl(bNode, model);
        } else if (clazz.equals(Prototype.class)) {
            return (E) new PrototypeImpl(bNode, model);
        } else if (clazz.equals(LexicalTopic.class)) {
            return (E) new TopicImpl(bNode, model);
        } else if (clazz.equals(Word.class)) {
            return (E) new WordImpl(bNode, model);
        } else {
            //return null;
            throw new RuntimeException("Unknown type");


        }
    }

    private static class TripleNode {

        public final Object resource;
        public final Map<URI, Map<Object, Object>> triples = new HashMap<URI, Map<Object, Object>>();

        public TripleNode(Object resource) {
            this.resource = resource;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final TripleNode other = (TripleNode) obj;
            if (this.resource != other.resource && (this.resource == null || !this.resource.equals(other.resource))) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hash = 3;
            hash = 89 * hash + (this.resource != null ? this.resource.hashCode() : 0);
            return hash;
        }
    }

    private static class StringLang {

        public final String value, lang;

        public StringLang(String value, String lang) {
            this.value = value;
            this.lang = lang;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final StringLang other = (StringLang) obj;
            if ((this.value == null) ? (other.value != null) : !this.value.equals(other.value)) {
                return false;
            }
            if ((this.lang == null) ? (other.lang != null) : !this.lang.equals(other.lang)) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 73 * hash + (this.value != null ? this.value.hashCode() : 0);
            hash = 73 * hash + (this.lang != null ? this.lang.hashCode() : 0);
            return hash;
        }
    }

    private class SPARQLList<T> extends AbstractList<T> {

        private final LinkedList<T> resolved = new LinkedList<T>();
        private final Class<T> clazz;
        private final LemonModelImpl model;
        private boolean isComplete = false;
        private Object head;

        public SPARQLList(Object head, Class<T> clazz, LemonModelImpl model) {
            this.head = head;
            this.clazz = clazz;
            this.model = model;
        }

        @Override
        public T get(int index) {
            if (index < resolved.size()) {
                return resolved.get(index);
            } else {
                while (!isComplete && index >= resolved.size()) {
                    advance();
                }
                return resolved.get(index);
            }
        }

        @Override
        public int size() {
            while (!isComplete) {
                advance();
            }
            return resolved.size();
        }

        @Override
        public Iterator<T> iterator() {
            return new Iterator<T>() {

                int i = 0;

                @Override
                public boolean hasNext() {
                    return i < resolved.size() || (i == resolved.size() && advance());
                }

                @Override
                public T next() {
                    while (!isComplete && i >= resolved.size()) {
                        advance();
                    }
                    return resolved.get(i++);
                }

                @Override
                public void remove() {
                    throw new UnsupportedOperationException("SPARQL remove by list modification not supported.");
                }
            };
        }

        private boolean advance() {
            if (isComplete) {
                return false;
            }
            try {
                final StringBuilder query = new StringBuilder("SELECT *");
                for (URI graph : graphs) {
                    query.append(" FROM <").append(graph.toString()).append(">");
                }
                query.append(" WHERE {  ?x <http://www.w3.org/1999/02/22-rdf-syntax-ns#first> ?value ; <http://www.w3.org/1999/02/22-rdf-syntax-ns#rest> ?next ."
                        + " filter(str(?x) = \"");
                if (head instanceof URI) {
                    query.append(head);
                } else {
                    query.append("_:").append(head);
                }
                query.append("\") }");
                System.err.println(query);
                final URL queryURL = new URL(endpoint + "?query=" + URLEncoder.encode(query.toString(), "UTF-8"));
                final URLConnection connection = queryURL.openConnection();
                connection.setRequestProperty("Accept", "application/sparql-results+xml");
                final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                final DocumentBuilder db = dbf.newDocumentBuilder();
                final InputStream in = connection.getInputStream();
                final Document document = db.parse(in);
                in.close();
                final NodeList resultsTags = document.getElementsByTagName("result");
                Object next = null;
                Object value = null;
                for (int i = 0; i < resultsTags.getLength(); i++) {
                    final Node node = resultsTags.item(i);
                    if (node instanceof Element) {
                        final Element element = (Element) node;
                        final NodeList resultTags = element.getElementsByTagName("binding");
                        for (int j = 0; j < resultTags.getLength(); j++) {
                            final Element resultElem = (Element) resultTags.item(j);
                            final String varName = resultElem.getAttribute("name");
                            if (varName == null) {
                                throw new RuntimeException("SPARQL results had <binding> without name attribute");
                            } else if (varName.equals("next")) {
                                next = readResult(resultElem);
                            } else if (varName.equals("value")) {
                                value = readResult(resultElem);
                            } else if (varName.equals("x")) {
                                // noop
                            } else {
                                throw new RuntimeException("Unexpected variable name " + varName);
                            }
                        }
                    }
                    if(next != null && value != null) {
                        break;
                    }
                }

                if (next == null || value == null) {
                    isComplete = true;
                    return false;
                } else {
                    if (value instanceof URI && ((URI) value).toString().equals("http://www.w3.org/1999/02/22-rdf-syntax-ns#nil")) {
                        isComplete = true;
                        return false;
                    } else {
                        if(value instanceof URI) {
                            resolved.add(make(clazz, (URI)value, model));
                        } else {
                            resolved.add(make(clazz, (String)value, model));
                        }
                        head = next;
                        return true;
                    }
                }
            } catch (Exception x) {
                throw new RuntimeException(x);
            }
        }
    }
}
