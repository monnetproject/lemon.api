/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.monnetproject.lemon.impl;

import eu.monnetproject.lemon.LemonFactory;
import eu.monnetproject.lemon.LemonModel;
import eu.monnetproject.lemon.LemonModels;
import eu.monnetproject.lemon.LemonSerializer;
import eu.monnetproject.lemon.LinguisticOntology;
import eu.monnetproject.lemon.model.Component;
import eu.monnetproject.lemon.model.LexicalEntry;
import eu.monnetproject.lemon.model.Lexicon;
import eu.monnetproject.lemon.model.MorphPattern;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import net.lexinfo.LexInfo;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.BeforeClass;

/**
 *
 * @author jmccrae
 */
public class LemonModelImplTest {

    public LemonModelImplTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getLexica method, of class LemonModelImpl.
     */
    @Test
    public void testGetLexica() {
        System.out.println("getLexica");
        LemonModelImpl instance =new LemonModelImpl(null);
        instance.addLexicon(URI.create("file:test#lexicon"), "en");
        assertFalse(instance.getLexica().isEmpty());
    }

    /**
     * Test of getContext method, of class LemonModelImpl.
     */
    @Test
    public void testGetContext() {
        System.out.println("getContext");
        LemonModelImpl instance =new LemonModelImpl(null);
        URI expResult = null;
        URI result = instance.getContext();
        assertEquals(expResult, result);
    }

    /**
     * Test of getFactory method, of class LemonModelImpl.
     */
    @Test
    public void testGetFactory() {
        System.out.println("getFactory");
        LemonModelImpl instance =new LemonModelImpl(null);
        LemonFactory result = instance.getFactory();
    }

    /**
     * Test of addLexicon method, of class LemonModelImpl.
     */
    @Test
    public void testAddLexicon() {
        System.out.println("addLexicon");
        LemonModelImpl instance =new LemonModelImpl(null);
        instance.addLexicon(URI.create("file:test#lexicon"), "en");
        assertFalse(instance.getLexica().isEmpty());
    }

    /**
     * Test of removeLexicon method, of class LemonModelImpl.
     */
    @Test
    public void testRemoveLexicon() {
        System.out.println("removeLexicon");
        LemonModelImpl instance =new LemonModelImpl(null);
        instance.addLexicon(URI.create("file:test#lexicon"), "en");
        assertFalse(instance.getLexica().isEmpty());
        instance.removeLexicon(instance.getLexica().iterator().next());
        assertTrue(instance.getLexica().isEmpty());
    }

    /**
     * Test of addPattern method, of class LemonModelImpl.
     */
    @Test
    public void testAddPattern() {
        System.out.println("addPattern");
        LemonModelImpl instance =new LemonModelImpl(null);
        MorphPattern pattern = instance.getFactory().makeMorphPattern();
        instance.addPattern(pattern);
        assertFalse(instance.getPatterns().isEmpty());
    }

    /**
     * Test of getPatterns method, of class LemonModelImpl.
     */
    @Test
    public void testGetPatterns() {
        System.out.println("getPatterns");
        LemonModelImpl instance =new LemonModelImpl(null);
        MorphPattern pattern = instance.getFactory().makeMorphPattern();
        instance.addPattern(pattern);
        assertFalse(instance.getPatterns().isEmpty());
    }

    /**
     * Test of query method, of class LemonModelImpl.
     */
    @Test
    public void testQuery() {
        System.out.println("query");
        // will fail
    }

    /**
     * Test of merge method, of class LemonModelImpl.
     */
    @Test
    public void testMerge() {
        System.out.println("merge");
        LemonModelImpl instance =new LemonModelImpl(null);
        final Lexicon lexicon = instance.addLexicon(URI.create("file:test#lexicon"), "en");
        final LexicalEntry from = LemonModels.addEntryToLexicon(lexicon, URI.create("file:test#lexicon/test1"), "test", null);
        from.addOtherForm(instance.getFactory().makeForm());
        final LexicalEntry to = LemonModels.addEntryToLexicon(lexicon, URI.create("file:test#lexicon/test2"), "test", null);
        instance.merge(from, to);
        assertFalse(to.getOtherForms().isEmpty());
    }

    /**
     * Test of purgeLexicon method, of class LemonModelImpl.
     */
    @Test
    public void testPurgeLexicon() {
        System.out.println("purgeLexicon");
        LinguisticOntology lo = new LexInfo();
        LemonModelImpl instance =new LemonModelImpl(null);
        Lexicon lexicon = instance.addLexicon(URI.create("file:test#lexicon"), "en");
        final LexicalEntry from = LemonModels.addEntryToLexicon(lexicon, URI.create("file:test#lexicon/test1"), "test", null);
        assertFalse(lexicon.getEntrys().isEmpty());
        instance.purgeLexicon(lexicon, lo);
        assertTrue(instance.getLexica().isEmpty());
        lexicon = instance.addLexicon(URI.create("file:test#lexicon"), "en");
        assertTrue(lexicon.getEntrys().isEmpty());
    }

    /**
     * Test of importLexicon method, of class LemonModelImpl.
     */
    @Test
    public void testImportLexicon() {
        System.out.println("importLexicon");
        LemonModelImpl instance2 =new LemonModelImpl(null);
        Lexicon lexicon = instance2.addLexicon(URI.create("file:test#lexicon"), "en");
        final LexicalEntry from = LemonModels.addEntryToLexicon(lexicon, URI.create("file:test#lexicon/test1"), "test", null);
        LinguisticOntology lo = new LexInfo();
        LemonModelImpl instance =new LemonModelImpl(null);
        instance.importLexicon(lexicon, lo);
        assertFalse(instance.getLexica().isEmpty());
        assertFalse(instance.getLexica().iterator().next().getEntrys().isEmpty());
    }

    //@Test
    public void testNullPointers() throws Exception {
        final LemonModel model = LemonModels.sparqlEndpoint(new URL("http://monnet02.sindice.net:8080/sparql"), Collections.singleton(URI.create("http://monnetproject.deri.ie/lemonsourceuser/john")), new LexInfo());
        for (Lexicon lexicon : model.getLexica()) {
            if (lexicon.getURI().toString().endsWith("test2")) {
                final LexicalEntry entry = lexicon.getEntrys().iterator().next();
                final Collection<Object> annos = entry.getAnnotations(URI.create("http://monnetproject.deri.ie/reviewstatus"));
                for(Object o : annos) {
                    assertNotNull(o);
                }
            }
        }
    }
    
  //  @Test
    public void testGetDecomposition() throws Exception {
        final LemonModel model = LemonModels.sparqlEndpoint(new URL("http://monnet02.sindice.net:8080/sparql"),Collections.singleton(URI.create("http://monnetproject.deri.ie/lemonsourceuser/john")),new net.lexinfo.LexInfo());
        final LexicalEntry entry = model.getFactory().makeLexicalEntry(java.net.URI.create("http://monnetproject.deri.ie/lemonsource/lemon__en/Morphological+pattern"));
        final Collection<List<Component>> decomps = entry.getDecompositions();
        System.out.println("decomps:");
        for(List<Component> comps : decomps) {
            for(Component comp : comps) {
                System.out.println(comp.getElement().getURI());
            }
        }
    }
    
   // @Test
    public void testWriteEntry() throws Exception {
        final LemonModel model = LemonModels.sparqlEndpoint(new URL("http://monnet02.sindice.net:8080/sparql"),Collections.singleton(URI.create("http://monnetproject.deri.ie/lemonsourceuser/john")),new net.lexinfo.LexInfo());
        final LexicalEntry entry = model.getFactory().makeLexicalEntry(java.net.URI.create("http://monnetproject.deri.ie/lemonsource/lemon__en/Morphological+pattern"));
        final LemonSerializer serializer = LemonSerializer.newInstance();
        System.out.println("writeEntry");
        final PrintWriter out = new PrintWriter(System.out);
        serializer.writeEntry(model, entry, new LexInfo(), out,false);
        out.flush();
        System.out.println("endWriteEntry");
    }
    
    @Test
    public void testMergeComp() throws Exception {
        final LemonModel model = LemonSerializer.newInstance().create();
        final Lexicon lexicon = model.addLexicon(URI.create("http://www.example.com/lexicon"), "en");
        final LexicalEntry testThis = LemonModels.addEntryToLexicon(lexicon, URI.create("http://www.example.com/lexicon/test+this"), "test this",null);
        final LexicalEntry test = LemonModels.addEntryToLexicon(lexicon, URI.create("http://www.example.com/lexicon/test"), "test",null);
        final LexicalEntry thiz = LemonModels.addEntryToLexicon(lexicon, URI.create("http://www.example.com/lexicon/this"), "this",null);
        final LexicalEntry thiz2 = LemonModels.addEntryToLexicon(lexicon, URI.create("http://www.example.com/lexicon/this2"), "this2",null);
        final LemonFactory factory = model.getFactory();
        final Component testComp = factory.makeComponent(URI.create("http://www.example.com/lexicon/test#component"));
        testComp.setElement(test);
        final Component thizComp = factory.makeComponent(URI.create("http://www.example.com/lexicon/this#component"));
        thizComp.setElement(thiz2);
        testThis.addDecomposition(Arrays.asList(testComp,thizComp));
        assertTrue(testThis.getDecompositions().iterator().next().get(1).getElement().getURI().toString().endsWith("this2"));
        model.merge(thiz2, thiz);
        assertTrue(testThis.getDecompositions().iterator().next().get(1).getElement().getURI().toString().endsWith("this"));
        
    }
}
