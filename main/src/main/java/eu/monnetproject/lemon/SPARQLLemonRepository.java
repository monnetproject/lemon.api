/**
 * ********************************************************************************
 * Copyright (c) 2011, Monnet Project All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met: *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. * Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials provided
 * with the distribution. * Neither the name of the Monnet Project nor the names
 * of its contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE MONNET PROJECT BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 ********************************************************************************
 */
package eu.monnetproject.lemon;

import eu.monnetproject.lemon.model.LexicalEntry;
import eu.monnetproject.lemon.model.Lexicon;
import java.io.Reader;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import net.lexinfo.LexInfo;

/**
 * A repository over SPARQL and SPARQL update
 * 
 * @author John McCrae
 */
public class SPARQLLemonRepository implements LemonRepository {

    private final String queryEndpoint, updateEndpoint, updateQueryParam, username, password;
    private final SPARQL dialect;

    public SPARQLLemonRepository(String queryEndpoint, String updateEndpoint, String updateQueryParam) {
        this.queryEndpoint = queryEndpoint;
        this.updateEndpoint = updateEndpoint;
        this.updateQueryParam = updateQueryParam;
        this.username = null;
        this.password = null;
        this.dialect = SPARQL.SPARQL10;
    }
    
    public SPARQLLemonRepository(String queryEndpoint, String updateEndpoint, String updateQueryParam, String username, String password) {
        this.queryEndpoint = queryEndpoint;
        this.updateEndpoint = updateEndpoint;
        this.updateQueryParam = updateQueryParam;
        this.username = username;
        this.password = password;
        this.dialect = SPARQL.SPARQL10;
    }
   
    public SPARQLLemonRepository(String queryEndpoint, String updateEndpoint, String updateQueryParam, SPARQL dialect) {
        this.queryEndpoint = queryEndpoint;
        this.updateEndpoint = updateEndpoint;
        this.updateQueryParam = updateQueryParam;
        this.username = null;
        this.password = null;
        this.dialect = dialect;
    }
    
    public SPARQLLemonRepository(String queryEndpoint, String updateEndpoint, String updateQueryParam, String username, String password, SPARQL dialect) {
        this.queryEndpoint = queryEndpoint;
        this.updateEndpoint = updateEndpoint;
        this.updateQueryParam = updateQueryParam;
        this.username = username;
        this.password = password;
        this.dialect = dialect;
    }

    @Override
    public LemonSerializer connect(final URI uri) {
        return new LemonSerializer() {

            final LemonSerializer serializer = LemonSerializer.newInstance();

            @Override
            public LemonModel read(Reader source) {
                return serializer.read(source);
            }

            @Override
            public void read(LemonModel model, Reader source) {
                serializer.read(model, source);
            }

            @Override
            public LexicalEntry readEntry(Reader source) {
                return serializer.readEntry(source);
            }

            @Override
            public LemonModel create() {
                try {
                    return LemonModels.sparqlUpdateEndpoint(new URL(queryEndpoint), uri, new LexInfo(), updateEndpoint + "?" + updateQueryParam + "=", username, password,dialect);
                } catch (MalformedURLException x) {
                    throw new RuntimeException(x);
                }
            }

            @Override
            @Deprecated
            public LemonModel create(URI graph) {
                if (!graph.equals(uri)) {
                    throw new RuntimeException();
                }
                try {
                    return LemonModels.sparqlUpdateEndpoint(new URL(queryEndpoint), uri, new LexInfo(), updateEndpoint + "?" + updateQueryParam + "=", username, password,dialect);
                } catch (MalformedURLException x) {
                    throw new RuntimeException(x);
                }
            }

            @Override
            public void write(LemonModel model, Writer target) {
                serializer.write(model, target);

            }

            @Override
            public void writeEntry(LemonModel model, LexicalEntry entry, LinguisticOntology lingOnto, Writer target) {
                serializer.writeEntry(model, entry, lingOnto, target);
            }

            @Override
            public void writeLexicon(LemonModel model, Lexicon lexicon, LinguisticOntology lingOnto, Writer target) {
                serializer.writeLexicon(model, lexicon, lingOnto, target);
            }

            @Override
            public void write(LemonModel model, Writer target, boolean xml) {
                serializer.write(model, target, xml);
            }

            @Override
            public void writeEntry(LemonModel model, LexicalEntry entry, LinguisticOntology lingOnto, Writer target, boolean xml) {
                serializer.writeEntry(model, entry, lingOnto, target, xml);
            }

            @Override
            public void writeLexicon(LemonModel model, Lexicon lexicon, LinguisticOntology lingOnto, Writer target, boolean xml) {
                serializer.writeLexicon(model, lexicon, lingOnto, target, xml);
            }

            @Override
            public void moveLexicon(Lexicon lexicon, LemonModel from, LemonModel to) {
                serializer.moveLexicon(lexicon, from, to);
            }

            @Override
            public void close() {
                serializer.close();
            }
        };
    }
}
