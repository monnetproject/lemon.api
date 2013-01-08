package eu.monnetproject.lemon.oils;

import eu.monnetproject.ontology.Property;
import java.util.Collection;

/**
 * A class of relationship between more than 2 entities
 * 
 * @author John McCrae
 */
public interface MultivariateRelationship  {
    /**
     * @return The roles this multivariate property works over
     */
    Collection<Property> getRoles();
    
    /**
     * @return This relationship as an ontology class
     */
    eu.monnetproject.ontology.Class asClass();
}
