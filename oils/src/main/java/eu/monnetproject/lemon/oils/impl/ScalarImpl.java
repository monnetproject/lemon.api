package eu.monnetproject.lemon.oils.impl;

import eu.monnetproject.lemon.oils.Scalar;
import eu.monnetproject.lemon.oils.Scalar.Degree;
import eu.monnetproject.ontology.AnnotationProperty;
import eu.monnetproject.ontology.AnnotationValue;
import eu.monnetproject.ontology.DatatypeProperty;
import eu.monnetproject.ontology.Individual;
import eu.monnetproject.ontology.LiteralValue;
import eu.monnetproject.ontology.ObjectProperty;
import eu.monnetproject.ontology.Ontology;
import eu.monnetproject.ontology.OntologyFactory;
import java.net.URI;
import java.util.Collection;

/**
 *
 * @author John McCrae
 */
public class ScalarImpl implements Scalar {
    private final eu.monnetproject.ontology.Class clazz;
    private final AnnotationProperty boundTo,degree,threshold,comparator;
    private final OntologyFactory factory;

    public ScalarImpl(eu.monnetproject.ontology.Class clazz, Ontology ontology) {
        this.clazz = clazz;
        factory = ontology.getFactory();
        boundTo = factory.makeAnnotationProperty(URI.create(LemonOilsImpl.LEMON_OILS+"boundTo"));
        degree = factory.makeAnnotationProperty(URI.create(LemonOilsImpl.LEMON_OILS+"degree"));
        threshold = factory.makeAnnotationProperty(URI.create(LemonOilsImpl.LEMON_OILS+"threshold"));
        comparator = factory.makeAnnotationProperty(URI.create(LemonOilsImpl.LEMON_OILS+"comparator"));
    }
    
    public DatatypeProperty getBoundTo() {
        Collection<AnnotationValue> values = clazz.getAnnotationValues(boundTo);
        if(values.isEmpty()) {
            return null;
        } else {
            return factory.makeDatatypeProperty(((Individual)values.iterator().next()).getURI());
        }
    }

    public Degree getDegree() {
        Collection<AnnotationValue> values = clazz.getAnnotationValues(degree);
        if(values.isEmpty()) {
            return null;
        } else {
            String uri = ((Individual)values.iterator().next()).getURI().toString();
            if(uri.endsWith("weak")) {
                return Degree.weak;
            } else if(uri.endsWith("medium")) {
                return Degree.medium;
            } else if(uri.endsWith("strong")) {
                return Degree.strong;
            } else if(uri.endsWith("veryStrong")) {
                return Degree.veryStrong;
            } else {
                return null;
            }
        }
    }

    public LiteralValue getThreshold() {
        Collection<AnnotationValue> values = clazz.getAnnotationValues(threshold);
        if(values.isEmpty()) {
            return null;
        } else {
            return (LiteralValue)values.iterator().next();
        }
    }

    public ObjectProperty getComparator() {
        Collection<AnnotationValue> values = clazz.getAnnotationValues(comparator);
        if(values.isEmpty()) {
            return null;
        } else {
            return factory.makeObjectProperty(((Individual)values.iterator().next()).getURI());
        }
    }

    public eu.monnetproject.ontology.Class asClass() {
        return clazz;
    }
    
}
