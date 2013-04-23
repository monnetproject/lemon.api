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
package eu.monnetproject.lemon.conversions.lmf;

/**
 *
 * @author John McCrae
 */
import eu.monnetproject.lemon.LemonModel;
import eu.monnetproject.lemon.LemonSerializer;
import eu.monnetproject.lemon.model.*;
import java.io.FileReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class LemonLMFConverter {

    public static final String GEOGRAPHICAL_VARIANT = "geographicalVariant";
    public static final String ORTHOGRAPHY_NAME = "orthographyName";
    public static final String SCRIPT = "script";
    public static final String WRITTEN_FORM = "writtenForm";

    public static Document lemon2lmf(LemonModel model) {
        try {
            if(model.getLexica().isEmpty()) {
                throw new IllegalArgumentException("Empty model");
            }
            return new toLMF().lemon2lmf(model);
        } catch (Exception x) {
            throw new RuntimeException(x);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////
    // Lemon to LMF
    private static class toLMF {

        private final Document document;
        private final Element root;

        public toLMF() throws ParserConfigurationException {
            document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            root = document.createElement("LexicalResource");
            root.setAttribute("dtdVersion", "16");
            document.appendChild(root);
        }

        private String toLMFValue(LemonElementOrPredicate elem, LemonModel model) {
            if (elem.getURI() != null) {
                if (elem.getURI().getFragment() != null) {
                    return elem.getURI().getFragment();
                } else {
                    return elem.getURI().toString().substring(elem.getURI().toString().lastIndexOf("/"));
                }
            } else {
                return ((LemonElement) elem).getID();
            }
        }

        public Document lemon2lmf(LemonModel model) {
            append(root, header(model));
            for (Lexicon lexicon : model.getLexica()) {
                append(root, lexicon2lmf(lexicon, model));
            }
            return document;
        }

        private Node header(LemonModel model) {
            final Element globalInfo = document.createElement("GlobalInformation");
            final Lexicon lexicon = model.getLexica().iterator().next();
            if(lexicon.getURI() != null) {
                append(globalInfo, getFeat("label", "LMF Lexicon derived from lemon model " + lexicon.getURI().toString()));
            } else {
                append(globalInfo, getFeat("label", "LMF Lexicon derived from lemon model"));
            }
            return globalInfo;
        }

        private void append(Node head, Node to) {
            head.appendChild(to);
        }

        private void append(Node head, List<Node> tos) {
            for (Node to : tos) {
                head.appendChild(to);
            }
        }

        private Node lexicon2lmf(Lexicon lexicon, LemonModel model) {
            final Element lexiconTag = document.createElement("Lexicon");
            append(lexiconTag, getFeat("language", lexicon.getLanguage()));
            for (LexicalEntry entry : lexicon.getEntrys()) {
                append(lexiconTag, lexEntry2lmf(entry, model));
                append(lexiconTag, senses2spc(lexicon, model));
                append(lexiconTag, frames2lmf(lexicon, model));
            }
            return lexiconTag;
        }

        private List<Node> lexEntry2lmf(LexicalEntry lexEntry, LemonModel model) {
            final Element lexEntryTag = document.createElement("LexicalEntry");
            append(lexEntryTag, getFeats(lexEntry.getPropertys(), model));
            append(lexEntryTag, getFeat("lexEntryType", lexEntry.getTypes().iterator().next().toString()));
            append(lexEntryTag, canForm2lmf(lexEntry.getCanonicalForm(), model));
            append(lexEntryTag, forms2lmf(lexEntry, model));
            append(lexEntryTag, abstractForms2lmf(lexEntry, model));
            append(lexEntryTag, compList2lmf(lexEntry, model));
            append(lexEntryTag, senses2lmf(lexEntry, model));
            append(lexEntryTag, synBeh2lmf(lexEntry, model));

            LinkedList<Node> tags = new LinkedList<Node>();
            tags.add(lexEntryTag);
            tags.addAll(mweLink2lmf(lexEntry, model));
            tags.addAll(mwe2lmf(lexEntry, model));
            // TODO: topics
            // TODO: lexical variants
            return tags;
        }

        private Element canForm2lmf(LexicalForm form, LemonModel model) {
            final Element lemmaTag = document.createElement("Lemma");
            append(lemmaTag, getFeat(WRITTEN_FORM, form.getWrittenRep().value));
            return lemmaTag;
        }

        private List<Node> forms2lmf(LexicalEntry lexEntry, LemonModel model) {
            List<Node> rval = new LinkedList<Node>();
            rval.add(form2lmf(lexEntry.getCanonicalForm(), model));
            for (LexicalForm form : lexEntry.getOtherForms()) {
                rval.add(form2lmf(form, model));
            }
            return rval;
        }

        private Node form2lmf(LexicalForm form, LemonModel model) {
            final Element formTag = document.createElement("WordForm");
            append(formTag, getFeat(WRITTEN_FORM, form.getWrittenRep().value));
            append(formTag, getFeats(form.getPropertys(), model));
            append(formTag, reps2formRep(form.getRepresentations(), model));
            return formTag;
        }

        private List<Node> abstractForms2lmf(LexicalEntry lexEntry, LemonModel model) {
            List<Node> nodes = new LinkedList<Node>();
            for (LexicalForm form : lexEntry.getAbstractForms()) {
                final Element stemTag = document.createElement("Stem");
                append(stemTag, getFeat(WRITTEN_FORM, form.getWrittenRep().value));
                append(stemTag, getFeats(form.getPropertys(), model));
                append(stemTag, reps2formRep(form.getRepresentations(), model));
                nodes.add(stemTag);
            }

            // TODO : form variants
            return nodes;
        }

        private List<Node> reps2formRep(Map<Representation, Collection<Text>> repMap, LemonModel model) {
            List<Node> nodes = new LinkedList<Node>();
            for (Representation rep : repMap.keySet()) {
                for (Text value : repMap.get(rep)) {
                    final Element formRepTag = document.createElement("FormRepresentation");
                    append(formRepTag, getFeat(WRITTEN_FORM, value.value));
                    append(formRepTag, decodeLangTag(value.language));
                    nodes.add(formRepTag);
                }
            }
            return nodes;
        }

        private List<Node> decodeLangTag(String langTag) {
            final String ieft = "([A-Za-z]{2,3})(-[A-Za-z]{4})?(-[A-Za-z]{2}|-[0-9]{3})?(-[A-Za-z]{5,8}|-[0-9]\\w{3})?(-\\w-\\w{2,8})?";
            final Matcher matcher = Pattern.compile(ieft).matcher(langTag);
            if (!matcher.matches()) {
                throw new IllegalArgumentException("Bad lang tag " + langTag);
            }
            final String lang = matcher.group(1);
            final String script = matcher.group(2);
            final String region = matcher.group(3);
            final String variant = matcher.group(4);
            final String extension = matcher.group(5);
            final List<Node> langTags = new LinkedList<Node>();

            if (script != null) {
                langTags.add(getFeat(SCRIPT, script.substring(1)));
            }

            if (region != null) {
                langTags.add(getFeat(GEOGRAPHICAL_VARIANT, region.substring(1)));
            }
            if (variant != null) {
                langTags.add(getFeat("variant", variant.substring(1)));
            }
            if (extension != null) {
                langTags.add(getFeat(ORTHOGRAPHY_NAME, extension.substring(3)));
            }
            return langTags;
        }

        private List<Node> compList2lmf(LexicalEntry lexEntry, LemonModel model) {
            if (lexEntry.getDecompositions().isEmpty()) {
                return Collections.EMPTY_LIST;
            } else {
                final Node loc = document.createElement("ListOfComponents");
                append(loc, getFeat("size", "" + lexEntry.getDecompositions().iterator().next().size()));
                append(loc, comp2lmf(lexEntry.getDecompositions().iterator().next(), model, 1));
                return Collections.singletonList(loc);
            }
        }

        private List<Node> comp2lmf(List<Component> compList, LemonModel model, int order) {
            if (compList.isEmpty()) {
                return Collections.EMPTY_LIST;
            } else {
                final Element compTag = document.createElement("Component");
                append(compTag, getFeat("order", "" + order));
                append(compTag, getFeats(compList.get(0).getPropertys(), model));
                compTag.setAttribute("entry", frag(compList.get(0).getElement(), model));
                List<Node> nodes = new LinkedList<Node>();
                nodes.add(compTag);
                nodes.addAll(comp2lmf(compList.subList(1, compList.size()), model, order + 1));
                return nodes;
            }
        }

        private List<Node> senses2lmf(LexicalEntry lexEntry, LemonModel model) {
            List<Node> nodes = new LinkedList<Node>();
            for (LexicalSense sense : lexEntry.getSenses()) {
                nodes.add(sense2lmf(sense, model));
            }
            return nodes;
        }

        private Node sense2lmf(LexicalSense sense, LemonModel model) {
            final Element senseTag = document.createElement("Sense");
            final Element refTag = document.createElement("MonolingualExternalRef");

            append(refTag, getFeat("externalSystem", "application/rdf"));
            append(refTag, getFeat("externalReference", sense.getReference().toString()));
            append(senseTag, refTag);

            append(senseTag, getFeat("refPref", sense.getRefPref().toString()));
            // TODO: context
            // TODO: condition
            for (LexicalSense subsense : sense.getSubsenses()) {
                append(senseTag, sense2lmf(sense, model));
            }
            append(senseTag, examples2lmf(sense.getExamples(), model));
            append(senseTag, defs2lmf(sense.getDefinitions().values(), model));
            append(senseTag, semArgs2pr(sense, model));
            append(senseTag, getFeats(sense.getPropertys(), model));
            return senseTag;
        }

        private List<Node> senseRelations2lmf(LemonModel model) {
            // TODO: sense relations
            return Collections.EMPTY_LIST;
        }

        private List<Node> examples2lmf(Collection<Example> examples, LemonModel model) {
            final LinkedList<Node> nodes = new LinkedList<Node>();

            for (Example example : examples) {
                final Element exampleTag = document.createElement("SenseExample");
                append(exampleTag, texts2textRep(example.getValue()));
                append(exampleTag, getFeats(example.getPropertys(), model));
                nodes.add(exampleTag);
            }
            return nodes;
        }

        private List<Node> defs2lmf(Collection<Collection<SenseDefinition>> defss, LemonModel model) {
            final LinkedList<Node> nodes = new LinkedList<Node>();
            for (Collection<SenseDefinition> defs : defss) {
                for (SenseDefinition defn : defs) {
                    final Element defTag = document.createElement("Definition");
                    append(defTag, texts2textRep(defn.getValue()));
                    append(defTag, getFeats(defn.getPropertys(), model));
                    nodes.add(defTag);
                }
            }
            return nodes;
        }

        private List<Node> texts2textRep(Collection<Text> reps) {
            final LinkedList<Node> nodes = new LinkedList<Node>();
            for (Text rep : reps) {
                Element repTag = texts2textRep(rep);
                nodes.add(repTag);
            }
            return nodes;
        }

        private Element texts2textRep(Text rep) throws DOMException {
            final Element repTag = document.createElement("TextRepresentation");
            append(repTag, getFeat(WRITTEN_FORM, rep.value));
            append(repTag, decodeLangTag(rep.language));
            return repTag;
        }

        private List<Node> semArgs2pr(LexicalSense sense, LemonModel model) {
            if (!sense.getSubjOfProps().isEmpty()
                    || !sense.getObjOfProps().isEmpty()
                    || !sense.getIsAs().isEmpty()) {
                final Element tag = document.createElement("PredicativeRepresentation");
                tag.setAttribute("predicate", ("__predicate_" + frag(sense, model)));
                tag.setAttribute("correspondences", ("__correspondence_" + frag(sense, model)));
                return Collections.singletonList((Node) tag);
            } else {
                return Collections.EMPTY_LIST;
            }
        }

        private List<Node> senses2spc(Lexicon lexicon, LemonModel model) {
            List<Node> nodes = new LinkedList<Node>();
            for (LexicalEntry entry : lexicon.getEntrys()) {
                for (LexicalSense sense : entry.getSenses()) {
                    if (!sense.getSubjOfProps().isEmpty() || !sense.getObjOfProps().isEmpty() || !sense.getIsAs().isEmpty()) {
                        final Element semPredTag = document.createElement("SemanticPredicate");
                        final Element sscTag = document.createElement("SynSemCorrespondence");
                        for (Argument arg : sense.getSubjOfProps()) {
                            final Element semArgTag = document.createElement("SemanticArgument");
                            append(semArgTag, getFeat("label", "subjOfProp"));
                            append(semArgTag, getFeat("id", "__subjOfProp_" + frag(sense, model)));
                            append(semPredTag, semArgTag);
                            final Element ssamTag = document.createElement("SynSemArgMap");
                            ssamTag.setAttribute("semArg", "__subjOfProp_" + frag(sense, model));
                            ssamTag.setAttribute("synArg", frag(arg, model));
                            append(sscTag, ssamTag);
                        }

                        for (Argument arg : sense.getObjOfProps()) {
                            final Element semArgTag = document.createElement("SemanticArgument");
                            append(semArgTag, getFeat("label", "objOfProp"));
                            append(semArgTag, getFeat("id", "__objOfProp_" + frag(sense, model)));
                            append(semPredTag, semArgTag);
                            final Element ssamTag = document.createElement("SynSemArgMap");
                            ssamTag.setAttribute("semArg", "__subjOfProp_" + frag(sense, model));
                            ssamTag.setAttribute("synArg", frag(arg, model));
                            append(sscTag, ssamTag);
                        }

                        for (Argument arg : sense.getIsAs()) {
                            final Element semArgTag = document.createElement("SemanticArgument");
                            append(semArgTag, getFeat("label", "isA"));
                            append(semArgTag, getFeat("id", "__isA_" + frag(sense, model)));
                            append(semPredTag, semArgTag);
                            final Element ssamTag = document.createElement("SynSemArgMap");
                            ssamTag.setAttribute("semArg", "__subjOfProp_" + frag(sense, model));
                            ssamTag.setAttribute("synArg", frag(arg, model));
                            append(sscTag, ssamTag);
                        }
                        semPredTag.setAttribute("id", ("__predicate_" + frag(sense, model)));
                        nodes.add(semPredTag);
                        sscTag.setAttribute("id", "__correspondence_" + frag(sense, model));
                        nodes.add(sscTag);
                    }
                }
            }
            return nodes;
        }

        private List<Node> synBeh2lmf(LexicalEntry lexEntry, LemonModel model) {
            List<Node> nodes = new LinkedList<Node>();
            if (lexEntry.getSynBehaviors().isEmpty()) {
                return Collections.EMPTY_LIST;
            } else {
                final Element sbTag = document.createElement("SyntacticBehavior");
                final StringBuilder builder = new StringBuilder();
                for (Frame frame : lexEntry.getSynBehaviors()) {
                    builder.append(frag(frame, model)).append(" ");
                }
                sbTag.setAttribute("subcategorizationFrames", builder.toString().trim());
                return Collections.singletonList((Node) sbTag);
            }
            // TODO: subcat frame sets
        }

        private List<Node> frames2lmf(Lexicon lexicon, LemonModel model) {
            final LinkedList<Node> nodes = new LinkedList<Node>();
            for (LexicalEntry entry : lexicon.getEntrys()) {
                for (Frame frame : entry.getSynBehaviors()) {
                    final Element sfTag = document.createElement("SubcategorizationFrame");
                    append(sfTag, args2lmf(frame, model));
                    sfTag.setAttribute("id", frag(frame, model));
                    nodes.add(sfTag);
                }
            }
            return nodes;
        }

        private List<Node> args2lmf(Frame frame, LemonModel model) {
            final LinkedList<Node> nodes = new LinkedList<Node>();
            for (SynArg synArg : frame.getSynArgs().keySet()) {
                for (Argument arg : frame.getSynArgs().get(synArg)) {
                    final Element synArgTag = document.createElement("SyntacticArgument");
                    append(synArgTag, getFeat("syntacticFunction", toLMFValue(synArg, model)));
                    append(synArgTag, getFeats(frame.getPropertys(), model));
                    append(synArgTag, getArgProps(arg, model));
                    synArgTag.setAttribute("id", frag(arg, model));
                    nodes.add(synArgTag);
                }
            }
            return nodes;
        }

        private List<Node> getArgProps(Argument arg, LemonModel model) {
            if (arg.getMarker() != null) {
                return Collections.singletonList(getFeat("marker", frag(arg.getMarker(), model)));
            } else {
                return Collections.EMPTY_LIST;
            }
        }

        private List<Node> mwe2lmf(LexicalEntry entry, LemonModel model) {
            final LinkedList<Node> nodes = new LinkedList<Node>();
            for (eu.monnetproject.lemon.model.Node root : entry.getPhraseRoots()) {
                final Element patTag = document.createElement("MWEPattern");
                append(patTag, node2lmf(root, model));
                patTag.setAttribute("id", frag(root, model));
                nodes.add(patTag);
            }
            return nodes;
        }

        private List<Node> mweLink2lmf(LexicalEntry lexEntry, LemonModel model) {
            if (lexEntry.getPhraseRoots().isEmpty()) {
                return Collections.EMPTY_LIST;
            } else {
                final StringBuilder sb = new StringBuilder();
                for (eu.monnetproject.lemon.model.Node node : lexEntry.getPhraseRoots()) {
                    sb.append(frag(node, model)).append(" ");
                }
                return Collections.singletonList(getFeat("mwePattern", sb.toString().trim()));
            }
        }

        private Node node2lmf(eu.monnetproject.lemon.model.Node node, LemonModel model) {
            final Element nodeTag = document.createElement("MWENode");
            if (node.getConstituent() != null) {
                append(nodeTag, getFeat("syntacticConstituent", toLMFValue(node.getConstituent(), model)));
            }
            for (Edge edge2 : node.getEdges().keySet()) {
                for (eu.monnetproject.lemon.model.Node node2 : node.getEdge(edge2)) {
                    final Element edgeTag = document.createElement("MWEEdge");
                    append(edgeTag, getFeat("function", toLMFValue(edge2, model)));
                    append(edgeTag, node2lmf(node, model));
                    append(nodeTag, edgeTag);
                }
            }
            if (node.getLeaf() != null) {
                final Element lexTag = document.createElement("MWELex");
                lexTag.setAttribute("target", frag(node.getLeaf(), model));
                append(nodeTag, lexTag);
            }
            if (node.getSeparator() != null) {
                append(nodeTag, getFeat("separator", node.getSeparator().value));
            }
            return nodeTag;
        }

        private <T extends LemonPredicate, U extends LemonElement> List<Node> getFeats(Map<T, Collection<U>> props, LemonModel model) {
            List<Node> rval = new LinkedList<Node>();
            for (T prop : props.keySet()) {
                for (U value : props.get(prop)) {
                    rval.add(getFeat(toLMFValue(prop, model), toLMFValue(value, model)));
                }
            }
            return rval;
        }

        private Node getFeat(String att, String vl) {
            final Element featTag = document.createElement("feat");
            featTag.setAttribute("att", att);
            featTag.setAttribute("val", vl);
            return featTag;
        }
        private final Random rand = new java.util.Random();

        private String frag(LemonElement elem, LemonModel model) {
            if (elem.getURI() != null) {
                return elem.getURI().getFragment();
            } else /*
             * if (elem.getID() != null)
             */ {
                return elem.getID();
            }
        }
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 1 || args.length > 2) {
            System.err.println("Usage:\n\tlemon2lmf lemonFile [lmfFile]");
            return;
        }
        final LemonSerializer serializer = LemonSerializer.newInstance();
        final LemonModel model = serializer.read(new FileReader(args[0]));
        final Document doc = lemon2lmf(model);
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        StreamResult result = args.length == 2 ? new StreamResult(new PrintWriter(args[1])) : new StreamResult(System.out);
        DOMSource source = new DOMSource(doc);
        transformer.transform(source, result);
        if(result.getWriter() != null) {
            result.getWriter().flush();
            result.getWriter().close();
        }
    }
}
