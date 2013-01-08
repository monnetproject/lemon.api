/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.monnetproject.lemon.impl;

import eu.monnetproject.lemon.LemonFactory;
import eu.monnetproject.lemon.model.Text;
import eu.monnetproject.lemon.LemonModel;
import eu.monnetproject.lemon.LemonModels;
import eu.monnetproject.lemon.LemonSerializer;
import eu.monnetproject.lemon.LinguisticOntology;
import eu.monnetproject.lemon.URIElement;
import eu.monnetproject.lemon.model.Argument;
import eu.monnetproject.lemon.model.Condition;
import eu.monnetproject.lemon.model.Constituent;
import eu.monnetproject.lemon.model.Frame;
import eu.monnetproject.lemon.model.LexicalEntry;
import eu.monnetproject.lemon.model.LexicalForm;
import eu.monnetproject.lemon.model.LexicalSense;
import eu.monnetproject.lemon.model.Lexicon;
import eu.monnetproject.lemon.model.Node;
import eu.monnetproject.lemon.model.Property;
import eu.monnetproject.lemon.model.PropertyValue;
import eu.monnetproject.lemon.model.SynArg;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URI;
import java.util.Collection;
import java.util.Map;
import net.lexinfo.LexInfo;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author jmccrae
 */
public class LemonSerializerImplTest {

    public LemonSerializerImplTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @After
    public void tearDown() {
    }
    private final String testTurtleDoc = "@prefix lemon: <http://www.monnet-project.eu/lemon#> . "
            + "@prefix : <file:test#> . "
            + ":lexicon a lemon:Lexicon ; "
            + " lemon:entry :Cat . "
            + ":Cat a lemon:Word ; "
            + " lemon:canonicalForm [ lemon:writtenRep \"cat\"@en ] ; "
            + " lemon:sense [ lemon:reference <http://dbpedia.org/resource/Cat> ] . ";
    private final String testXMLDoc = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:lemon=\"http://www.monnet-project.eu/lemon#\">"
            + " <lemon:Lexicon rdf:about=\"file:test#lexicon\">"
            + "  <lemon:entry> "
            + "   <lemon:Word rdf:about=\"file:test#Cat\"> "
            + "    <lemon:canonicalForm rdf:parseType=\"Resource\"> "
            + "     <lemon:writtenRep xml:lang=\"en\">cat</lemon:writtenRep> "
            + "    </lemon:canonicalForm> "
            + "    <lemon:sense rdf:parseType=\"Resource\"> "
            + "     <lemon:reference rdf:resource=\"http://dbpedia.org/resource/Cat\"/> "
            + "    </lemon:sense> "
            + "   </lemon:Word> "
            + "  </lemon:entry> "
            + " </lemon:Lexicon> "
            + "</rdf:RDF>";

    /**
     * Test of read method, of class LemonSerializerImpl.
     */
    @Test
    public void testRead_Reader() {
        //System.out.println("read");
        Reader source = new StringReader(testXMLDoc);
        LemonSerializerImpl instance = new LemonSerializerImpl(null);
        instance.read(source);
        //System.out.println("XML read OK");
        source = new StringReader(testTurtleDoc);
        instance.read(source);
        //System.out.println("Turtle read OK");
    }

    /**
     * Test of write method, of class LemonSerializerImpl.
     */
    @Test
    public void testWrite_LemonModel_Writer() {
        String expResult = "<?xml version=\"1.0\" encoding=\"US-ASCII\"?>" + ls
                + "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">" + ls
                + "  <lemon:Lexicon rdf:about=\"file:test#lexicon\" xmlns:lemon=\"http://www.monnet-project.eu/lemon#\">" + ls
                + "    <lemon:language>en</lemon:language>" + ls
                + "    <lemon:entry>" + ls
                + "      <lemon:LexicalEntry rdf:about=\"file:test#Cat\">" + ls
                + "        <lemon:sense>" + ls
                + "          <lemon:LexicalSense rdf:about=\"file:test#Cat/sense\">" + ls
                + "            <lemon:reference rdf:resource=\"http://dbpedia.org/resource/Cat\"/>" + ls
                + "          </lemon:LexicalSense>" + ls
                + "        </lemon:sense>" + ls
                + "        <lemon:canonicalForm>" + ls
                + "          <lemon:Form rdf:about=\"file:test#Cat/canonicalForm\">" + ls
                + "            <lemon:writtenRep xml:lang=\"en\">cat</lemon:writtenRep>" + ls
                + "          </lemon:Form>" + ls
                + "        </lemon:canonicalForm>" + ls
                + "      </lemon:LexicalEntry>" + ls
                + "    </lemon:entry>" + ls
                + "  </lemon:Lexicon>" + ls
                + "</rdf:RDF>";
        //System.out.println("write");
        LemonSerializerImpl instance = new LemonSerializerImpl(null);
        LemonModel model = makeModel(instance);
        Writer target = new StringWriter();
        instance.write(model, target);
        //System.out.println(target.toString());
        assertEquals(expResult.replaceAll("\\s","").toLowerCase(), target.toString().replaceAll("\\s","").toLowerCase());
    }
    private LemonModel lazyModel;

    private LemonModel makeModel(LemonSerializerImpl instance) {
        if (lazyModel != null) {
            return lazyModel;
        }
        LemonModel model = instance.create(URI.create("file:test"));
        final Lexicon lexicon = model.addLexicon(URI.create("file:test#lexicon"), "en");
        LemonModels.addEntryToLexicon(lexicon, URI.create("file:test#Cat"), "cat", URI.create("http://dbpedia.org/resource/Cat"));
        return lazyModel = model;
    }

    private LemonModel makeModelUTF8(LemonSerializerImpl instance) {
        if (lazyModel != null) {
            return lazyModel;
        }
        LemonModel model = instance.create(URI.create("file:test"));
        final Lexicon lexicon = model.addLexicon(URI.create("file:test#lexicon"), "ga");
        LemonModels.addEntryToLexicon(lexicon, URI.create("file:test#Sa\u00edocht"), "Sa\u00edocht", URI.create("http://ga.dbpedia.org/resource/Sa\u00edocht"));
        return lazyModel = model;
    }


    /**
     * Test of create method, of class LemonSerializerImpl.
     */
    @Test
    public void testCreate() {
        //System.out.println("create");
        URI context = URI.create("file:test");
        LemonSerializerImpl instance = new LemonSerializerImpl(null);
        instance.create(context);
    }

    /**
     * Test of writeEntry method, of class LemonSerializerImpl.
     */
    @Test
    public void testWriteEntry_4args() {
        //System.out.println("writeEntry");
        LemonSerializerImpl instance = new LemonSerializerImpl(null);
        LemonModel model = makeModel(instance);
        LexicalEntry entry = model.getLexica().iterator().next().getEntrys().iterator().next();
        LinguisticOntology lingOnto = new LexInfo();
        Writer target = new StringWriter();
        instance.writeEntry(model, entry, lingOnto, target);
        //System.out.println(target.toString());
    }

    /**
     * Test of writeLexicon method, of class LemonSerializerImpl.
     */
    @Test
    public void testWriteLexicon_4args() {
        //String expResult = 
        //System.out.println("writeLexicon");
        LemonSerializerImpl instance = new LemonSerializerImpl(null);
        LemonModel model = makeModel(instance);
        Lexicon lexicon = model.getLexica().iterator().next();
        LinguisticOntology lingOnto = new LexInfo();
        Writer target = new StringWriter();
        instance.writeLexicon(model, lexicon, lingOnto, target);
        //System.out.println(target.toString());
    }

    /**
     * Test of moveLexicon method, of class LemonSerializerImpl.
     */
    @Test
    public void testMoveLexicon() {
        //System.out.println("moveLexicon");
        LemonSerializerImpl instance = new LemonSerializerImpl(null);
        LemonModel from = makeModel(instance);
        Lexicon lexicon = from.getLexica().iterator().next();
        LemonModel to = instance.create(URI.create("file:test2"));
        instance.moveLexicon(lexicon, from, to);
    }

    /**
     * Test of read method, of class LemonSerializerImpl.
     */
    @Test
    public void testRead_LemonModel_Reader() {
        //System.out.println("read");
        LemonSerializerImpl instance = new LemonSerializerImpl(null);
        LemonModel lm = instance.create(URI.create("file:test"));
        Reader ds = new StringReader(testXMLDoc);
        instance.read(lm, ds);
    }

    /**
     * Test of write method, of class LemonSerializerImpl.
     */
    @Test
    public void testWrite_3args() {
        String expResult = "@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> . " + ls
                + "@prefix lemon: <http://www.monnet-project.eu/lemon#> . " + ls
                + "" + ls
                + "<file:test#Cat> lemon:sense [  a lemon:LexicalSense ;" + ls
                + " lemon:reference <http://dbpedia.org/resource/Cat> ] ;" + ls
                + " lemon:canonicalForm [  lemon:writtenRep \"cat\"@en ;" + ls
                + " a lemon:Form ] ;" + ls
                + " a lemon:LexicalEntry ." + ls
                + "" + ls
                + "<file:test#lexicon> lemon:entry <file:test#Cat> ;" + ls
                + " a lemon:Lexicon .";
        //System.out.println("write");
        LemonSerializerImpl instance = new LemonSerializerImpl(null);
        LemonModel lm = makeModel(instance);
        Writer dt = new StringWriter();
        boolean xml = false;
        instance.write(lm, dt, xml);
        //System.out.println(dt.toString());
    }

    /**
     * Test of writeEntry method, of class LemonSerializerImpl.
     */
    @Test
    public void testWriteEntry_5args() {
        String expResult = "@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> . " + ls
                + "@prefix lemon: <http://www.monnet-project.eu/lemon#> . " + ls
                + "" + ls
                + "<file:test#Cat/sense> a lemon:LexicalSense ;" + ls
                + " lemon:reference <http://dbpedia.org/resource/Cat> ." + ls
                + "" + ls
                + "<file:test#Cat/canonicalForm> lemon:writtenRep \"cat\"@en ;" + ls
                + " a lemon:Form ." + ls
                + "" + ls
                + "<file:test#Cat> lemon:sense <file:test#Cat/sense> ;" + ls
                + " lemon:canonicalForm <file:test#Cat/canonicalForm> ;" + ls
                + " a lemon:LexicalEntry .";
        //System.out.println("writeEntry");
        LemonSerializerImpl instance = new LemonSerializerImpl(null);
        LemonModel lm = makeModel(instance);
        LexicalEntry le = lm.getLexica().iterator().next().getEntrys().iterator().next();
        LinguisticOntology lo = new LexInfo();
        Writer dt = new StringWriter();
        boolean xml = false;
        instance.writeEntry(lm, le, lo, dt, xml);
        assertEquals(expResult, dt.toString().trim());
    }

    /**
     * Test of writeLexicon method, of class LemonSerializerImpl.
     */
    @Test
    public void testWriteLexicon_5args() {
        String expResult = "@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> . " + ls
                + "@prefix lemon: <http://www.monnet-project.eu/lemon#> . " + ls
                + "" + ls
                + "<file:test#Cat/sense> a lemon:LexicalSense ;" + ls
                + " lemon:reference <http://dbpedia.org/resource/Cat> ." + ls
                + "" + ls
                + "<file:test#Cat/canonicalForm> lemon:writtenRep \"cat\"@en ;" + ls
                + " a lemon:Form ." + ls
                + "" + ls
                + "<file:test#Cat> lemon:sense <file:test#Cat/sense> ;" + ls
                + " lemon:canonicalForm <file:test#Cat/canonicalForm> ;" + ls
                + " a lemon:LexicalEntry ." + ls
                + "" + ls
                + "<file:test#lexicon> lemon:entry <file:test#Cat> ;" + ls
                + " a lemon:Lexicon .";
        //System.out.println("writeLexicon");
        LemonSerializerImpl instance = new LemonSerializerImpl(null);
        LemonModel lm = makeModel(instance);
        Lexicon lxcn = lm.getLexica().iterator().next();
        LinguisticOntology lo = new LexInfo();
        Writer dt = new StringWriter();
        boolean xml = false;
        instance.writeLexicon(lm, lxcn, lo, dt, xml);
        assertEquals(expResult, dt.toString().trim());
    }
    private final String input = "@prefix MusicBrainzLexicon: <http://monnetproject.deri.ie/lemonsource/user/httpswwwgooglecomaccountso8ididAItOawnRWNkyXGW_lk5kD1JgLCzU9MCwC_R8TY/MusicBrainzLexicon#>." + ls
            + "@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>." + ls
            + "@prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>." + ls
            + "@prefix lemon: <http://www.monnet-project.eu/lemon#>." + ls
            + "@prefix lexinfo: <http://www.lexinfo.net/ontology/2.0/lexinfo#>."
            + "@prefix : <http://monnetproject.deri.ie/lemonsource/user/httpswwwgooglecomaccountso8ididAItOawnRWNkyXGW_lk5kD1JgLCzU9MCwC_R8TY/MusicBrainzLexicon#>.\n" + ls
            + "MusicBrainzLexicon:lexicon a lemon:Lexicon ; lemon:entry MusicBrainzLexicon:collaborationOf." + ls
            + "MusicBrainzLexicon:collaborationOf lemon:sense [ lemon:reference <http://purl.org/vocab/relationship/collaboratesWith> ;" + ls
            + "                                    lemon:subjOfProp :arg1collaboration ;" + ls
            + "                                   lemon:objOfProp  :arg2collaboration ;" + ls
            + "                                   lemon:propertyDomain :example ] ;" + ls
            + " lexinfo:partOfSpeech lexinfo:noun ;" + ls
            + "lemon:synBehavior [ rdf:type lexinfo:NounPPFrame ;" + ls
            + " 	              lexinfo:subject :arg2collaboration ;" + ls
            + "                    lexinfo:prepositionalObject :arg1collaboration ] ;" + ls
            + "lexinfo:partOfSpeech lexinfo:noun ;" + ls
            + "lemon:canonicalForm [ lemon:writtenRep \"collaboration\"@en ;" + ls
            + "                      lexinfo:number lexinfo:singular ] ;" + ls
            + "lemon:otherForm [ lemon:writtenRep \"collaborations\"@en ;" + ls
            + "                  lexinfo:number lexinfo:plural ] ." + ls
            + ""
            + ":arg2collaboration lemon:marker :Of." + ls;

    @Test
    public void testSynArgRead() {
        final LemonSerializer lemonSerializer = LemonSerializer.newInstance();
        final LemonModel model = lemonSerializer.read(new StringReader(input));
        final Collection<Lexicon> lexica = model.getLexica();
        assertFalse(lexica.isEmpty());
        final Lexicon lexicon = lexica.iterator().next();
        final Collection<LexicalEntry> entrys = lexicon.getEntrys();
        assertFalse(entrys.isEmpty());
        final LexicalEntry entry = entrys.iterator().next();
        final Collection<Frame> synBehaviors = entry.getSynBehaviors();
        assertFalse(synBehaviors.isEmpty());
        final Frame frame = synBehaviors.iterator().next();
        final Map<SynArg, Collection<Argument>> synArgs = frame.getSynArgs();
        assertFalse(synArgs.isEmpty());
        final Map<Property, Collection<PropertyValue>> props = entry.getPropertys();
        assertFalse(props.isEmpty());
        final LexInfo lexInfo = new LexInfo();
        final Property pos = lexInfo.getProperty("partOfSpeech");
        final Collection<PropertyValue> pvs = entry.getProperty(pos);
        assertFalse(pvs.isEmpty());
        assertEquals(lexInfo.getPropertyValue("noun"), pvs.iterator().next());
        final Collection<LexicalSense> senses = entry.getSenses();
        assertFalse(senses.isEmpty());
        final LexicalSense sense = senses.iterator().next();
        assertEquals(URI.create("http://purl.org/vocab/relationship/collaboratesWith"), sense.getReference());
        assertEquals(null, sense.getRefPref());
        assertEquals(1, sense.getSubjOfProps().size());
        assertFalse(sense.getObjOfProps().isEmpty());
        assertTrue(sense.getIsAs().isEmpty());
        assertFalse(sense.getConditions().isEmpty());
        assertFalse(sense.getCondition(Condition.propertyDomain).isEmpty());
        final Argument subject = frame.getSynArg(lexInfo.getSynArg("subject")).iterator().next();
        assertEquals(subject, sense.getObjOfProps().iterator().next());
        assertNotNull(subject.getMarker());
        assertEquals(2, frame.getTypes().size());
        for (URI frameType : frame.getTypes()) {
            assertTrue(URI.create("http://www.lexinfo.net/ontology/2.0/lexinfo#NounPPFrame").equals(frameType)
                    || URI.create("http://www.monnet-project.eu/lemon#Frame").equals(frameType));
        }
        assertFalse(entry.getOtherForms().isEmpty());
        final LexicalForm otherForm = entry.getOtherForms().iterator().next();
        assertEquals(new Text("collaborations", "en"), otherForm.getWrittenRep());

    }

    @Test
    public void testLoadUnicode() {
        //System.out.println("testloadUnicode");
        final String xmlDoc = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:lemon=\"http://www.monnet-project.eu/lemon#\">"
                + " <lemon:Lexicon rdf:about=\"file:test#lexicon\">"
                + "  <lemon:entry> "
                + "   <lemon:Word rdf:about=\"file:test#Baer\"> "
                + "    <lemon:canonicalForm rdf:parseType=\"Resource\"> "
                + "     <lemon:writtenRep xml:lang=\"en\">b&#x00e4;r</lemon:writtenRep> "
                + "    </lemon:canonicalForm> "
                + "    <lemon:sense rdf:parseType=\"Resource\"> "
                + "     <lemon:reference rdf:resource=\"http://dbpedia.org/resource/Bear\"/> "
                + "    </lemon:sense> "
                + "   </lemon:Word> "
                + "  </lemon:entry> "
                + " </lemon:Lexicon> "
                + "</rdf:RDF>";
        Reader source = new StringReader(xmlDoc);
        LemonSerializerImpl instance = new LemonSerializerImpl(null);
        final LemonModel model = instance.read(source);
        final Lexicon lexicon = model.getLexica().iterator().next();
        final LexicalEntry entry = lexicon.getEntrys().iterator().next();
        final String baer = entry.getCanonicalForm().getWrittenRep().value;
        assertEquals("b\u00e4r", baer);
    }

    @Test
    public void testWriteUnicode() {
        //System.out.println("testWriteUnicode");
        final LemonModel model = LemonSerializer.newInstance().create();
        final Lexicon lexicon = model.addLexicon(URI.create("file:test#lexicon__de"), "de");
        LemonModels.addEntryToLexicon(lexicon, URI.create("file:test#lexicon__de/baer"), "b\u00e4", null);
    }

    @Test
    public void testReadEntry() {
        //System.out.println("testReadEntry");
        final String xmlDoc = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:lemon=\"http://www.monnet-project.eu/lemon#\">"
                + " <lemon:LexicalEntry rdf:about=\"http://www.example.com/test\">"
                + "   <lemon:canonicalForm>"
                + "     <lemon:LexicalForm rdf:about=\"http://www.example.com/test#form\">"
                + "       <lemon:writtenRep xml:lang=\"en\">test</lemon:writtenRep>"
                + "     </lemon:LexicalForm>"
                + "   </lemon:canonicalForm>"
                + " </lemon:LexicalEntry>"
                + "</rdf:RDF>";

        Reader source = new StringReader(xmlDoc);
        LemonSerializerImpl instance = new LemonSerializerImpl(null);
        final LexicalEntry entry = instance.readEntry(source);
        final String test = entry.getCanonicalForm().getWrittenRep().value;
        assertEquals("test", test);
    }

    @Test
    public void testWriteLexiconDescription() {
        //System.out.println("testWriteLexiconDescription");
        final StringWriter out = new StringWriter();
        LemonSerializerImpl instance = new LemonSerializerImpl(null);
        final LemonModel model = instance.create();
        final Lexicon lexicon = model.addLexicon(URI.create("http://www.example.com/lexicon/"), "en");
        LemonModels.addEntryToLexicon(lexicon, URI.create("http://www.example.com/lexicon/example"), "example", null);
        instance.writeLexiconDescription(model, lexicon, out);
        final String xmlDoc = "<?xml version=\"1.0\" encoding=\"US-ASCII\"?>" + ls
                + "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">" + ls
                + "  <lemon:Lexicon rdf:about=\"http://www.example.com/lexicon/\" xmlns:lemon=\"http://www.monnet-project.eu/lemon#\">" + ls
                + "    <lemon:language>en</lemon:language>" + ls
                + "    <lemon:entry rdf:resource=\"http://www.example.com/lexicon/example\"/>" + ls
                + "  </lemon:Lexicon>" + ls
                + "</rdf:RDF>";
        assertEquals(xmlDoc, out.toString());
    }
    static final String ls = System.getProperty("line.separator");

    @Test
    public void testConstituent() {
        //System.out.println("testConstituent");
        String expResult = "@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> . " + ls
                + "@prefix lemon: <http://www.monnet-project.eu/lemon#> . " + ls
                + "" + ls
                + "<file:test#Cat/node> lemon:constituent  <file:test#NP>  ;" + ls
                + " a lemon:Node ." + ls
                + "" + ls
                + "<file:test#Cat/sense> a lemon:LexicalSense ;" + ls
                + " lemon:reference <http://dbpedia.org/resource/Cat> ." + ls
                + "" + ls
                + "<file:test#Cat/canonicalForm> lemon:writtenRep \"cat\"@en ;" + ls
                + " a lemon:Form ." + ls
                + "" + ls
                + "<file:test#Cat> lemon:phraseRoot <file:test#Cat/node> ;" + ls
                + " lemon:sense <file:test#Cat/sense> ;" + ls
                + " lemon:canonicalForm <file:test#Cat/canonicalForm> ;" + ls
                + " a lemon:LexicalEntry ." + ls
                + "" + ls
                + "<file:test#lexicon> lemon:entry <file:test#Cat> ;" + ls
                + " a lemon:Lexicon ." + ls
                + "" + ls;
//                + "<file:test#Cat/sense> a lemon:LexicalSense ;" + ls
//                + " lemon:reference <http://dbpedia.org/resource/Cat> ." + ls
//                + "" + ls
//                + "<file:test#Cat> lemon:phraseRoot <file:test#Cat/node> ;" + ls
//                + " lemon:sense <file:test#Cat/sense> ;" + ls
//                + " lemon:canonicalForm <file:test#Cat/canonicalForm> ;" + ls
//                + " a lemon:LexicalEntry ." + ls
//                + "" + ls
//                + "<file:test#Cat/sense> a lemon:LexicalSense ;" + ls
//                + " lemon:reference <http://dbpedia.org/resource/Cat> ."+ls+ls;
        //System.out.println("write");
        LemonSerializerImpl instance = new LemonSerializerImpl(null);
        LemonModel lm = makeModel(instance);
        final Lexicon lexicon = lm.addLexicon(URI.create("file:test#lexicon"), "en");
        final LemonFactory factory = lm.getFactory();
        final LexicalEntry lexEntry = factory.makeLexicalEntry(URI.create("file:test#Cat"));//LemonModels.addEntryToLexicon(lexicon, URI.create("file:test#Cat"), "cat", URI.create("http://dbpedia.org/resource/Cat"));
        final Node node = factory.makeNode(URI.create("file:test#Cat/node"));
        class ConstituentImpl extends URIElement implements Constituent {

            public ConstituentImpl(URI uri) {
                super(uri);
            }
        }
        node.setConstituent(new ConstituentImpl(URI.create("file:test#NP")));
        lexEntry.addPhraseRoot(node);
        Writer dt = new StringWriter();
        boolean xml = false;
        instance.write(lm, dt, xml);
        //System.out.println(dt.toString());
        assertEquals(expResult, dt.toString());
    }

    /**
     * Test of writeEntry method, of class LemonSerializerImpl.
     */
    @Test
    public void testWriteEntry_utf8() {
        String expResult = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + ls 
                + "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">" + ls
                + "  <lemon:LexicalEntry rdf:about=\"file:test#Sa\u00edocht\" xmlns:lemon=\"http://www.monnet-project.eu/lemon#\">" + ls
                + "    <lemon:sense>" + ls
                + "      <lemon:LexicalSense rdf:about=\"file:test#Sa\u00edocht/sense\">" + ls
                + "        <lemon:reference rdf:resource=\"http://ga.dbpedia.org/resource/Sa\u00edocht\"/>" +ls
                + "      </lemon:LexicalSense>" + ls
                + "    </lemon:sense>" + ls
                + "    <lemon:canonicalForm>" + ls
                + "      <lemon:Form rdf:about=\"file:test#Sa\u00edocht/canonicalForm\">" + ls
                + "        <lemon:writtenRep xml:lang=\"ga\">Sa\u00edocht</lemon:writtenRep>" + ls
                + "      </lemon:Form>" + ls
                + "    </lemon:canonicalForm>" + ls
                + "  </lemon:LexicalEntry>" + ls
                + "</rdf:RDF>";
        System.setProperty("lemon.api.xml.encoding","UTF-8");
        LemonSerializerImpl instance = new LemonSerializerImpl(null);
        LemonModel lm = makeModelUTF8(instance);
        LexicalEntry le = lm.getLexica().iterator().next().getEntrys().iterator().next();
        LinguisticOntology lo = new LexInfo();
        Writer dt = new StringWriter();
        boolean xml = true;
        instance.writeEntry(lm, le, lo, dt, xml);
        assertEquals(expResult.replaceAll("\\s",""), dt.toString().replaceAll("\\s",""));
        System.setProperty("lemon.api.xml.encoding","US-ASCII");
        //assertEquals("","");
    }
}
