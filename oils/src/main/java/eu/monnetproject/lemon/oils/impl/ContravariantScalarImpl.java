/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.monnetproject.lemon.oils.impl;

import eu.monnetproject.lemon.oils.ContravariantScalar;
import eu.monnetproject.ontology.Ontology;

/**
 *
 * @author John McCrae
 */
public class ContravariantScalarImpl extends ScalarImpl implements ContravariantScalar {
    public ContravariantScalarImpl(eu.monnetproject.ontology.Class clazz, Ontology ontology) {
        super(clazz,ontology);
    }
}
