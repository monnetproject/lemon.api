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

import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Deactivate;
import eu.monnetproject.lemon.LinguisticOntology;
import eu.monnetproject.lemon.URIElement;
import eu.monnetproject.lemon.URIValue;
import eu.monnetproject.lemon.model.Condition;
import eu.monnetproject.lemon.model.Definition;
import eu.monnetproject.lemon.model.Edge;
import eu.monnetproject.lemon.model.FormVariant;
import eu.monnetproject.lemon.model.LemonElement;
import eu.monnetproject.lemon.model.LemonPredicate;
import eu.monnetproject.lemon.model.LexicalVariant;
import eu.monnetproject.lemon.model.Property;
import eu.monnetproject.lemon.model.PropertyValue;
import eu.monnetproject.lemon.model.Representation;
import eu.monnetproject.lemon.model.SenseContext;
import eu.monnetproject.lemon.model.SenseRelation;
import eu.monnetproject.lemon.model.SynArg;
import eu.monnetproject.util.ResourceFinder;
import java.util.*;
import eu.monnetproject.util.Logging;
import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import eu.monnetproject.util.Logger;
import org.openrdf.repository.*;
import org.openrdf.repository.sail.*;
import org.openrdf.model.*;
import org.openrdf.rio.RDFFormat;
import org.openrdf.sail.memory.MemoryStore;

@aQute.bnd.annotation.component.Component(provide = LinguisticOntology.class)
public class LexInfo implements LinguisticOntology {

    private final Logger log = Logging.getLogger(this);
    private static final String lemonURI = "http://www.monnet-project.eu/lemon#";
    private static final String lexinfoURI = "http://www.lexinfo.net/ontology/2.0/lexinfo#";
    private HashMap<String, Property> propertys;
    private HashMap<String, PropertyValue> propertyValues;
    private HashMap<String, Condition> conditions;
    private HashMap<String, SenseContext> contexts;
    private HashMap<String, Definition> definitions;
    private HashMap<String, Edge> edges;
    private HashMap<String, Representation> representations;
    private HashMap<String, SenseRelation> senseRelations;
    private HashMap<String, LexicalVariant> lexicalVariants;
    private HashMap<String, FormVariant> formVariants;
    private HashMap<String, java.net.URI> frameClasses;
    private HashMap<Property, Collection<PropertyValue>> valuesByProp;
    private HashMap<java.net.URI, Collection<SynArg>> synArgsByFrame;
    private HashMap<String, SynArg> synArgs;

    public LexInfo() {
        setRepository(new SailRepository(new MemoryStore()));
        try {
            repo.initialize();
        } catch (RepositoryException ex) {
            log.severe(ex.toString());
        }
        start();
    }

    public LexInfo(Repository repository) {
        setRepository(repository);
        start();
    }
    private Repository repo;
    private RepositoryConnection model;
    private ValueFactory factory;

    private void setRepository(Repository repo) {
        this.repo = repo;
    }
    public static LexInfo instance;

    @Activate
    public final void start() {
        try {
            model = repo.getConnection();
            InputStream is = ResourceFinder.getResource("/lexinfo.owl").openStream();
            if (is == null) {
                throw new RuntimeException("Could not load LexInfo OWL file");
            }
            model.add(is, lexinfoURI, RDFFormat.RDFXML);
            factory = model.getValueFactory();
            propertys = readSubProps(factory.createURI(lemonURI, "property"),
                    PropertyImpl.class, Property.class);
            propertyValues = readElems(readSubClasses(factory.createURI(lemonURI, "PropertyValue")),
                    PropertyValueImpl.class, PropertyValue.class);
            conditions = readSubProps(factory.createURI(lemonURI, "condition"),
                    ConditionPredicateImpl.class, Condition.class);
            contexts = readElems(readSubClasses(factory.createURI(lemonURI, "Context")),
                    ContextImpl.class, SenseContext.class);
            definitions = readSubProps(factory.createURI(lemonURI, "definition"),
                    DefinitionPredicateImpl.class, Definition.class);
            edges = readSubProps(factory.createURI(lemonURI, "edge"),
                    EdgeImpl.class, Edge.class);
            representations = readSubProps(factory.createURI(lemonURI, "representation"),
                    RepresentationImpl.class, Representation.class);
            senseRelations = readSubProps(factory.createURI(lemonURI, "senseRelation"), SenseRelationImpl.class, SenseRelation.class);
            lexicalVariants = readSubProps(factory.createURI(lemonURI, "lexicalVariant"), LexicalVariantImpl.class, LexicalVariant.class);
            formVariants = readSubProps(factory.createURI(lemonURI, "formVariant"), FormVariantImpl.class, FormVariant.class);
            synArgs = readSubProps(factory.createURI(lemonURI, "synArg"), SynArgImpl.class, SynArg.class);
            HashMap<String, Resource> frameClasses2 = readSubClasses(factory.createURI(lemonURI, "Frame"));
            frameClasses = new HashMap<String, java.net.URI>();
            for (Map.Entry<String, Resource> fc2 : frameClasses2.entrySet()) {
                frameClasses.put(fc2.getKey(), java.net.URI.create(fc2.getValue().stringValue()));
            }

            readValuesByProp();
            readArgsByFrame();
        } catch (RepositoryException x) {
            throw new RuntimeException(x);
        } catch (IOException x) {
            throw new RuntimeException(x);
        } catch (org.openrdf.rio.RDFParseException x) {
            throw new RuntimeException(x);
        }
    }

    @Deactivate
    public void deactivate() {
        try {
            if (model != null) {
                model.close();
            }
        } catch (Exception x) {
            Logging.stackTrace(log, x);
        }
    }

    private <C extends LemonPredicate, C2 extends C> HashMap<String, C> readSubProps(Resource property, Class<C2> implementation, Class<C> defn) throws RepositoryException {
        try {
            List<Statement> triples = model.getStatements(null, factory.createURI("http://www.w3.org/2000/01/rdf-schema#subPropertyOf"), property, false).asList();
            Constructor<C2> constructor = implementation.getConstructor(java.net.URI.class);
            HashMap<String, C> rval = new HashMap<String, C>();

            for (Statement triple : triples) {
                Resource res = triple.getSubject();
                java.net.URI uri = java.net.URI.create(res.stringValue());
                rval.put(uri.getFragment(), constructor.newInstance(uri));
                rval.putAll(readSubProps(res, implementation, defn));
            }
            return rval;
        } catch (NoSuchMethodException x) {
            log.stackTrace(x);
            return new HashMap<String, C>();
        } catch (InstantiationException x) {
            Logging.stackTrace(log, x);
            return new HashMap<String, C>();
        } catch (IllegalAccessException x) {
            Logging.stackTrace(log, x);
            return new HashMap<String, C>();
        } catch (InvocationTargetException x) {
            Logging.stackTrace(log, x);
            return new HashMap<String, C>();
        }
    }

    private HashMap<String, Resource> readSubClasses(Resource clazz) throws RepositoryException {
        List<Statement> triples = model.getStatements(null,
                factory.createURI("http://www.w3.org/2000/01/rdf-schema#subClassOf"), clazz, false).asList();
        HashMap<String, Resource> rval = new HashMap<String, Resource>();

        for (Statement triple : triples) {
            Resource res = triple.getSubject();
            java.net.URI uri = java.net.URI.create(res.stringValue());
            rval.put(uri.getFragment(), res);
            rval.putAll(readSubClasses(res));
        }
        return rval;
    }

    private <C extends LemonElement, C2 extends C> HashMap<String, C> readElems(HashMap<String, Resource> clazzSet,
            Class<C2> implementation, Class<C> inter) throws RepositoryException {
        try {
            Constructor<C2> constructor = implementation.getConstructor(java.net.URI.class);
            HashMap<String, C> rval = new HashMap<String, C>();
            for (Resource clazz : clazzSet.values()) {
                List<Statement> triples = model.getStatements(null, factory.createURI("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"), clazz, false).asList();

                for (Statement triple : triples) {
                    Resource res = triple.getSubject();
                    java.net.URI uri = java.net.URI.create(res.stringValue());
                    rval.put(uri.getFragment(), constructor.newInstance(uri));
                }
            }
            return rval;
        } catch (NoSuchMethodException x) {
            Logging.stackTrace(log, x);
            return new HashMap<String, C>();
        } catch (InstantiationException x) {
            Logging.stackTrace(log, x);
            return new HashMap<String, C>();
        } catch (IllegalAccessException x) {
            Logging.stackTrace(log, x);
            return new HashMap<String, C>();
        } catch (InvocationTargetException x) {
            Logging.stackTrace(log, x);
            return new HashMap<String, C>();
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

    private void readValuesByProp() throws RepositoryException {
        valuesByProp = new HashMap<Property, Collection<PropertyValue>>();
        for (Property prop : propertys.values()) {
            List<Statement> triples = model.getStatements(factory.createURI(prop.getURI().toString()),
                    factory.createURI("http://www.w3.org/2000/01/rdf-schema#range"),
                    null, false).asList();
            if (triples.isEmpty()) {
                log.warning("No range defined for " + prop.getURI());
                continue;
            }
            Resource range = ((Resource) triples.iterator().next().getObject());
            HashMap<String, Resource> clazzSet = readSubClasses(range);
            clazzSet.put("", range);
            valuesByProp.put(prop, readElems(clazzSet, PropertyValueImpl.class, PropertyValue.class).values());
        }
    }

    private void readArgsByFrame() throws RepositoryException {
        synArgsByFrame = new HashMap<java.net.URI, Collection<SynArg>>();
        for (java.net.URI frameClass : frameClasses.values()) {
            synArgsByFrame.put(frameClass, readArgFrameClass(frameClass));
        }
    }

    private HashSet<SynArg> readArgFrameClass(java.net.URI frameClass) throws RepositoryException {
        List<Statement> triples = model.getStatements(factory.createURI(frameClass.toString()),
                factory.createURI("http://www.w3.org/2002/07/owl#equivalentClass"),
                null, false).asList();
        if (triples.isEmpty()) {
            return new HashSet<SynArg>();
        }
        Resource eqClass = (Resource) triples.iterator().next().getObject();
        triples = model.getStatements(eqClass,
                factory.createURI("http://www.w3.org/2002/07/owl#intersectionOf"),
                null, false).asList();
        HashSet<SynArg> rval = new HashSet<SynArg>();
        if (triples.isEmpty()) {
            readOneSynArg(eqClass, rval);
            return rval;
        }
        Resource intersection = (Resource) triples.iterator().next().getObject();
        while (true) {
            triples = model.getStatements(intersection,
                    factory.createURI("http://www.w3.org/1999/02/22-rdf-syntax-ns#first"),
                    null, false).asList();
            if (triples.isEmpty()) {
                break;
            }
            readOneSynArg((Resource) triples.iterator().next().getObject(), rval);
            triples = model.getStatements(intersection,
                    factory.createURI("http://www.w3.org/1999/02/22-rdf-syntax-ns#rest"),
                    null, false).asList();
            if (triples.isEmpty()) {
                throw new IllegalArgumentException("Invalid RDF list");
            }
            intersection = (Resource) triples.iterator().next().getObject();
        }
        return rval;
    }

    private void readOneSynArg(Resource classStat, HashSet<SynArg> rval) throws RepositoryException {
        List<Statement> triples = model.getStatements(classStat,
                factory.createURI("http://www.w3.org/2002/07/owl#onProperty"),
                null, false).asList();
        if (!triples.isEmpty()) {
            Resource synArg = (Resource) triples.iterator().next().getObject();
            triples = model.getStatements(classStat,
                    factory.createURI("http://www.w3.org/2002/07/owl#cardinality"),
                    null, false).asList();
            if (triples.isEmpty()) {
                rval.add(new SynArgImpl(java.net.URI.create(synArg.stringValue())));
            } else {
                Literal lit = (Literal) triples.iterator().next().getObject();
                if (!lit.stringValue().equals("0")) {
                    rval.add(new SynArgImpl(java.net.URI.create(synArg.stringValue())));
                } // else { do nothing } 
            }
        } else {
            rval.addAll(readArgFrameClass(java.net.URI.create(classStat.stringValue())));
        }
    }

    /**
     * Get a property reference by its name
     * @throws IllegalArgumentException If the name is not recognised by the ontology
     */
    public Property getProperty(String name) {
        return getCheck(propertys, name);
    }

    /**
     * Get the set of properties listed in the linguistics ontology
     */
    public Collection<Property> getProperties() {
        return propertys.values();
    }

    /**
     * Get a property value by its name
     * @throws IllegalArgumentException If the name is not recognised by the ontology
     */
    public PropertyValue getPropertyValue(String name) {
        return getCheck(propertyValues, name);
    }

    /**
     * Get the set of property values listed in the linguistic ontology for a given property 
     */
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
    public SynArg getSynArg(String name) {
        return getCheck(synArgs, name);
    }

    /**
     * Get the set of properties listed in the linguistics ontology
     */
    public Collection<SynArg> getSynArgs() {
        return synArgs.values();
    }

    /**
     * Get the set of URI values listed in the linguistic ontology for a given URI 
     */
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
    public Condition getCondition(String name) {
        return getCheck(conditions, name);
    }

    /**
     * Get the set of condition predicates listed in the linguistic ontology
     */
    public Collection<Condition> getConditions() {
        return conditions.values();
    }

    /**
     * Get a context predicate by its name 
     * @throws IllegalArgumentException If the name is not recognised by the ontology
     */
    public SenseContext getContext(String name) {
        return getCheck(contexts, name);
    }

    /**
     * Get the set of context predicates listed in the linguistic ontology
     */
    public Collection<SenseContext> getContexts() {
        return contexts.values();
    }

    /**
     * Get a definition predicate by its name 
     * @throws IllegalArgumentException If the name is not recognised by the ontology
     */
    public Definition getDefinition(String name) {
        return getCheck(definitions, name);
    }

    /**
     * Get the set of definition predicates listed in the linguistic ontology
     */
    public Collection<Definition> getDefinitions() {
        return definitions.values();
    }

    /**
     * Get a edge relation type by its name 
     * @throws IllegalArgumentException If the name is not recognised by the ontology
     */
    public Edge getEdge(String name) {
        return getCheck(edges, name);
    }

    /**
     * Get the set of edge relation types listed in the linguistic ontology
     */
    public Collection<Edge> getEdge() {
        return edges.values();
    }

    /**
     * Get a form variant relation type by its name 
     * @throws IllegalArgumentException If the name is not recognised by the ontology
     */
    public FormVariant getFormVariant(String name) {
        return getCheck(formVariants, name);
    }

    /**
     * Get the set of form variant relation types listed in the linguistic ontology
     */
    public Collection<FormVariant> getFormVariant() {
        return formVariants.values();
    }

    /**
     * Get a lexical variant relation type by its name 
     * @throws IllegalArgumentException If the name is not recognised by the ontology
     */
    public LexicalVariant getLexicalVariant(String name) {
        return getCheck(lexicalVariants, name);
    }

    /**
     * Get the set of lexical variant relation types listed in the linguistic ontology
     */
    public Collection<LexicalVariant> getLexicalVariant() {
        return lexicalVariants.values();
    }

    /**
     * Get a representation relation type by its name 
     * @throws IllegalArgumentException If the name is not recognised by the ontology
     */
    public Representation getRepresentation(String name) {
        return getCheck(representations, name);
    }

    /**
     * Get the set of representation relation types listed in the linguistic ontology
     */
    public Collection<Representation> getRepresentation() {
        return representations.values();
    }

    /**
     * Get a sense relation type by its name 
     * @throws IllegalArgumentException If the name is not recognised by the ontology
     */
    public SenseRelation getSenseRelation(String name) {
        return getCheck(senseRelations, name);
    }

    /**
     * Get the set of sense relation types listed in the linguistic ontology
     */
    public Collection<SenseRelation> getSenseRelation() {
        return senseRelations.values();
    }

    /**
     * Get all possible frame classes
     */
    public Collection<java.net.URI> getFrameClasses() {
        return frameClasses.values();
    }

    /**
     * Get a frame class by name
     * @throws IllegalArgumentException If the name is not recognised by the ontology
     */
    public java.net.URI getFrameClass(String name) {
        return getCheck(frameClasses, name);
    }

    public Map<Property, Collection<PropertyValue>> getPropertyMap(String... strings) {
        Map<Property, Collection<PropertyValue>> rval = new HashMap<Property, Collection<PropertyValue>>();
        for(int i = 0; i < strings.length; i += 2) {
            final Property property = getProperty(strings[i]);
            if(!rval.containsKey(property)) {
                rval.put(property, new LinkedList<PropertyValue>());
            }
            rval.get(property).add(getPropertyValue(strings[i+1]));
        }
        return rval;
    }
}
/*
class JARDataSource implements DataSource {
final String resourceName;

public JARDataSource(String resourceName) {
this.resourceName = resourceName;
}

public File asFile() { throw new UnsupportedOperationException(); }

public InputStream asInputStream() { 
return this.getClass().getClassLoader().getResourceAsStream(resourceName); 
}

public URL asURL() { 
return this.getClass().getClassLoader().getResource(resourceName); 
}

public String getMIMEType() { return "application/rdf+xml"; }	
}*/

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
