/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.monnetproject.lemon.oils.impl;

import eu.monnetproject.lemon.oils.ContravariantScalar;
import eu.monnetproject.lemon.oils.CovariantScalar;
import eu.monnetproject.lemon.oils.LemonOils;
import eu.monnetproject.lemon.oils.MultivariateRelationship;
import eu.monnetproject.lemon.oils.Scalar;
import eu.monnetproject.lemon.oils.Scalar.Degree;
import eu.monnetproject.ontology.AnnotationProperty;
import eu.monnetproject.ontology.AnnotationValue;
import eu.monnetproject.ontology.Class;
import eu.monnetproject.ontology.DatatypeProperty;
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
public class LemonOilsImpl implements LemonOils {

    public static final String LEMON_OILS = "http://lemon-model.net/oils#";
    
    public CovariantScalar makeCovariantScalar(Ontology ontlg, URI uri, DatatypeProperty dp, Degree degree) {
        OntologyFactory factory = ontlg.getFactory();
        Class c = factory.makeClass(uri);
        AnnotationProperty boundTo = factory.makeAnnotationProperty(URI.create(LEMON_OILS+"boundTo"));
        c.addAnnotation(boundTo, factory.makeLiteral(dp.getURI().toString()));
        AnnotationProperty degreeAP = factory.makeAnnotationProperty(URI.create(LEMON_OILS+"degree"));
        c.addAnnotation(degreeAP, factory.makeLiteral(LEMON_OILS + degree.toString()));
        return new CovariantScalarImpl(c, ontlg);
    }

    public CovariantScalar makeCovariantScalar(Ontology ontlg, URI uri, DatatypeProperty dp, Degree degree, ObjectProperty op) {
        OntologyFactory factory = ontlg.getFactory();
        Class c = factory.makeClass(uri);
        AnnotationProperty boundTo = factory.makeAnnotationProperty(URI.create(LEMON_OILS+"boundTo"));
        c.addAnnotation(boundTo, factory.makeLiteral(dp.getURI().toString()));
        AnnotationProperty degreeAP = factory.makeAnnotationProperty(URI.create(LEMON_OILS+"degree"));
        c.addAnnotation(degreeAP, factory.makeLiteral(LEMON_OILS + degree.toString()));
        AnnotationProperty comparator = factory.makeAnnotationProperty(URI.create(LEMON_OILS+"comparator"));
        c.addAnnotation(comparator, factory.makeLiteral(op.getURI().toString()));
        return new CovariantScalarImpl(c, ontlg);
    }

    public ContravariantScalar makeContravariantScalar(Ontology ontlg,  URI uri, DatatypeProperty dp, Degree degree) {
         OntologyFactory factory = ontlg.getFactory();
        Class c = factory.makeClass(uri);
        AnnotationProperty boundTo = factory.makeAnnotationProperty(URI.create(LEMON_OILS+"boundTo"));
        c.addAnnotation(boundTo, factory.makeLiteral(dp.getURI().toString()));
        AnnotationProperty degreeAP = factory.makeAnnotationProperty(URI.create(LEMON_OILS+"degree"));
        c.addAnnotation(degreeAP, factory.makeLiteral(LEMON_OILS + degree.toString()));
        return new ContravariantScalarImpl(c, ontlg);
    }

    public ContravariantScalar makeContravariantScalar(Ontology ontlg, URI uri, DatatypeProperty dp, Degree degree, ObjectProperty op) {
        OntologyFactory factory = ontlg.getFactory();
        Class c = factory.makeClass(uri);
        AnnotationProperty boundTo = factory.makeAnnotationProperty(URI.create(LEMON_OILS+"boundTo"));
        c.addAnnotation(boundTo, factory.makeLiteral(dp.getURI().toString()));
        AnnotationProperty degreeAP = factory.makeAnnotationProperty(URI.create(LEMON_OILS+"degree"));
        c.addAnnotation(degreeAP, factory.makeLiteral(LEMON_OILS + degree.toString()));
        AnnotationProperty comparator = factory.makeAnnotationProperty(URI.create(LEMON_OILS+"comparator"));
        c.addAnnotation(comparator, factory.makeLiteral(op.getURI().toString()));
        return new ContravariantScalarImpl(c, ontlg);
    }

    public CovariantScalar makeCovariantScalar(Ontology ontlg, URI uri, DatatypeProperty dp, LiteralValue lv) {
        OntologyFactory factory = ontlg.getFactory();
        Class c = factory.makeClass(uri);
        AnnotationProperty boundTo = factory.makeAnnotationProperty(URI.create(LEMON_OILS+"boundTo"));
        c.addAnnotation(boundTo, factory.makeLiteral(dp.getURI().toString()));
        AnnotationProperty degreeAP = factory.makeAnnotationProperty(URI.create(LEMON_OILS+"threshold"));
        c.addAnnotation(degreeAP, lv);
        return new CovariantScalarImpl(c, ontlg);
    }

    public CovariantScalar makeCovariantScalar(Ontology ontlg, URI uri, DatatypeProperty dp, LiteralValue lv, ObjectProperty op) {
        OntologyFactory factory = ontlg.getFactory();
        Class c = factory.makeClass(uri);
        AnnotationProperty boundTo = factory.makeAnnotationProperty(URI.create(LEMON_OILS+"boundTo"));
        c.addAnnotation(boundTo, factory.makeLiteral(dp.getURI().toString()));
        AnnotationProperty degreeAP = factory.makeAnnotationProperty(URI.create(LEMON_OILS+"threshold"));
        c.addAnnotation(degreeAP, lv);
        AnnotationProperty comparator = factory.makeAnnotationProperty(URI.create(LEMON_OILS+"comparator"));
        c.addAnnotation(comparator, factory.makeLiteral(op.getURI().toString()));
        return new CovariantScalarImpl(c, ontlg);
    }

    public ContravariantScalar makeContravariantScalar(Ontology ontlg, URI uri, DatatypeProperty dp, LiteralValue lv) {
        OntologyFactory factory = ontlg.getFactory();
        Class c = factory.makeClass(uri);
        AnnotationProperty boundTo = factory.makeAnnotationProperty(URI.create(LEMON_OILS+"boundTo"));
        c.addAnnotation(boundTo, factory.makeLiteral(dp.getURI().toString()));
        AnnotationProperty degreeAP = factory.makeAnnotationProperty(URI.create(LEMON_OILS+"threshold"));
        c.addAnnotation(degreeAP, lv);
        return new ContravariantScalarImpl(c, ontlg);
    }

    public ContravariantScalar makeContravariantScalar(Ontology ontlg, URI uri, DatatypeProperty dp, LiteralValue lv, ObjectProperty op) {
        OntologyFactory factory = ontlg.getFactory();
        Class c = factory.makeClass(uri);
        AnnotationProperty boundTo = factory.makeAnnotationProperty(URI.create(LEMON_OILS+"boundTo"));
        c.addAnnotation(boundTo, factory.makeLiteral(dp.getURI().toString()));
        AnnotationProperty degreeAP = factory.makeAnnotationProperty(URI.create(LEMON_OILS+"threshold"));
        c.addAnnotation(degreeAP, lv);
        AnnotationProperty comparator = factory.makeAnnotationProperty(URI.create(LEMON_OILS+"comparator"));
        c.addAnnotation(comparator, factory.makeLiteral(op.getURI().toString()));
        return new ContravariantScalarImpl(c, ontlg);
    }

    public Scalar toScalar(Class type) {
        OntologyFactory factory = type.getOntology().getFactory();
        AnnotationProperty boundTo = factory.makeAnnotationProperty(URI.create(LEMON_OILS+"boundTo"));
        final Collection<AnnotationValue> annotationValues = type.getAnnotationValues(boundTo);
        if(annotationValues.isEmpty()) {
            return null;
        } else {
            final Collection<Class> superClasses = type.getSuperClassOf();
            for(Class cl : superClasses) {
                if(cl.getURI().toString().equals(LEMON_OILS+"CovariantScalar")) {
                    return new CovariantScalarImpl(cl, cl.getOntology());
                } else if(cl.getURI().toString().equals(LEMON_OILS+"ContravariantScalar")) {
                    return new ContravariantScalarImpl(cl, cl.getOntology());
                }
            }
            return null;
        } 
    }

    public MultivariateRelationship makeMultivariateRelationship(Ontology ontlg, URI uri, ObjectProperty... ops) {
        final OntologyFactory factory = ontlg.getFactory();
        final AnnotationProperty roleAP = factory.makeAnnotationProperty(URI.create(LEMON_OILS+"role"));
        final Class rval = factory.makeClass(uri);
        for(ObjectProperty op : ops) {
            rval.addAnnotation(roleAP, factory.makeIndividual(op.getURI()));
        }
        return new MultivariateRelationshipImpl(rval);
    }

    public MultivariateRelationship toMultivariateRelationship(Class type) {
        final OntologyFactory factory = type.getOntology().getFactory();
        final AnnotationProperty roleAP = factory.makeAnnotationProperty(URI.create(LEMON_OILS+"role"));
        if(type.getAnnotationValues(roleAP).isEmpty()) {
            return null;
        } else {
            return new MultivariateRelationshipImpl(type);
        }
    }
    
}
