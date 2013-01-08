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
package eu.monnetproject.lemon;

import eu.monnetproject.lemon.impl.SPARQLResolver;
import eu.monnetproject.lemon.impl.LemonModelImpl;
import eu.monnetproject.lemon.impl.SPARULUpdater;
import eu.monnetproject.lemon.impl.SPARULUpdaterFactory;
import eu.monnetproject.lemon.model.PropertyValue;
import eu.monnetproject.lemon.model.Text;
import eu.monnetproject.lemon.model.LexicalForm;
import eu.monnetproject.lemon.model.LexicalEntry;
import eu.monnetproject.lemon.model.Lexicon;
import eu.monnetproject.lemon.model.Property;
import eu.monnetproject.lemon.model.LexicalSense;
import eu.monnetproject.lemon.liam.MorphologyApplicationException;
import eu.monnetproject.lemon.liam.MorphologyEngine;
import eu.monnetproject.lemon.liam.impl.MorphologyEngineImpl;
import eu.monnetproject.lemon.model.MorphPattern;
import java.net.URI;
import java.net.URL;
import java.util.*;

/**
 * Set of static task that work on lemon models
 *
 * @author John McCrae
 */
public final class LemonModels {
    // No instantiation

    private LemonModels() {
    }

    /**
     * Select a lexical entry by the form's representation
     *
     * @param model The model containing the appropriate lexica
     * @param form The representation of the form
     * @param lang The languages of the form
     */
    public static List<LexicalEntry> getEntriesByForm(LemonModel model, String form, String lang) {
        LinkedList<LexicalEntry> rval = new LinkedList<LexicalEntry>();
        String query = "PREFIX lemon: <" + LemonModel.LEMON_URI + ">  "
                + "SELECT DISTINCT ?entry { "
                + "?form lemon:writtenRep \"" + form + "\"@" + lang.toString() + " ."
                + "{ ?entry lemon:canonicalForm ?form } UNION "
                + "{ ?entry lemon:otherForm ?form } UNION "
                + "{ ?entry lemon:abstractForm ?form } UNION "
                + "{ ?entry lemon:lexicalForm ?form } ."
                + "?lexicon lemon:entry ?entry }";
        final Iterator<LexicalEntry> iter = model.query(LexicalEntry.class, query);
        while (iter.hasNext()) {
            rval.add(iter.next());
        }
        return rval;
    }

    /**
     * Select a lexical entry by the form's representation, using regex
     *
     * @param model The model containing the appropriate lexica
     * @param form The representation of the form
     */
    public static List<LexicalEntry> getEntriesByFormApprox(LemonModel model, String form) {
        LinkedList<LexicalEntry> rval = new LinkedList<LexicalEntry>();
        String query = "PREFIX lemon: <" + LemonModel.LEMON_URI + ">  "
                + "SELECT DISTINCT ?entry { "
                + "?form lemon:writtenRep ?rep ."
                + "FILTER(regex(str(?rep),\"" + form + "\",\"i\")) ."
                + "{ ?entry lemon:canonicalForm ?form } UNION "
                + "{ ?entry lemon:otherForm ?form } UNION "
                + "{ ?entry lemon:abstractForm ?form } UNION "
                + "{ ?entry lemon:form ?form }  }";
        //System.err.println(query);
        Iterator<LexicalEntry> iter = model.query(LexicalEntry.class, query);
        while (iter.hasNext()) {
            LexicalEntry entry = iter.next();
            rval.add(entry);
            //System.err.println(entry.getURI());
        }
        return rval;
    }

    /**
     * Select a lexical entry by its form
     *
     * @param model The model containing the appropriate lexica
     * @param form The form object
     */
    public static List<LexicalEntry> getEntriesByForm(LemonModel model, LexicalForm form) {
        try {
            LinkedList<LexicalEntry> rval = new LinkedList<LexicalEntry>();
            String formSparql;
            if (form.getURI() != null) {
                formSparql = "<" + form.getURI() + ">";
            } else {
                formSparql = "_:" + form.getID();
            }
            String query = "PREFIX lemon: <" + LemonModel.LEMON_URI + ">  "
                    + "SELECT DISTINCT ?entry { "
                    + "{ ?entry lemon:canonicalForm " + formSparql + " } UNION "
                    + "{ ?entry lemon:otherForm " + formSparql + " } UNION "
                    + "{ ?entry lemon:abstractForm " + formSparql + " } UNION "
                    + "{ ?entry lemon:form " + formSparql + " } ."
                    + "?lexicon lemon:entry ?entry }";
            Iterator<LexicalEntry> iter = model.query(LexicalEntry.class, query);
            while (iter.hasNext()) {
                rval.add(iter.next());
            }
            return rval;
        } catch (Exception x) {
            try {
                if (form.getURI() == null) {
                    LinkedList<LexicalEntry> rval = new LinkedList<LexicalEntry>();
                    // Work around for sesame bug
                    String query = "PREFIX lemon: <" + LemonModel.LEMON_URI + "> "
                            + "SELECT DISTINCT ?entry { "
                            + "{ ?entry lemon:canonicalForm ?x . ?x lemon:writtenRep \"" + form.getWrittenRep().value + "\"@" + form.getWrittenRep().language + " } UNION "
                            + "{ ?entry lemon:otherForm ?x . ?x lemon:writtenRep \"" + form.getWrittenRep().value + "\"@" + form.getWrittenRep().language + " } UNION "
                            + "{ ?entry lemon:abstractForm ?x . ?x lemon:writtenRep \"" + form.getWrittenRep().value + "\"@" + form.getWrittenRep().language + " } UNION "
                            + "{ ?entry lemon:form ?x . ?x lemon:writtenRep \"" + form.getWrittenRep().value + "\"@" + form.getWrittenRep().language + " } ."
                            + "?lexicon lemon:entry ?entry. ?entry ?p ?x }";

                    Iterator<LexicalEntry> iter = model.query(LexicalEntry.class, query);
                    while (iter.hasNext()) {
                        rval.add(iter.next());
                    }
                    return rval;
                } else {
                    throw new Exception();
                }
            } catch (Exception x2) {
                LinkedList<LexicalEntry> rval = new LinkedList<LexicalEntry>();
                for (Lexicon lexicon : model.getLexica()) {
                    ENTRIES:
                    for (LexicalEntry entry : lexicon.getEntrys()) {
                        for (LexicalForm form2 : entry.getForms()) {
                            if (form.equals(form2)) {
                                rval.add(entry);
                                continue ENTRIES;
                            }
                        }
                    }
                }
                return rval;
            }
        }
    }

    /**
     * Get the set of lexica containing a given entry
     *
     * @param model The model containing the appropriate lexica
     * @param entry The entry
     */
    public static List<Lexicon> getLexicaByEntry(LemonModel model, LexicalEntry entry) {
        LinkedList<Lexicon> rval = new LinkedList<Lexicon>();
        try {
            
            String entrySparql;
            if (entry.getURI() != null) {
                entrySparql = "<" + entry.getURI() + ">";
            } else {
                entrySparql = "_:" + entry.getID();
            }
            String query = "PREFIX lemon: <" + LemonModel.LEMON_URI + ">  "
                    + "SELECT DISTINCT ?lexicon { "
                    + "?lexicon lemon:entry " + entrySparql + " }";
            Iterator<Lexicon> iter = model.query(Lexicon.class, query);
            while (iter.hasNext()) {
                rval.add(iter.next());
            }
            return rval;
        } catch (Exception x) {
            x.printStackTrace();
            for(Lexicon lexicon : model.getLexica()) {
                if(lexicon.hasEntry(entry)) {
                    rval.add(lexicon);
                }
            }
            return rval;
        }
    }

    /**
     * Get the set of entries that refer to a given reference
     *
     * @param model The model containing the appropriate lexica
     * @param reference The uri reference
     */
    public static List<LexicalEntry> getEntryByReference(LemonModel model, URI reference) {
        LinkedList<LexicalEntry> rval = new LinkedList<LexicalEntry>();
        try {
            String query = "PREFIX lemon: <" + LemonModel.LEMON_URI + ">  "
                    + "SELECT DISTINCT ?entry { "
                    + "?entry lemon:sense ?sense ."
                    + "?sense lemon:reference <" + reference + "> }";
            Iterator<LexicalEntry> iter = model.query(LexicalEntry.class, query);
            while (iter.hasNext()) {
                rval.add(iter.next());
            }
            return rval;
        } catch (Exception x) {
            for (Lexicon lexicon : model.getLexica()) {
                for (LexicalEntry lexicalEntry : lexicon.getEntrys()) {
                    for (LexicalSense sense : lexicalEntry.getSenses()) {
                        if (sense.getReference().equals(reference)) {
                            rval.add(lexicalEntry);
                            continue;
                        }
                    }
                }
            }
            return rval;
        }
    }

    public static List<LexicalEntry> getEntryByReference(Lexicon lexicon, URI reference) {
        LinkedList<LexicalEntry> rval = new LinkedList<LexicalEntry>();
        try {
            String query = "PREFIX lemon: <" + LemonModel.LEMON_URI + ">  "
                    + "SELECT DISTINCT ?entry { "
                    + "<" + lexicon.getURI() + "> lemon:entry ?entry ."
                    + "?entry lemon:sense ?sense ."
                    + "?sense lemon:reference <" + reference + "> }";
            Iterator<LexicalEntry> iter = lexicon.getModel().query(LexicalEntry.class, query);
            while (iter.hasNext()) {
                rval.add(iter.next());
            }
            return rval;
        } catch (Exception x) {
            for (LexicalEntry lexicalEntry : lexicon.getEntrys()) {
                for (LexicalSense sense : lexicalEntry.getSenses()) {
                    if (sense.getReference().equals(reference)) {
                        rval.add(lexicalEntry);
                        continue;
                    }
                }
            }
            return rval;
        }

    }

    /**
     * Get the set of entries that refer to a given sense
     *
     * @param model The model containing the appropriate lexica
     * @param sense The sense object
     */
    @SuppressWarnings("unchecked")
    public static LexicalEntry getEntryBySense(LemonModel model, LexicalSense sense) {
        if (sense.getIsSenseOf() != null) {
            return sense.getIsSenseOf();
        } else {
            String senseSparql;
            if (sense.getURI() != null) {
                senseSparql = "<" + sense.getURI() + ">";
            } else {
                senseSparql = "_:" + sense.getID();
            }
            String query = "PREFIX lemon: <" + LemonModel.LEMON_URI + ">  "
                    + "SELECT DISTINCT ?entry { "
                    + "?entry lemon:sense " + senseSparql + " }";
            System.err.println(query);
            Iterator<LexicalEntry> iter = model.query(LexicalEntry.class, query);
            if (iter.hasNext()) {
                final LexicalEntry entry = iter.next();
                sense.setIsSenseOf(entry);
                return entry;
            } else {
                return null;
            }
        }
    }

    /**
     * Get all the entries in a lexicon as an alphabetic sorted list
     *
     * @param model The model containing the lexica
     * @param lexicon The lexicon to list
     * @param offset The first entry to show
     * @param limit The maximum number of entries to return, 0 for no limit
     */
    @SuppressWarnings("unchecked")
    public static Collection<LexicalEntry> getEntriesAlphabetic(LemonModel model, Lexicon lexicon, int offset, int limit) {
        final Comparator<LexicalEntry> entryIDComp = new Comparator<LexicalEntry>() {

            @Override
            public int compare(LexicalEntry o1, LexicalEntry o2) {
                if (o1.getURI() != null && o2.getURI() != null) {
                    return o1.getURI().toString().compareTo(o2.getURI().toString());
                } else if (o1.getURI() == null && o2.getURI() == null) {
                    return o1.getID().compareTo(o2.getID());
                } else if (o1.getURI() == null) {
                    return +1;
                } else {
                    return -1;
                }
            }
        };
        try {
            TreeSet<LexicalEntry> rval = new TreeSet<LexicalEntry>(entryIDComp);
            final String query = "PREFIX lemon: <" + LemonModel.LEMON_URI + "> "
                    + "SELECT DISTINCT ?entry {"
                    + "<" + lexicon.getURI() + "> lemon:entry ?entry . } "
                    + "ORDER BY ?entry "
                    + (limit > 0 ? "LIMIT " + limit : "")
                    + (offset > 0 ? "OFFSET " + offset : "");
            Iterator<LexicalEntry> iter = model.query(LexicalEntry.class, query);
            while (iter.hasNext()) {
                rval.add(iter.next());
            }
            return rval;
        } catch (Exception x) {
            x.printStackTrace();
            TreeSet<LexicalEntry> entries = new TreeSet<LexicalEntry>(new Comparator<LexicalEntry>() {

                @Override
                public int compare(LexicalEntry e1, LexicalEntry e2) {
                    if (e1.getCanonicalForm() != null && e1.getCanonicalForm().getWrittenRep() != null) {
                        if (e2.getCanonicalForm() != null && e2.getCanonicalForm().getWrittenRep() != null) {
                            int rv = e1.getCanonicalForm().getWrittenRep().value.compareTo(
                                    e2.getCanonicalForm().getWrittenRep().value);
                            if (rv == 0) {
                                return e1.getURI().compareTo(e2.getURI());
                            } else {
                                return rv;
                            }
                        } else {
                            return -1;
                        }
                    } else if (e2.getCanonicalForm() != null && e2.getCanonicalForm().getWrittenRep() != null) {
                        return 1;
                    } else {
                        return e1.getURI().compareTo(e2.getURI());
                    }
                }
            });
            for (LexicalEntry le : lexicon.getEntrys()) {
                entries.add(le);
            }
            if (limit > 0) {
                if (offset > 0) {
                    return new ArrayList<LexicalEntry>(entries).subList(offset, Math.min(entries.size(), offset + limit));
                } else {
                    return new ArrayList<LexicalEntry>(entries).subList(0, Math.min(entries.size(), 0 + limit));
                }
            } else {
                if (offset > 0) {
                    return new ArrayList<LexicalEntry>(entries).subList(offset, entries.size());
                } else {
                    return new ArrayList<LexicalEntry>(entries);
                }
            }
        }
    }

    /**
     * Get entries in a lexicon mapped by the references they have
     *
     * @param model The model containing all the lexica
     * @param lexicon The lexicon containg all entries
     * @param offset The first entry to return
     * @param limit The maxiumum number of entries to return, 0 for unlimited
     */
    public static Map<URI, List<LexicalEntry>> getEntriesBySense(LemonModel model, Lexicon lexicon, int offset, int limit) {

        try {
            TreeMap<URI, List<LexicalEntry>> rval = new TreeMap<URI, List<LexicalEntry>>(new Comparator<URI>() {

                @Override
                public int compare(URI uri1, URI uri2) {
                    if (uri1.toString().equals("special:none")) {
                        if (uri2.toString().equals("special:none")) {
                            return 0;
                        } else {
                            return -1;
                        }
                    } else if (uri2.toString().equals("special:none")) {
                        return 1;
                    } else {
                        return uri1.toString().toLowerCase().compareTo(uri2.toString().toLowerCase());
                    }
                }

                @Override
                public int hashCode() {
                    return super.hashCode();
                }

                @Override
                public boolean equals(Object obj) {
                    if (obj == null) {
                        return false;
                    }
                    if (getClass() != obj.getClass()) {
                        return false;
                    }
                    return this == obj;
                }
            });
            Iterator<LexicalEntry> iter = model.query(LexicalEntry.class,
                    "PREFIX lemon: <" + LemonModel.LEMON_URI + "> "
                    + "SELECT DISTINCT ?entry {"
                    + "<" + lexicon.getURI() + "> lemon:entry ?entry . "
                    + "OPTIONAL { ?entry lemon:sense ?sense ."
                    + "?sense lemon:reference ?ref . } "
                    + "OPTIONAL { ?entry lemon:canonicalForm ?form . "
                    + "?form lemon:writtenRep ?rep } } "
                    + "ORDER BY ?sense ?rep "
                    + (limit > 0 ? "LIMIT " + limit : "")
                    + (offset > 0 ? "OFFSET " + offset : ""));
            while (iter.hasNext()) {
                LexicalEntry entry = iter.next();
                for (LexicalSense s : entry.getSenses()) {
                    URI ref = s.getReference();
                    if (!rval.containsKey(ref)) {
                        rval.put(ref, new LinkedList<LexicalEntry>());
                    }
                    rval.get(ref).add(entry);
                }
                if (entry.getSenses().isEmpty()) {
                    URI ref = URI.create("special:none");
                    if (!rval.containsKey(ref)) {
                        rval.put(ref, new LinkedList<LexicalEntry>());
                    }
                    rval.get(ref).add(entry);
                }
            }
            return rval;
        } catch (Exception x) {
            x.printStackTrace();
            return null;
            //TreeSet<LexicalEntry> entries = new TreeSet<LexicalEntry>(new Comparator<LexicalEntry>() {
            //		public int compare(LexicalEntry e1, LexicalEntry e2) {
            //			if(e1.getCanonicalForm() != null && e1.getCanonicalForm().getWrittenRep() != null) {
            //				if(e2.getCanonicalForm() != null && e2.getCanonicalForm().getWrittenRep() != null) {
            //					int rv = e1.getCanonicalForm().getWrittenRep().value.compareTo(
            //						e2.getCanonicalForm().getWrittenRep().value);
            //					if(rv == 0) {
            //						return e1.getURI().compareTo(e2.getURI());
            //					} else {
            //						return rv;
            //					}
            //				} else {
            //					return -1;
            //				}
            //			} else if(e2.getCanonicalForm() != null  && e2.getCanonicalForm().getWrittenRep() != null) {
            //				return 1;
            //			} else {
            //				return e1.getURI().compareTo(e2.getURI());
            //			}
            //		}
            //		public boolean equals(Object o) { return this == o; }
            //});
            //for(LexicalEntry le : lexicon.getEntrys()) {
            //	entries.add(le);
            //}
            //if(limit > 0) {
            //	if(offset > 0) {
            //		return new ArrayList(entries).subList(offset,offset+limit);
            //	} else {
            //		return new ArrayList(entries).subList(0,0+limit);
            //	}
            //} else {
            //	if(offset > 0) {
            //		return new ArrayList(entries).subList(offset,entries.size());
            //	} else {
            //		return new ArrayList(entries);
            //	}
            //}
        }
    }

    /**
     * Get entries by their written representation and properties
     *
     * @param model The model containing all lexica
     * @param form The written representation of the form
     * @param lang The language of the form
     * @param props The set of properties the entry object has
     */
    public static List<LexicalEntry> getEntriesByFormAndProps(LemonModel model, String form, String lang,
            Map<Property, PropertyValue> props) {
        try {
            LinkedList<LexicalEntry> rval = new LinkedList<LexicalEntry>();
            StringBuilder query = new StringBuilder(100);
            query.append("PREFIX lemon: <" + LemonModel.LEMON_URI + ">  " + "SELECT DISTINCT ?entry { " + "?form lemon:writtenRep \"").append(form).append("\"@").append(lang.toString()).append(" ."
                    + "{ ?entry lemon:canonicalForm ?form } UNION "
                    + "{ ?entry lemon:otherForm ?form } UNION "
                    + "{ ?entry lemon:abstractForm ?form } UNION "
                    + "{ ?entry lemon:lexicalForm ?form } .");
            for (Map.Entry<Property, PropertyValue> prop : props.entrySet()) {
                query.append("?entry <").append(prop.getKey().getURI()).append("> <").append(prop.getValue().getURI()).append("> .");
            }
            query.append("?lexicon lemon:entry ?entry }");
            Iterator<LexicalEntry> iter = model.query(LexicalEntry.class, query.toString());
            while (iter.hasNext()) {
                rval.add(iter.next());
            }
            return rval;
        } catch (Exception x) {
            List<LexicalEntry> rval = new LinkedList<LexicalEntry>();
            for (Lexicon lexicon : model.getLexica()) {
                LE_LOOP:
                for (LexicalEntry le : lexicon.getEntrys()) {
                    for (Property prop : props.keySet()) {
                        Collection<PropertyValue> vals = le.getProperty(prop);
                        if (!vals.contains(props.get(prop))) {
                            continue LE_LOOP;
                        }
                    }
                    if (le.getCanonicalForm() != null && le.getCanonicalForm().getWrittenRep().value.equals(form)) {
                        rval.add(le);
                    } else {
                        for (LexicalForm f : le.getOtherForms()) {
                            if (f.getWrittenRep().value.equals(form)
                                    && f.getWrittenRep().language.equals(lang)) {
                                rval.add(le);
                                continue LE_LOOP;
                            }
                        }
                        for (LexicalForm f : le.getAbstractForms()) {
                            if (f.getWrittenRep().value.equals(form)
                                    && f.getWrittenRep().language.equals(lang)) {
                                rval.add(le);
                                continue LE_LOOP;
                            }
                        }
                        for (LexicalForm f : le.getForms()) {
                            if (f.getWrittenRep().value.equals(form)
                                    && f.getWrittenRep().language.equals(lang)) {
                                rval.add(le);
                                continue LE_LOOP;
                            }
                        }
                    }
                }
            }
            return rval;
        }
    }

    /**
     * Quickly add a lexical entry to a lexicon. Will re-use existing lexical entry
     * adding a new sense if the entry's URI already exists.
     *
     * @param Lexicon The lexicon
     * @param entryURI The identifier for the entry
     * @param canForm The canonical form
     * @param sense The reference of the sense URI
     * @throws IllegalArgumentException If the entry URI is duplicated by an element with a different canonical form
     */
    public static LexicalEntry addEntryToLexicon(Lexicon lexicon, URI entryURI, String canForm, URI senseRef) {
        LemonFactory factory = lexicon.getModel().getFactory();
        boolean duplicateEntry = factory.isURIUsed(entryURI);
        LexicalEntry entry = factory.makeLexicalEntry(entryURI);
        if(duplicateEntry && entry.getCanonicalForm() != null && entry.getCanonicalForm().getWrittenRep() != null && !canForm.equals(entry.getCanonicalForm().getWrittenRep().value)) {
            throw new IllegalArgumentException("There is already a lexical entry in this lexicon with URI <" +entryURI+"> and canonical form \""
                    + entry.getCanonicalForm().getWrittenRep().value + "\" that differs from \"" + canForm +"\"");
        }
        LexicalForm form = factory.makeForm(URI.create(entryURI + "/canonicalForm"));
        if (senseRef != null) {
            URI senseURI = URI.create(entryURI + "/sense");
            int i = 1;
            while(factory.isURIUsed(senseURI)) {
                senseURI = URI.create(entryURI + "/sense"+i++);
            }
            LexicalSense sense = factory.makeSense(senseURI);
           
            sense.setReference(senseRef);
            entry.addSense(sense);
        }
        form.setWrittenRep(new Text(canForm, lexicon.getLanguage()));
        entry.setCanonicalForm(form);
        lexicon.addEntry(entry);
        return entry;
    }

    public static LexicalForm resolveForm(LexicalEntry entry, Map<Property, Collection<PropertyValue>> properties) {
        FORMS:
        for (LexicalForm form : entry.getForms()) {
            for (Map.Entry<Property, Collection<PropertyValue>> props : properties.entrySet()) {
                if (!form.getProperty(props.getKey()).containsAll(props.getValue())) {
                    continue FORMS;
                }
            }
            return form;
        }
        final MorphologyEngine morphEngine = new MorphologyEngineImpl();
        for (MorphPattern pattern : entry.getPatterns()) {
            try {
                final LexicalForm form = morphEngine.generate(entry, pattern, properties);
                if (form != null) {
                    return form;
                }
            } catch (MorphologyApplicationException x) {
                x.printStackTrace();
            }
        }
        return null;
    }

    /**
     * Connect to a lemon model contained in a SPARQL endpoint
     *
     * @param endpoint The URL of the SPARQL endpoint
     * @param graphs The graphs in the endpoint to use
     * @param lingOnto The linguistic ontology to use (may be null)
     * @return A model which resolves based on the endpoint
     */
    public static LemonModel sparqlEndpoint(URL endpoint, Set<URI> graphs, LinguisticOntology lingOnto) {
        return new LemonModelImpl(null, new SPARQLResolver(endpoint, graphs, lingOnto),null);
    }

    /**
     * Connect to a lemon model in a repository supporting SPARQL and SPARQL
     * update
     *
     * @param sparqlEndpoint The URL of the endpoint for querying, e.g.,
     * "http://localhost:8080/sparql"
     * @param graph The graph to use in the endpoint
     * @param lingOnto The linguistic ontology to use (may be null)
     * @param updateEndpoint The URL pattern for the endpoint with query, e.g.,
     * "http://localhost:8080/sparql-auth?query="
     * @param dialect Which dialect of SPARQL to use, e.g., SPARUL for Virtuoso,
     * SPARQL11 for 4store
     * @return A model which resolves and updates based on the endpoint
     */
    public static LemonModel sparqlUpdateEndpoint(URL sparqlEndpoint, URI graph, LinguisticOntology lingOnto,
            String updateEndpoint, SPARQL dialect) {
        return new LemonModelImpl(null, new SPARQLResolver(sparqlEndpoint, Collections.singleton(graph), lingOnto), new SPARULUpdaterFactory(updateEndpoint, graph, dialect));
    }

    /**
     * Connect to a lemon model in a repository supporting SPARQL and SPARQL
     * update
     *
     * @param sparqlEndpoint The URL of the endpoint for querying, e.g.,
     * "http://localhost:8080/sparql"
     * @param graph The graph to use in the endpoint
     * @param lingOnto The linguistic ontology to use (may be null)
     * @param updateEndpoint The URL pattern for the endpoint with query, e.g.,
     * "http://localhost:8080/sparql-auth?query="
     * @param username The user name to use to authenticate
     * @param password The password to use to authenticate
     * @param dialect Which dialect of SPARQL to use, e.g., SPARUL for Virtuoso,
     * SPARQL11 for 4store
     * @return A model which resolves and updates based on the endpoint
     */
    public static LemonModel sparqlUpdateEndpoint(URL sparqlEndpoint, URI graph, LinguisticOntology lingOnto,
            String updateEndpoint, String username, String password, SPARQL dialect) {
        return new LemonModelImpl(null, new SPARQLResolver(sparqlEndpoint, Collections.singleton(graph), lingOnto), new SPARULUpdaterFactory(updateEndpoint, graph, username, password, dialect));
    }
}
