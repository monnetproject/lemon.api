package eu.monnetproject.lemon.oils.impl;

import eu.monnetproject.lemon.oils.MultivariateRelationship;
import eu.monnetproject.ontology.AnnotationValue;
import eu.monnetproject.ontology.Class;
import eu.monnetproject.ontology.Individual;
import eu.monnetproject.ontology.OntologyFactory;
import eu.monnetproject.ontology.Property;
import java.net.URI;
import java.util.Collection;
import java.util.HashSet;

/**
 *
 * @author John McCrae
 */
public class MultivariateRelationshipImpl implements MultivariateRelationship {
    private final Class clazz;

    public MultivariateRelationshipImpl(Class clazz) {
        this.clazz = clazz;
    }
    
    
    public Collection<Property> getRoles() {
        OntologyFactory factory = clazz.getOntology().getFactory();
        final Collection<AnnotationValue> annotationValues = 
                clazz.getAnnotationValues(factory.makeAnnotationProperty(URI.create(LemonOilsImpl.LEMON_OILS+"role")));
        HashSet<Property> rval = new HashSet<Property>();
        for(AnnotationValue av : annotationValues) {
            if(av instanceof Individual) {
                rval.add(factory.makeObjectProperty(((Individual)av).getURI()));
            }
        }
        return rval;
    }

    public Class asClass() {
        return clazz;
    }
    
}
