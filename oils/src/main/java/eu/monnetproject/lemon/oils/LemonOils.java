package eu.monnetproject.lemon.oils;

import eu.monnetproject.ontology.DatatypeProperty;
import eu.monnetproject.ontology.LiteralValue;
import eu.monnetproject.ontology.ObjectProperty;
import eu.monnetproject.ontology.Ontology;
import java.net.URI;

/**
 * The lemon OILS object for making appropriate OILS objects
 * @author John McCrae
 */
public interface LemonOils {
    /**
     * Make a covariant scalar
     * @param ontology The ontology to insert into
     * @param uri The URI of the class
     * @param boundTo The datatype property it is bound to
     * @param degree The degree of variance
     * @return The scalar object
     */
    CovariantScalar makeCovariantScalar(Ontology ontology, URI uri, DatatypeProperty boundTo, Scalar.Degree degree);
    
    /**
     * Make a covariant scalar
     * @param ontology The ontology to insert into
     * @param uri The URI of the class
     * @param boundTo The datatype property it is bound to
     * @param degree The degree of variance
     * @param comparator The object property defined as the greaterThan property
     * @return The scalar object
     */
    CovariantScalar makeCovariantScalar(Ontology ontology, URI uri, DatatypeProperty boundTo, Scalar.Degree degree, ObjectProperty comparator);
    
    /**
     * Make a contravariant scalar
     * @param ontology The ontology to insert into
     * @param uri The URI of the class
     * @param boundTo The datatype property it is bound to
     * @param degree The degree of variance
     * @return The scalar object
     */
    ContravariantScalar makeContravariantScalar(Ontology ontology, URI uri, DatatypeProperty boundTo, Scalar.Degree degree);
    
    /**
     * Make a contravariant scalar
     * @param ontology The ontology to insert into
     * @param uri The URI of the class
     * @param boundTo The datatype property it is bound to
     * @param degree The degree of variance
     * @param comparator The object property defined as the lessThan property
     * @return The scalar object
     */
    ContravariantScalar makeContravariantScalar(Ontology ontology, URI uri, DatatypeProperty boundTo, Scalar.Degree degree, ObjectProperty comparator);
    
    /**
     * Make a convariant scalar
     * @param ontology The ontology to insert into
     * @param uri The URI of the class
     * @param boundTo The datatype property it is bound to
     * @param threshold The minimum value for class membership
     * @return The scalar object
     */
    CovariantScalar makeCovariantScalar(Ontology ontology, URI uri, DatatypeProperty boundTo, LiteralValue threshold);
    
    /**
     * Make a covariant scalar
     * @param ontology The ontology to insert into
     * @param uri The URI of the class
     * @param boundTo The datatype property it is bound to
     * @param threshold The minimum value for class membership
     * @param comparator The object property defined as the greaterThan property
     * @return The scalar object
     */
    CovariantScalar makeCovariantScalar(Ontology ontology, URI uri, DatatypeProperty boundTo, LiteralValue threshold, ObjectProperty comparator);
    
    /**
     * Make a contravariant scalar
     * @param ontology The ontology to insert into
     * @param uri The URI of the class
     * @param boundTo The datatype property it is bound to
     * @param threshold The minimum value for class membership
     * @return The scalar object
     */
    ContravariantScalar makeContravariantScalar(Ontology ontology, URI uri, DatatypeProperty boundTo, LiteralValue threshold);
    
    /**
     * Make a contravariant scalar
     * @param ontology The ontology to insert into
     * @param uri The URI of the class
     * @param boundTo The datatype property it is bound to
     * @param threshold The minimum value for class membership
     * @param comparator The object property defined as the lessThan property
     * @return The scalar object
     */
    ContravariantScalar makeContravariantScalar(Ontology ontology, URI uri, DatatypeProperty boundTo, LiteralValue threshold, ObjectProperty comparator);
    
    /**
     * Convert a class to a scalar object
     * @param clazz The class to convert
     * @return A scalar object or null if the object is not scalar
     */
    Scalar toScalar(eu.monnetproject.ontology.Class clazz);
    
    /**
     * Make a multivariate relationship class
     * @param ontology The ontology to insert into
     * @param uri The URI of the class
     * @param roles The roles this relationship has (at least three)
     * @return The multivariate relationship object
     */
    MultivariateRelationship makeMultivariateRelationship(Ontology ontology, URI uri, ObjectProperty... roles);
    
    /**
     * Convert a class to a multivariate relationship
     * @param clazz The class to convert
     * @return A multivariate relationship object or null if not a multivariate relationship
     */
    MultivariateRelationship toMultivariateRelationship(eu.monnetproject.ontology.Class clazz);
}
