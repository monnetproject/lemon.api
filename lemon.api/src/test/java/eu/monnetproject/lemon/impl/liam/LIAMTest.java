/**********************************************************************************
 * Copyright (c) 2011, Monnet Project
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the Monnet Project nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE MONNET PROJECT BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *********************************************************************************/
package eu.monnetproject.lemon.impl.liam;

import eu.monnetproject.lemon.liam.impl.MorphologyEngineImpl;
import eu.monnetproject.lemon.model.Text;
import eu.monnetproject.lemon.LemonFactory;
import eu.monnetproject.lemon.LemonModel;
import eu.monnetproject.lemon.LemonModels;
import eu.monnetproject.lemon.LemonSerializer;
import eu.monnetproject.lemon.LinguisticOntology;
import eu.monnetproject.lemon.impl.LemonSerializerImpl;
import eu.monnetproject.lemon.model.LexicalEntry;
import eu.monnetproject.lemon.model.LexicalForm;
import eu.monnetproject.lemon.model.Lexicon;
import eu.monnetproject.lemon.model.MorphPattern;
import eu.monnetproject.lemon.model.MorphTransform;
import eu.monnetproject.lemon.model.Prototype;
import java.net.URI;
import net.lexinfo.LexInfo;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author John McCrae
 */
public class LIAMTest {

    @Test
    public void example64() {
        final LinguisticOntology lingOnto = new LexInfo();
        final LemonSerializer serializer = new LemonSerializerImpl(lingOnto);
        final MorphologyEngineImpl morphologyEngineImpl = new MorphologyEngineImpl();
        final LemonModel model = serializer.create(null);
        final Lexicon lexicon = model.addLexicon(URI.create("file:test#lexicon"), "en");
        final LemonFactory factory = model.getFactory();
        final LexicalEntry catEntry = LemonModels.addEntryToLexicon(lexicon, URI.create("file:test#cat"), "cat", null);
        final MorphPattern englishNoun = factory.makeMorphPattern();
        final MorphTransform enNounPluralPatt = factory.makeMorphTransform();
        final Prototype enNounPluralProto = factory.makePrototype();
        enNounPluralPatt.addRule("~s");
        enNounPluralProto.addProperty(lingOnto.getProperty("number"), lingOnto.getPropertyValue("plural"));
        enNounPluralPatt.addGenerates(enNounPluralProto);
        englishNoun.addTransform(enNounPluralPatt);
        final LexicalForm result1 = morphologyEngineImpl.generate(catEntry, englishNoun, lingOnto.getPropertyMap("number", "plural"));
        assertEquals("cats", result1.getWrittenRep().value);
    }

    @Test
    public void example66() {
        final LinguisticOntology lingOnto = new LexInfo();
        final LemonSerializer serializer = new LemonSerializerImpl(lingOnto);
        final MorphologyEngineImpl morphologyEngineImpl = new MorphologyEngineImpl();
        final LemonModel model = serializer.create(null);
        final Lexicon lexicon = model.addLexicon(URI.create("file:test#lexicon"), "en");
        final LemonFactory factory = model.getFactory();
        final MorphPattern englishNoun = factory.makeMorphPattern();
        final MorphTransform enNounPluralPatt = factory.makeMorphTransform();
        final Prototype enNounPluralProto = factory.makePrototype();
        enNounPluralPatt.addRule("~s");
        enNounPluralProto.addProperty(lingOnto.getProperty("number"), lingOnto.getPropertyValue("plural"));
        enNounPluralPatt.addGenerates(enNounPluralProto);
        englishNoun.addTransform(enNounPluralPatt);

        enNounPluralPatt.addRule("~y/~ies");
        final LexicalEntry poppyEntry = LemonModels.addEntryToLexicon(lexicon, URI.create("file:test#poppy"), "poppy", null);
        final LexicalForm result2 = morphologyEngineImpl.generate(poppyEntry, englishNoun, lingOnto.getPropertyMap("number", "plural"));
        assertEquals("poppies", result2.getWrittenRep().value);
    }

    @Test
    public void example67() {
        final LinguisticOntology lingOnto = new LexInfo();
        final LemonSerializer serializer = new LemonSerializerImpl(lingOnto);
        final MorphologyEngineImpl morphologyEngineImpl = new MorphologyEngineImpl();
        final LemonModel model = serializer.create(null);
        final Lexicon lexicon = model.addLexicon(URI.create("file:test#lexicon"), "en");
        final LemonFactory factory = model.getFactory();
        final MorphPattern englishNoun = factory.makeMorphPattern();
        final MorphTransform enNounPluralPatt = factory.makeMorphTransform();
        final Prototype enNounPluralProto = factory.makePrototype();
        enNounPluralPatt.addRule("~s");
        enNounPluralProto.addProperty(lingOnto.getProperty("number"), lingOnto.getPropertyValue("plural"));
        enNounPluralPatt.addGenerates(enNounPluralProto);
        englishNoun.addTransform(enNounPluralPatt);
        enNounPluralPatt.addRule("~(<?![aeiou])y/~ies");
        final LexicalEntry playEntry = LemonModels.addEntryToLexicon(lexicon, URI.create("file:test#play"), "play", null);
        final LexicalForm result3 = morphologyEngineImpl.generate(playEntry, englishNoun, lingOnto.getPropertyMap("number", "plural"));
        assertEquals("plays", result3.getWrittenRep().value);
    }

    @Test
    public void example68() {
        final LinguisticOntology lingOnto = new LexInfo();
        final LemonSerializer serializer = new LemonSerializerImpl(lingOnto);
        final MorphologyEngineImpl morphologyEngineImpl = new MorphologyEngineImpl();
        final LemonModel model = serializer.create(null);
        final Lexicon deLexicon = model.addLexicon(URI.create("file:test#lexicon_de"), "de");
        final LemonFactory factory = model.getFactory();
        final MorphPattern germanWeakVerb = factory.makeMorphPattern();
        final MorphTransform dePerfect = factory.makeMorphTransform();
        final Prototype dePerfectProto = factory.makePrototype();
        dePerfectProto.addProperty(lingOnto.getProperty("tense"), lingOnto.getPropertyValue("past"));
        dePerfectProto.addProperty(lingOnto.getProperty("aspect"), lingOnto.getPropertyValue("perfective"));
        dePerfectProto.addProperty(lingOnto.getProperty("verbFormMood"), lingOnto.getPropertyValue("participle"));
        dePerfect.addGenerates(dePerfectProto);
        dePerfect.addRule("~e?n/ge~t");
        germanWeakVerb.addTransform(dePerfect);
        final LexicalEntry spielenEntry = LemonModels.addEntryToLexicon(deLexicon, URI.create("file:test#spielen"), "spielen", null);
        final LexicalForm result4 = morphologyEngineImpl.generate(spielenEntry, germanWeakVerb, lingOnto.getPropertyMap("tense", "past", "aspect", "perfective", "verbFormMood", "participle"));
        assertEquals("gespielt", result4.getWrittenRep().value);
    }

    @Test
    public void example69() {
        final LinguisticOntology lingOnto = new LexInfo();
        final LemonSerializer serializer = new LemonSerializerImpl(lingOnto);
        final MorphologyEngineImpl morphologyEngineImpl = new MorphologyEngineImpl();
        final LemonModel model = serializer.create(null);
        final LemonFactory factory = model.getFactory();
        final Lexicon lexicon = model.addLexicon(URI.create("file:test#lexicon_it"), "it");
        final MorphPattern regularVerb = factory.makeMorphPattern();
        final MorphTransform s1p = factory.makeMorphTransform();
        final Prototype s1pProto = factory.makePrototype();
        s1pProto.addProperty(lingOnto.getProperty("tense"), lingOnto.getPropertyValue("present"));
        s1pProto.addProperty(lingOnto.getProperty("person"), lingOnto.getPropertyValue("firstPerson"));
        s1pProto.addProperty(lingOnto.getProperty("number"), lingOnto.getPropertyValue("singular"));
        s1p.addGenerates(s1pProto);
        s1p.addRule("~are/~o");
        regularVerb.addTransform(s1p);
        final MorphTransform s3p = factory.makeMorphTransform();
        final Prototype s3pProto = factory.makePrototype();
        s3pProto.addProperty(lingOnto.getProperty("tense"), lingOnto.getPropertyValue("present"));
        s3pProto.addProperty(lingOnto.getProperty("person"), lingOnto.getPropertyValue("thirdPerson"));
        s3pProto.addProperty(lingOnto.getProperty("number"), lingOnto.getPropertyValue("singular"));
        s3p.addGenerates(s3pProto);
        s3p.addRule("~are/~a");
        regularVerb.addTransform(s3p);
        final LexicalEntry amareEntry = LemonModels.addEntryToLexicon(lexicon, URI.create("file:test#amare"), "amare", null);
        final LexicalForm result1 = morphologyEngineImpl.generate(amareEntry, regularVerb, lingOnto.getPropertyMap("tense", "present", "person", "firstPerson", "number", "singular"));
        assertEquals("amo", result1.getWrittenRep().value);
        final LexicalForm result2 = morphologyEngineImpl.generate(amareEntry, regularVerb, lingOnto.getPropertyMap("tense", "present", "person", "thirdPerson", "number", "singular"));
        assertEquals("ama", result2.getWrittenRep().value);
    }

    @Test
    public void example70() {
        final LinguisticOntology lingOnto = new LexInfo();
        final LemonSerializer serializer = new LemonSerializerImpl(lingOnto);
        final MorphologyEngineImpl morphologyEngineImpl = new MorphologyEngineImpl();
        final LemonModel model = serializer.create(null);
        final Lexicon deLexicon = model.addLexicon(URI.create("file:test#lexicon_de"), "de");
        final LemonFactory factory = model.getFactory();
        final MorphPattern germanMixedVerb = factory.makeMorphPattern();
        final MorphTransform past2p = factory.makeMorphTransform();
        final Prototype past2pProto = factory.makePrototype();
        final Prototype past2pStem = factory.makePrototype();
        past2pProto.addProperty(lingOnto.getProperty("tense"), lingOnto.getPropertyValue("past"));
        past2pProto.addProperty(lingOnto.getProperty("verbFormMood"), lingOnto.getPropertyValue("indicative"));
        past2pProto.addProperty(lingOnto.getProperty("person"), lingOnto.getPropertyValue("secondPerson"));
        past2pProto.addProperty(lingOnto.getProperty("number"), lingOnto.getPropertyValue("singular"));
        past2p.addGenerates(past2pProto);
        past2pStem.addProperty(lingOnto.getProperty("tense"), lingOnto.getPropertyValue("past"));
        past2pStem.addProperty(lingOnto.getProperty("verbFormMood"), lingOnto.getPropertyValue("indicative"));
        past2pStem.addProperty(lingOnto.getProperty("person"), lingOnto.getPropertyValue("firstPerson"));
        past2pStem.addProperty(lingOnto.getProperty("number"), lingOnto.getPropertyValue("singular"));
        past2p.setOnStem(past2pStem);
        past2p.addRule("~st");
        germanMixedVerb.addTransform(past2p);
        final LexicalEntry denken = LemonModels.addEntryToLexicon(deLexicon, URI.create("file:test#denken"), "denken", null);
        final LexicalForm dachte = factory.makeForm();
        dachte.setWrittenRep(new Text("dachte", "de"));
        dachte.addProperty(lingOnto.getProperty("tense"), lingOnto.getPropertyValue("past"));
        dachte.addProperty(lingOnto.getProperty("verbFormMood"), lingOnto.getPropertyValue("indicative"));
        dachte.addProperty(lingOnto.getProperty("person"), lingOnto.getPropertyValue("firstPerson"));
        dachte.addProperty(lingOnto.getProperty("number"), lingOnto.getPropertyValue("singular"));
        denken.addOtherForm(dachte);
        final LexicalForm result = morphologyEngineImpl.generate(denken, germanMixedVerb, lingOnto.getPropertyMap("tense", "past", "verbFormMood", "indicative", "person", "secondPerson", "number", "singular"));
        assertEquals("dachtest", result.getWrittenRep().value);

    }

    @Test
    public void example71() {
        final LinguisticOntology lingOnto = new LexInfo();
        final LemonSerializer serializer = new LemonSerializerImpl(lingOnto);
        final MorphologyEngineImpl morphologyEngineImpl = new MorphologyEngineImpl();
        final LemonModel model = serializer.create(null);
        final Lexicon jaLexicon = model.addLexicon(URI.create("file:test#lexicon_ja"), "ja");
        final LemonFactory factory = model.getFactory();
        final MorphPattern vowelStemVerb = factory.makeMorphPattern();

        final MorphTransform causative = factory.makeMorphTransform();
        final Prototype causativeProto = factory.makePrototype();
        causativeProto.addProperty(lingOnto.getProperty("case"), lingOnto.getPropertyValue("causativeCase"));
        causative.addGenerates(causativeProto);
        causative.addRule("~ru/~saseru");

        final MorphTransform passive = factory.makeMorphTransform();
        final Prototype passiveProto = factory.makePrototype();
        passiveProto.addProperty(lingOnto.getProperty("voice"), lingOnto.getPropertyValue("passiveVoice"));
        passive.addGenerates(passiveProto);
        passive.addRule("~ru/~rareru");
        
        final MorphTransform negative = factory.makeMorphTransform();
        final Prototype negativeProto = factory.makePrototype();
        negativeProto.addProperty(lingOnto.getProperty("negative"), lingOnto.getPropertyValue("yes"));
        negative.addGenerates(negativeProto);
        negative.addRule("~ru/~nai");
       
        final MorphTransform pastNeg = factory.makeMorphTransform();
        final Prototype pastNegProto = factory.makePrototype();
        pastNegProto.addProperty(lingOnto.getProperty("tense"), lingOnto.getPropertyValue("past"));
        pastNegProto.addProperty(lingOnto.getProperty("negative"), lingOnto.getPropertyValue("yes"));
        final Prototype pastNegStem = factory.makePrototype();
        pastNegStem.addProperty(lingOnto.getProperty("negative"), lingOnto.getPropertyValue("yes"));
        pastNegStem.addProperty(lingOnto.getProperty("tense"), lingOnto.getPropertyValue("present"));
        pastNeg.addGenerates(pastNegProto);
        pastNeg.addRule("~i/~katta");

        
        causative.addNextTransform(passive);
        causative.addNextTransform(negative);
        
        passive.addNextTransform(negative);
        
        negative.addNextTransform(pastNeg);
        
        vowelStemVerb.addTransform(causative);
        vowelStemVerb.addTransform(passive);
        vowelStemVerb.addTransform(negative);
        vowelStemVerb.addTransform(pastNeg);
        
        final LexicalEntry taberu = LemonModels.addEntryToLexicon(jaLexicon, URI.create("file:test#taberu"), "taberu", null);
        taberu.getCanonicalForm().addProperty(lingOnto.getProperty("tense"), lingOnto.getPropertyValue("present"));
        final LexicalForm result = morphologyEngineImpl.generate(taberu, vowelStemVerb, lingOnto.getPropertyMap("case","causativeCase","voice","passiveVoice","tense","past","negative","yes"));
        assertEquals("tabesaserarenakatta",result.getWrittenRep().value);

    }
    
    @Test
    public void testBracketing() {
        final LinguisticOntology lingOnto = new LexInfo();
        final LemonSerializer serializer = new LemonSerializerImpl(lingOnto);
        final MorphologyEngineImpl morphologyEngineImpl = new MorphologyEngineImpl();
        final LemonModel model = serializer.create(null);
        final Lexicon lexicon = model.addLexicon(URI.create("file:test#lexicon"), "en");
        final LemonFactory factory = model.getFactory();
        final LexicalEntry catEntry = LemonModels.addEntryToLexicon(lexicon, URI.create("file:test#cat"), "catenbox", null);
        final MorphPattern englishNoun = factory.makeMorphPattern();
        final MorphTransform enNounPluralPatt = factory.makeMorphTransform();
        final Prototype enNounPluralProto = factory.makePrototype();
        enNounPluralPatt.addRule("(((c)a)t~(box))/$3$2~$4s");
        enNounPluralProto.addProperty(lingOnto.getProperty("number"), lingOnto.getPropertyValue("plural"));
        enNounPluralPatt.addGenerates(enNounPluralProto);
        englishNoun.addTransform(enNounPluralPatt);
        final LexicalForm result1 = morphologyEngineImpl.generate(catEntry, englishNoun, lingOnto.getPropertyMap("number", "plural"));
        assertEquals("ccaenboxs", result1.getWrittenRep().value);
        
    }
}
