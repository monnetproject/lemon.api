/****************************************************************************
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
 ********************************************************************************/
package eu.monnetproject.lemon.impl;

import eu.monnetproject.lemon.LemonModel;
import eu.monnetproject.lemon.impl.LemonSerializerImpl;
import eu.monnetproject.lemon.model.Lexicon;
import java.io.StringReader;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author John McCrae
 */
public class XMLLoadingTest {

    private String testDocument =
            "<rdf:RDF xmlns:lemon=\"http://www.monnet-project.eu/lemon#\""
            + "   xmlns:lexinfo=\"http://www.lexinfo.net/ontology/2.0/lexinfo#\""
            + "   xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\""
            + "   xml:base=\"http://examplelexicon#\">"
            + "  <lemon:Lexicon rdf:about=\"lexicon\">"
            + "    <lemon:entry>"
            + "      <lemon:LexicalEntry>"
            + "        <lemon:canonicalForm>"
            + "          <lemon:LexicalForm>"
            + "            <lemon:writtenRep xml:lang=\"en\">cat</lemon:writtenRep>"
            + "          </lemon:LexicalForm>"
            + "        </lemon:canonicalForm>"
            + "        <lemon:otherForm>"
            + "          <lemon:LexicalForm>"
            + "            <lemon:writtenRep xml:lang=\"en\">cats</lemon:writtenRep>"
            + "            <lexinfo:number rdf:resource=\"http://www.lexinfo.net/ontology/2.0/lexinfo#plural\"/>"
            + "          </lemon:LexicalForm>"
            + "        </lemon:otherForm>"
            + "        <lemon:sense>"
            + "          <lemon:LexicalSense>"
            + "            <lemon:reference rdf:resource=\"http://dbpedia.org/resource/Cat\"/>"
            + "          </lemon:LexicalSense>"
            + "        </lemon:sense>"
            + "      </lemon:LexicalEntry>"
            + "    </lemon:entry>"
            + "  </lemon:Lexicon>"
            + "</rdf:RDF>";

    @Test
    public void testRead() {
        final LemonSerializerImpl simpleLemonSerializer = new LemonSerializerImpl(null);
        final LemonModel model = simpleLemonSerializer.read(new StringReader(testDocument));
        assertEquals(1,model.getLexica().size());
        final Lexicon next = model.getLexica().iterator().next();
        assertEquals(1,next.getEntrys().size());
    }
    
    public static void main(String[] args) {
        System.out.println("hello");
        new XMLLoadingTest().testRead();
    }
}
