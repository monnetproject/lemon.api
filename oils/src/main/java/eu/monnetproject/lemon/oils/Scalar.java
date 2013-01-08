package eu.monnetproject.lemon.oils;

import eu.monnetproject.ontology.DatatypeProperty;
import eu.monnetproject.ontology.LiteralValue;
import eu.monnetproject.ontology.ObjectProperty;


/**
 * A class, whose membership is based on a a datatype property
 * @author John McCrae
 */
public interface Scalar {
    /**
     * The degree to which the binding between the class and the datatype property
     * holds, specified fuzzily as the percentage of known examples belong to the
     * class
     */
    enum Degree {
        /** >50% */
        weak,
        /** >70% */
        medium,
        /** >90% */
        strong,
        /** >99% */
        veryStrong
    }
    
    /**
     * @return The property this class is defined by
     */
    DatatypeProperty getBoundTo();
    
    /**
     * 
     * @return The degree of the binding, or null if not degree is specified
     */
    Degree getDegree();
    
    /**
     * 
     * @return A threshold for class membership, or null if no threshold is specified
     */
    LiteralValue getThreshold();
    
    /**
     * 
     * @return  The object property that indicates greaterThan for instances of this
     * class or null if no comparator is specified
     */
    ObjectProperty getComparator();
    
    /**
     * 
     * @return Get this as a Class Object
     */
    eu.monnetproject.ontology.Class asClass();
}
