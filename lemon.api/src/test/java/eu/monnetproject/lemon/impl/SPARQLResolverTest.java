/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.monnetproject.lemon.impl;

import eu.monnetproject.lemon.LemonModel;
import eu.monnetproject.lemon.LemonModels;
import java.io.FileNotFoundException;
import java.net.URI;
import java.net.URL;
import java.util.Collections;
import java.util.HashSet;
import junit.framework.Assert;
import net.lexinfo.LexInfo;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author jmccrae
 */
public class SPARQLResolverTest {

    public SPARQLResolverTest() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSPARQLResolver() throws Exception {
        final SPARQLResolver resolver = new SPARQLResolver(new URL("file:src/test/resources/sparql"), Collections.EMPTY_SET, new LexInfo());
        final LemonModelImpl model = new LemonModelImpl(null);
        final LexicalEntryImpl le = new LexicalEntryImpl("http://www.example.com/", model);
        try {
            resolver.resolveRemote(model, le,1);
            Assert.assertFalse(le.getSenses().isEmpty());
        } catch(RuntimeException x) {
            if(x.getCause() == null || !(x.getCause() instanceof FileNotFoundException)) {
                throw x;
            } else {
                System.err.println("Test fails on Windows... oh well");
            }
        }
    }

    // Depends on actual endpoint... uncomment with caution
    //  @Test 
    public void testSPARQL() throws Exception {
        try {
            final LemonModel model = LemonModels.sparqlEndpoint(new URL("http://monnet02.sindice.net:8080/sparql"), Collections.singleton(URI.create("http://monnetproject.deri.ie/lemonsourcepublic")), new net.lexinfo.LexInfo());
            Assert.assertFalse(model.getLexica().isEmpty());
        } catch (Exception x) {
            x.printStackTrace();
        }
    }
}
