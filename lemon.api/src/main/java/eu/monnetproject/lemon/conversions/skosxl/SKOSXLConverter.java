/**********************************************************************************
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
 *********************************************************************************/
package eu.monnetproject.lemon.conversions.skosxl;

import eu.monnetproject.lemon.LemonModel;
import eu.monnetproject.lemon.LemonModels;
import eu.monnetproject.lemon.LemonSerializer;
import eu.monnetproject.lemon.impl.io.Visitor;
import eu.monnetproject.lemon.impl.io.turtle.TurtleParser;
import eu.monnetproject.lemon.impl.io.xml.RDFXMLReader;
import eu.monnetproject.lemon.model.LexicalEntry;
import eu.monnetproject.lemon.model.LexicalForm;
import eu.monnetproject.lemon.model.LexicalSense;
import eu.monnetproject.lemon.model.Lexicon;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author John McCrae
 */
public class SKOSXLConverter {

    private final static String RDF = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
    private static final String SKOSXL = "http://www.w3.org/2008/05/skos-xl#";
    
    private final static Pattern splitRegex = Pattern.compile("(.*?)(\\w*)");

    public static LemonModel convert(Reader document) {
        final fromSKOSXL fromSKOSXL = new fromSKOSXL();
        try {
            RDFXMLReader reader = new RDFXMLReader(fromSKOSXL);
            reader.parse(document);
        } catch (Exception x) {
            try {
                TurtleParser parser = new TurtleParser(document, fromSKOSXL);
                parser.parse();
            } catch (Exception x2) {
                throw new RuntimeException(x);
            }
        }
        try {
            return fromSKOSXL.toModel();
        } catch (UnsupportedEncodingException x) {
            throw new RuntimeException(x);
        }
    }

    public static Document convert(LemonModel model) {
        try {
            final Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            doc.setXmlStandalone(true);
            final Element root = doc.createElementNS(RDF, "RDF");
            root.setPrefix("rdf");
            doc.appendChild(root);
            for (Lexicon lexicon : model.getLexica()) {
                for (LexicalEntry entry : lexicon.getEntrys()) {
                    for (LexicalSense sense : entry.getSenses()) {
                        for (LexicalForm form : entry.getForms()) {
                            String prefType;
                            if (entry.getAbstractForms().contains(form)) {
                                prefType = "hidden";
                            } else if (sense.getRefPref() == LexicalSense.ReferencePreference.hiddenRef) {
                                prefType = "hidden";
                            } else if (entry.getOtherForms().contains(form)) {
                                prefType = "alt";
                            } else if (sense.getRefPref() == LexicalSense.ReferencePreference.altRef) {
                                prefType = "alt";
                            } else {
                                prefType = "pref";
                            }
                            final Element desc = doc.createElementNS(RDF, "Description");
                            desc.setPrefix("rdf");
                            final Attr descAbout = doc.createAttributeNS(RDF, "about");
                            descAbout.setPrefix("rdf");
                            descAbout.setTextContent(sense.getReference().toString());
                            desc.getAttributes().setNamedItem(descAbout);
                            final Element skosxlLabel = doc.createElementNS(SKOSXL, prefType + "Label");
                            skosxlLabel.setPrefix("skosxl");
                            desc.appendChild(skosxlLabel);
                            final Element desc2 = doc.createElementNS(SKOSXL, "Label");
                            desc2.setPrefix("skosxl");
                            if (entry.getURI() != null) {
                                final Attr desc2about = doc.createAttributeNS(RDF, "about");
                                desc2about.setPrefix("rdf");
                                desc2about.setTextContent(entry.getURI().toString());
                                desc2.getAttributes().setNamedItemNS(desc2about);
                            } else {
                                final Attr nodeID = doc.createAttributeNS(RDF, "nodeID");
                                nodeID.setPrefix("rdf");
                                nodeID.setTextContent(entry.getID());
                                desc2.getAttributes().setNamedItemNS(nodeID);
                            }
                            skosxlLabel.appendChild(desc2);
                            final Element literalForm = doc.createElementNS(SKOSXL, "literalForm");
                            literalForm.setPrefix("skosxl");
                            literalForm.setTextContent(form.getWrittenRep().value);
                            literalForm.setAttribute("xml:lang", form.getWrittenRep().language);
                            desc2.appendChild(literalForm);
                            
                            for (Entry<URI, Collection<Object>> anno : entry.getAnnotations().entrySet()) {
                                final Matcher splitter = splitRegex.matcher(anno.getKey().toString());
                                splitter.matches();
                                for(Object annoVal : anno.getValue()) {
                                    if(annoVal instanceof String) {
                                        final Element annoElem = doc.createElementNS(splitter.group(1), splitter.group(2));
                                        annoElem.setTextContent(annoVal.toString());
                                        desc2.appendChild(annoElem);
                                    }
                                }
                            }
                            
                            root.appendChild(desc);
                        }
                    }
                }
            }
            return doc;
        } catch (Exception x) {
            throw new RuntimeException(x);
        }
    }

    private static class SKOSXLEntry {

        public URI ref;
        public String prefType;
        public Object entryIdOrURI;
        public String label;
        public String language;

        public SKOSXLEntry(Object entryIdOrURI) {
            this.entryIdOrURI = entryIdOrURI;
        }
    }

    private static class fromSKOSXL implements Visitor {

        private static final HashMap<Object, SKOSXLEntry> entries = new HashMap<Object, SKOSXLEntry>();

        private String getURI() {
            for (Object obj : entries.keySet()) {
                if (obj instanceof URI) {
                    final String uriStr = obj.toString();
                    final int idx = Math.max(uriStr.lastIndexOf("/"), uriStr.lastIndexOf("#"));
                    if (idx > 0) {
                        return uriStr.substring(0, idx + 1);
                    }
                }
            }
            return "unknown:lexicon";
        }

        public LemonModel toModel() throws UnsupportedEncodingException {
            final String prefix = getURI();
            LemonModel model = LemonSerializer.newInstance().create();
            final HashMap<String, Lexicon> lexica = new HashMap<String, Lexicon>();
            for (Entry<Object, SKOSXLEntry> entry : entries.entrySet()) {
                if (!lexica.containsKey(entry.getValue().language)) {
                    lexica.put(entry.getValue().language, model.addLexicon(URI.create(prefix + "#lexicon__" + entry.getValue().language), entry.getValue().language));
                }
                LemonModels.addEntryToLexicon(lexica.get(entry.getValue().language),
                        URI.create(prefix + "#lexicon__" + entry.getValue().language + "/" + URLEncoder.encode(entry.getValue().label, "UTF-8")),
                        entry.getValue().label,
                        entry.getValue().ref);
            }
            return model;
        }

        @Override
        public void accept(URI subj, URI pred, URI value) {
            if (pred.toString().equals(SKOSXL + "prefLabel")) {
                if (!entries.containsKey(value)) {
                    entries.put(value, new SKOSXLEntry(value));
                }
                entries.get(value).prefType = "pref";
                entries.get(value).ref = subj;
            } else if (pred.toString().equals(SKOSXL + "altLabel")) {
                if (!entries.containsKey(value)) {
                    entries.put(value, new SKOSXLEntry(value));
                }
                entries.get(value).prefType = "alt";
                entries.get(value).ref = subj;
            } else if (pred.toString().equals(SKOSXL + "hiddenLabel")) {
                if (!entries.containsKey(value)) {
                    entries.put(value, new SKOSXLEntry(value));
                }
                entries.get(value).prefType = "hidden";
                entries.get(value).ref = subj;
            }
        }

        @Override
        public void accept(URI subj, URI pred, String value) {
            if (pred.toString().equals(SKOSXL + "prefLabel")) {
                if (!entries.containsKey(value)) {
                    entries.put(value, new SKOSXLEntry(value));
                }
                entries.get(value).prefType = "pref";
                entries.get(value).ref = subj;
            } else if (pred.toString().equals(SKOSXL + "altLabel")) {
                if (!entries.containsKey(value)) {
                    entries.put(value, new SKOSXLEntry(value));
                }
                entries.get(value).prefType = "alt";
                entries.get(value).ref = subj;
            } else if (pred.toString().equals(SKOSXL + "hiddenLabel")) {
                if (!entries.containsKey(value)) {
                    entries.put(value, new SKOSXLEntry(value));
                }
                entries.get(value).prefType = "hidden";
                entries.get(value).ref = subj;
            }
        }

        @Override
        public void accept(URI subj, URI pred, String val, String lang) {
            if (pred.toString().equals(SKOSXL + "literalForm")) {
                if (!entries.containsKey(subj)) {
                    entries.put(subj, new SKOSXLEntry(subj));
                }
                entries.get(subj).label = val;
                entries.get(subj).language = lang;
            }
        }

        @Override
        public void accept(String subj, URI pred, URI value) {
        }

        @Override
        public void accept(String subj, URI pred, String id) {
        }

        @Override
        public void accept(String subj, URI pred, String val, String lang) {
            if (pred.toString().equals(SKOSXL + "literalForm")) {
                if (!entries.containsKey(subj)) {
                    entries.put(subj, new SKOSXLEntry(subj));
                }
                entries.get(subj).label = val;
                entries.get(subj).language = lang;
            }
        }
    }
}
