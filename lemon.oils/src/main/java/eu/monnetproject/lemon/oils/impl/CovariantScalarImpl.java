package eu.monnetproject.lemon.oils.impl;

import eu.monnetproject.lemon.oils.CovariantScalar;
import eu.monnetproject.ontology.Ontology;

/**
 *
 * @author John McCrae
 */
public class CovariantScalarImpl extends ScalarImpl implements CovariantScalar {
    public CovariantScalarImpl(eu.monnetproject.ontology.Class clazz, Ontology ontology) {
        super(clazz,ontology);
    }
}
