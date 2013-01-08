/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.monnetproject.lemon.conversions.skosxl;

import eu.monnetproject.lemon.LemonModel;
import eu.monnetproject.lemon.LemonModels;
import eu.monnetproject.lemon.LemonSerializer;
import eu.monnetproject.lemon.model.LexicalEntry;
import eu.monnetproject.lemon.model.Lexicon;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URI;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.w3c.dom.Document;

/**
 *
 * @author jmccrae
 */
public class SKOSXLConverterTest {
    
    public SKOSXLConverterTest() {
    }
    
    @BeforeClass
    public static void setUpClass() throws Exception {
    }
    
    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    
    public void tearDown() {
    }

    /**
     * Test of convert method, of class SKOSXLConverter.
     */
    @Test
    public void testConvert_Reader() {
        System.out.println("convert");
        Reader document = new StringReader(
                "<rdf:RDF xmlns:rdf='http://www.w3.org/1999/02/22-rdf-syntax-ns#' xmlns:skosxl='http://www.w3.org/2008/05/skos-xl#' xmlns='file:test'>"
                + "<rdf:Description rdf:about='Cat'>"
                + "  <skosxl:prefLabel>"
                + "    <rdf:Description>"
                + "      <skosxl:literalForm xml:lang='en'>cat</skosxl:literalForm>"
                + "    </rdf:Description>"
                + "  </skosxl:prefLabel>"
                + "</rdf:Description>"
                + "</rdf:RDF>");
        LemonModel result = SKOSXLConverter.convert(document);
        assertFalse(result.getLexica().isEmpty());
        assertTrue(result.getLexica().iterator().next().getEntrys().iterator().next().getCanonicalForm().getWrittenRep().value.equals("cat"));
    }

    /**
     * Test of convert method, of class SKOSXLConverter.
     */
    @Test
    public void testConvert_LemonModel() throws Exception {
        System.out.println("convert");
        LemonModel model = LemonSerializer.newInstance().create(null);
        final Lexicon lexicon = model.addLexicon(URI.create("file:test#lexicon"), "en");
        LemonModels.addEntryToLexicon(lexicon, URI.create("file:test#lexicon/Cat"), "cat", URI.create("http://test.com/Cat"));
        Document result = SKOSXLConverter.convert(model);
        final TransformerFactory factory = TransformerFactory.newInstance();
        factory.setAttribute("indent-number", 2);
        Transformer trans = factory.newTransformer();
        trans.setOutputProperty(OutputKeys.INDENT, "yes");
        StreamResult streamResult = new StreamResult(new StringWriter());
        DOMSource source = new DOMSource(result);
        trans.transform(source, streamResult);
        System.out.println(streamResult.getWriter().toString());
        final String ls = System.getProperty("line.separator");
        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+ls
                + "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">"+ls
                + " <rdf:Description rdf:about=\"http://test.com/Cat\">"+ls
                + "    <skosxl:prefLabel xmlns:skosxl=\"http://www.w3.org/2008/05/skos-xl#\">"+ls
                + "      <skosxl:Label rdf:about=\"file:test#lexicon/Cat\">"+ls
                + "        <skosxl:literalForm xml:lang=\"en\">cat</skosxl:literalForm>"+ls
                + "      </skosxl:Label>"+ls
                + "    </skosxl:prefLabel>"+ls
                + "  </rdf:Description>"+ls
                + "</rdf:RDF>";
        assertEquals(expected.replaceAll("\\s",""), streamResult.getWriter().toString().replaceAll("\\s",""));
    }
    
    @Test
    public void testConvert_LemonModelWithAnno() throws Exception {
        System.out.println("convert");
        LemonModel model = LemonSerializer.newInstance().create(null);
        final Lexicon lexicon = model.addLexicon(URI.create("file:test#lexicon"), "en");
        final LexicalEntry entry = LemonModels.addEntryToLexicon(lexicon, URI.create("file:test#lexicon/Cat"), "cat", URI.create("http://test.com/Cat"));
        entry.addAnnotation(URI.create("http://www.example.com/test#test"), "testValue");
        Document result = SKOSXLConverter.convert(model);
        final TransformerFactory factory = TransformerFactory.newInstance();
        factory.setAttribute("indent-number", 2);
        Transformer trans = factory.newTransformer();
        trans.setOutputProperty(OutputKeys.INDENT, "yes");
        StreamResult streamResult = new StreamResult(new StringWriter());
        DOMSource source = new DOMSource(result);
        trans.transform(source, streamResult);
        System.out.println(streamResult.getWriter().toString());
        final String ls = System.getProperty("line.separator");
        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+ls
                + "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">"+ls
                + "  <rdf:Description rdf:about=\"http://test.com/Cat\">"+ls
                + "    <skosxl:prefLabel xmlns:skosxl=\"http://www.w3.org/2008/05/skos-xl#\">"+ls
                + "      <skosxl:Label rdf:about=\"file:test#lexicon/Cat\">"+ls
                + "        <skosxl:literalForm xml:lang=\"en\">cat</skosxl:literalForm>"+ls
                + "        <test xmlns=\"http://www.example.com/test#\">testValue</test>"+ls
                + "      </skosxl:Label>"+ls
                + "    </skosxl:prefLabel>"+ls
                + "  </rdf:Description>"+ls
                + "</rdf:RDF>";
        assertEquals(expected.replaceAll("\\s",""), streamResult.getWriter().toString().replaceAll("\\s",""));
    }
}
