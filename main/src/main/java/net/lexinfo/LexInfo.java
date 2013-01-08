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
package net.lexinfo;

import eu.monnetproject.lemon.LinguisticOntology;
import eu.monnetproject.lemon.URIElement;
import eu.monnetproject.lemon.URIValue;
import eu.monnetproject.lemon.model.Condition;
import eu.monnetproject.lemon.model.Definition;
import eu.monnetproject.lemon.model.Edge;
import eu.monnetproject.lemon.model.FormVariant;
import eu.monnetproject.lemon.model.LemonElementOrPredicate;
import eu.monnetproject.lemon.model.LexicalVariant;
import eu.monnetproject.lemon.model.Property;
import eu.monnetproject.lemon.model.PropertyValue;
import eu.monnetproject.lemon.model.Representation;
import eu.monnetproject.lemon.model.SenseContext;
import eu.monnetproject.lemon.model.SenseRelation;
import eu.monnetproject.lemon.model.SynArg;
import eu.monnetproject.lemon.model.Text;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;


public class LexInfo implements LinguisticOntology {

    //private static final String lemonURI = "http://www.monnet-project.eu/lemon#";
    public static final String LEXINFO_URI = "http://www.lexinfo.net/ontology/2.0/lexinfo#";
    private final HashMap<String, Property> propertys;
    private final HashMap<String, PropertyValue> propertyValues;
    private final HashMap<String, Condition> conditions;
    private final HashMap<String, SenseContext> contexts;
    private final HashMap<String, Definition> definitions;
    private final HashMap<String, Edge> edges;
    private final HashMap<String, Representation> representations;
    private final HashMap<String, SenseRelation> senseRelations;
    private final HashMap<String, LexicalVariant> lexicalVariants;
    private final HashMap<String, FormVariant> formVariants;
    private final HashMap<String, java.net.URI> frameClasses;
    private final HashMap<Property, Collection<PropertyValue>> valuesByProp;
    private final HashMap<java.net.URI, Collection<SynArg>> synArgsByFrame;
    private final HashMap<String, SynArg> synArgs;
    private final HashMap<String, Collection<String>> comments;
    private final HashMap<URI, Collection<Text>> examples;

    public LexInfo() {
        conditions = new HashMap<String, Condition>();
        contexts = new HashMap<String, SenseContext>();
        definitions = new HashMap<String, Definition>();
        edges = new HashMap<String, Edge>();
        formVariants = new HashMap<String, FormVariant>();
        frameClasses = new HashMap<String, java.net.URI>();
        lexicalVariants = new HashMap<String, LexicalVariant>();
        propertyValues = new HashMap<String, PropertyValue>();
        propertys = new HashMap<String, Property>();
        representations = new HashMap<String, Representation>();
        senseRelations = new HashMap<String, SenseRelation>();
        synArgs = new HashMap<String, SynArg>();
        synArgsByFrame = new HashMap<java.net.URI, Collection<SynArg>>();
        valuesByProp = new HashMap<Property, Collection<PropertyValue>>();
        comments = new HashMap<String, Collection<String>>();
        examples = new HashMap<URI, Collection<Text>>();
        try {
            final String path = "/lexinfo/";
            readMap(path + "conditions", conditions, ConditionPredicateImpl.class);
            readMap(path + "contexts", contexts, ContextImpl.class);
            readMap(path + "definitions", definitions, DefinitionPredicateImpl.class);
            readMap(path + "edges", edges, EdgeImpl.class);
            readMap(path + "formVariants", formVariants, FormVariantImpl.class);
            readMapURI(path + "frameClasses", frameClasses);
            readMap(path + "lexicalVariants", lexicalVariants, LexicalVariantImpl.class);
            readMap(path + "propertyValues", propertyValues, PropertyValueImpl.class);
            readMap(path + "propertys", propertys, PropertyImpl.class);
            readMap(path + "representations", representations, RepresentationImpl.class);
            readMap(path + "senseRelations", senseRelations, SenseRelationImpl.class);
            readMap(path + "synArgs", synArgs, SynArgImpl.class);
            readMapSynArgs(path + "synArgsByFrame", synArgsByFrame);
            readMapValues(path + "valuesByProp", valuesByProp);
            readHelpStr(path + "comments", comments);
            readHelpURI(path + "examples", examples);
        } catch (Exception x) {
            throw new RuntimeException(x);
        }
    }

    private <Elem> void readMap(String path, HashMap<String, Elem> map, Class<? extends Elem> clazz) throws IOException, NoSuchMethodException, InstantiationException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        final URL resource = this.getClass().getResource(path);
        if (resource == null) {
            throw new RuntimeException("Miscompiled could not load " + path);
        }
        final BufferedReader in = new BufferedReader(new InputStreamReader(resource.openStream()));
        String s;
        final Constructor<? extends Elem> constructor = clazz.getConstructor(java.net.URI.class);
        while ((s = in.readLine()) != null) {
            String[] ss = s.split("\\s");
            if (ss.length != 2) {
                throw new RuntimeException("invalid line: " + s);
            }
            final Elem inst = constructor.newInstance(java.net.URI.create(ss[1]));
            map.put(ss[0], inst);
        }
    }

    private void readMapURI(String path, HashMap<String, java.net.URI> map) throws IOException {
        final BufferedReader in = new BufferedReader(new InputStreamReader(this.getClass().getResource(path).openStream()));
        String s;
        while ((s = in.readLine()) != null) {
            String[] ss = s.split("\\s");
            if (ss.length != 2) {
                throw new RuntimeException("invalid line: " + s);
            }
            map.put(ss[0], java.net.URI.create(ss[1]));
        }
    }

    private void readMapSynArgs(String path, HashMap<java.net.URI, Collection<SynArg>> map) throws IOException {
        final BufferedReader in = new BufferedReader(new InputStreamReader(this.getClass().getResource(path).openStream()));
        String s;
        while ((s = in.readLine()) != null) {
            String[] ss = s.split("\\s");
            if (ss.length < 1) {
                throw new RuntimeException("invalid line: " + s);
            }
            Collection<SynArg> args = new LinkedList<SynArg>();
            for (int i = 1; i < ss.length; i++) {
                args.add(new SynArgImpl((java.net.URI.create(ss[i]))));
            }
            map.put(java.net.URI.create(ss[0]), args);
        }
    }

    private void readMapValues(String path, HashMap<Property, Collection<PropertyValue>> map) throws IOException {
        final BufferedReader in = new BufferedReader(new InputStreamReader(this.getClass().getResource(path).openStream()));
        String s;
        while ((s = in.readLine()) != null) {
            String[] ss = s.split("\\s");
            if (ss.length < 1) {
                throw new RuntimeException("invalid line: " + s);
            }
            Collection<PropertyValue> args = new LinkedList<PropertyValue>();
            for (int i = 1; i < ss.length; i++) {
                args.add(new PropertyValueImpl((java.net.URI.create(ss[i]))));
            }
            map.put(new PropertyImpl(java.net.URI.create(ss[0])), args);
        }
    }

    private <C> C getCheck(HashMap<String, C> map, String key) {
        C rv = map.get(key);
        if (rv == null) {
            throw new IllegalArgumentException(key + " is not within LexInfo");
        } else {
            return rv;
        }
    }

    /**
     * Get a property reference by its name
     * @throws IllegalArgumentException If the name is not recognised by the ontology
     */
    @Override
    public Property getProperty(String name) {
        return getCheck(propertys, name);
    }

    /**
     * Get the set of properties listed in the linguistics ontology
     */
    @Override
    public Collection<Property> getProperties() {
        return propertys.values();
    }

    /**
     * Get a property value by its name
     * @throws IllegalArgumentException If the name is not recognised by the ontology
     */
    @Override
    public PropertyValue getPropertyValue(String name) {
        return getCheck(propertyValues, name);
    }

    /**
     * Get the set of property values listed in the linguistic ontology for a given property 
     */
    @Override
    public Collection<PropertyValue> getValues(Property property) {
        Collection<PropertyValue> rv = valuesByProp.get(property);
        if (rv == null) {
            throw new IllegalArgumentException(property.getURI() + " not in LexInfo");
        }
        return rv;
    }

    /**
     * Get a synArg reference by its name
     * @throws IllegalArgumentException If the name is not recognised by the ontology
     */
    @Override
    public SynArg getSynArg(String name) {
        return getCheck(synArgs, name);
    }

    /**
     * Get the set of properties listed in the linguistics ontology
     */
    @Override
    public Collection<SynArg> getSynArgs() {
        return synArgs.values();
    }

    /**
     * Get the set of URI values listed in the linguistic ontology for a given URI 
     */
    @Override
    public Collection<SynArg> getSynArgsForFrame(java.net.URI frameClass) {
        Collection<SynArg> rv = synArgsByFrame.get(frameClass);
        if (rv == null) {
            throw new IllegalArgumentException(frameClass + " not in LexInfo");
        }
        return rv;
    }

    /**
     * Get a condition predicate by its name 
     * @throws IllegalArgumentException If the name is not recognised by the ontology
     */
    @Override
    public Condition getCondition(String name) {
        return getCheck(conditions, name);
    }

    /**
     * Get the set of condition predicates listed in the linguistic ontology
     */
    @Override
    public Collection<Condition> getConditions() {
        return conditions.values();
    }

    /**
     * Get a context predicate by its name 
     * @throws IllegalArgumentException If the name is not recognised by the ontology
     */
    @Override
    public SenseContext getContext(String name) {
        return getCheck(contexts, name);
    }

    /**
     * Get the set of context predicates listed in the linguistic ontology
     */
    @Override
    public Collection<SenseContext> getContexts() {
        return contexts.values();
    }

    /**
     * Get a definition predicate by its name 
     * @throws IllegalArgumentException If the name is not recognised by the ontology
     */
    @Override
    public Definition getDefinition(String name) {
        return getCheck(definitions, name);
    }

    /**
     * Get the set of definition predicates listed in the linguistic ontology
     */
    @Override
    public Collection<Definition> getDefinitions() {
        return definitions.values();
    }

    /**
     * Get a edge relation type by its name 
     * @throws IllegalArgumentException If the name is not recognised by the ontology
     */
    @Override
    public Edge getEdge(String name) {
        return getCheck(edges, name);
    }

    /**
     * Get the set of edge relation types listed in the linguistic ontology
     */
    @Override
    public Collection<Edge> getEdge() {
        return edges.values();
    }

    /**
     * Get a form variant relation type by its name 
     * @throws IllegalArgumentException If the name is not recognised by the ontology
     */
    @Override
    public FormVariant getFormVariant(String name) {
        return getCheck(formVariants, name);
    }

    /**
     * Get the set of form variant relation types listed in the linguistic ontology
     */
    @Override
    public Collection<FormVariant> getFormVariant() {
        return formVariants.values();
    }

    /**
     * Get a lexical variant relation type by its name 
     * @throws IllegalArgumentException If the name is not recognised by the ontology
     */
    @Override
    public LexicalVariant getLexicalVariant(String name) {
        return getCheck(lexicalVariants, name);
    }

    /**
     * Get the set of lexical variant relation types listed in the linguistic ontology
     */
    @Override
    public Collection<LexicalVariant> getLexicalVariant() {
        return lexicalVariants.values();
    }

    /**
     * Get a representation relation type by its name 
     * @throws IllegalArgumentException If the name is not recognised by the ontology
     */
    @Override
    public Representation getRepresentation(String name) {
        return getCheck(representations, name);
    }

    /**
     * Get the set of representation relation types listed in the linguistic ontology
     */
    @Override
    public Collection<Representation> getRepresentation() {
        return representations.values();
    }

    /**
     * Get a sense relation type by its name 
     * @throws IllegalArgumentException If the name is not recognised by the ontology
     */
    @Override
    public SenseRelation getSenseRelation(String name) {
        return getCheck(senseRelations, name);
    }

    /**
     * Get the set of sense relation types listed in the linguistic ontology
     */
    @Override
    public Collection<SenseRelation> getSenseRelation() {
        return senseRelations.values();
    }

    /**
     * Get all possible frame classes
     */
    @Override
    public Collection<java.net.URI> getFrameClasses() {
        return frameClasses.values();
    }

    /**
     * Get a frame class by name
     * @throws IllegalArgumentException If the name is not recognised by the ontology
     */
    @Override
    public java.net.URI getFrameClass(String name) {
        return getCheck(frameClasses, name);
    }

    @Override
    public Map<Property, Collection<PropertyValue>> getPropertyMap(String... strings) {
        Map<Property, Collection<PropertyValue>> rval = new HashMap<Property, Collection<PropertyValue>>();
        for (int i = 0; i < strings.length; i += 2) {
            final Property property = getProperty(strings[i]);
            if (!rval.containsKey(property)) {
                rval.put(property, new LinkedList<PropertyValue>());
            }
            rval.get(property).add(getPropertyValue(strings[i + 1]));
        }
        return rval;
    }

    private void readHelpStr(String string, HashMap<String, Collection<String>> comments) throws IOException {
        final URL resource = this.getClass().getResource(string);
        final BufferedReader in = new BufferedReader(new InputStreamReader(resource.openStream()));
        String s;
        while ((s = in.readLine()) != null) {
            String[] ss = s.split("\t");
            if (ss.length != 2) {
                throw new RuntimeException("invalid line: " + s);
            }
            if (!comments.containsKey(ss[0])) {
                comments.put(ss[0], new LinkedList<String>());
            }
            comments.get(ss[0]).add(ss[1]);
        }
    }

    private void readHelpURI(String string, HashMap<URI, Collection<Text>> examples)  throws IOException {
        final URL resource = this.getClass().getResource(string);
        final BufferedReader in = new BufferedReader(new InputStreamReader(resource.openStream()));
        String s;
        while ((s = in.readLine()) != null) {
            String[] ss = s.split("\t");
            if (ss.length != 3) {
                throw new RuntimeException("invalid line: " + s);
            }
            final URI uri = URI.create(ss[0]);
            if (!examples.containsKey(uri)) {
                examples.put(uri, new LinkedList<Text>());
            }
            String lang;
            if(ss[1].equals("null")) {
                lang = null;
            } else {
                lang = ss[1];
            }
            examples.get(uri).add(new Text(ss[2],lang));
        }
    }

    @Override
    public Collection<String> getDefinitions(LemonElementOrPredicate elem) {
        if(elem.getURI() == null) {
            return Collections.EMPTY_LIST;
        } else if(!comments.containsKey(elem.getURI().getFragment())) {
            return Collections.EMPTY_LIST;
        } else {
            return comments.get(elem.getURI().getFragment());
        }
    }

    @Override
    public Collection<Text> getExamples(URI frameClass) {
        if(examples.containsKey(frameClass)) {
            return examples.get(frameClass);
        } else { 
            return Collections.EMPTY_LIST;
        }
    }
    
    
    
    
}
class PropertyImpl extends URIValue implements Property {

    public PropertyImpl(java.net.URI uri) {
        super(uri);
    }
}

class ConditionPredicateImpl extends URIValue implements Condition {

    public ConditionPredicateImpl(java.net.URI uri) {
        super(uri);
    }
}

class DefinitionPredicateImpl extends URIValue implements Definition {

    public DefinitionPredicateImpl(java.net.URI uri) {
        super(uri);
    }
}

class EdgeImpl extends URIValue implements Edge {

    public EdgeImpl(java.net.URI uri) {
        super(uri);
    }
}



class ContextImpl extends URIElement implements SenseContext {

    public ContextImpl(java.net.URI uri) {
        super(uri);
    }

    @Override
    public Text getValue() {
        return null;
    }

    @Override
    public void setValue(Text value) {
        throw new UnsupportedOperationException("Not allowed");
    }
}

class RepresentationImpl extends URIValue implements Representation {

    public RepresentationImpl(java.net.URI uri) {
        super(uri);
    }
}

class SenseRelationImpl extends URIValue implements SenseRelation {

    public SenseRelationImpl(java.net.URI uri) {
        super(uri);
    }
}

class LexicalVariantImpl extends URIValue implements LexicalVariant {

    public LexicalVariantImpl(java.net.URI uri) {
        super(uri);
    }
}

class FormVariantImpl extends URIValue implements FormVariant {

    public FormVariantImpl(java.net.URI uri) {
        super(uri);
    }
}

class PropertyValueImpl extends URIElement implements PropertyValue {

    public PropertyValueImpl(java.net.URI uri) {
        super(uri);
    }
}

class SynArgImpl extends URIValue implements SynArg {

    public SynArgImpl(java.net.URI uri) {
        super(uri);
    }
}
