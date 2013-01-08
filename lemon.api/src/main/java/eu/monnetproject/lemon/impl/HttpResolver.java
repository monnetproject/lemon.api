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
package eu.monnetproject.lemon.impl;

import eu.monnetproject.lemon.impl.io.turtle.TurtleParser;
import eu.monnetproject.lemon.impl.io.xml.RDFXMLReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URI;
import java.net.URLConnection;
import java.util.List;

/**
 *
 * @author John McCrae
 */
public class HttpResolver implements RemoteResolver {

    @Override
    public void resolveRemote(LemonModelImpl model, LemonElementImpl<?> element, int depth) {
        final URI uri = element.getURI();
        if (uri != null && (uri.getScheme().equals("http")
                || uri.getScheme().equals("https")
                || uri.getScheme().equals("ftp"))) {
            try {
                final URLConnection connection = uri.toURL().openConnection();
                final InputStream source = connection.getInputStream();

                StringBuilder sb = new StringBuilder();
                try {
                    byte[] buf = new byte[1024];
                    int s;
                    while ((s = source.read(buf)) != -1) {
                        sb.append(new String(buf, 0, s));
                    }
                } catch (IOException x) {
                    throw new RuntimeException(x);
                }
                final RDFXMLReader rdfXMLReader = new RDFXMLReader(model);
                try {
                    rdfXMLReader.parse(new StringReader(sb.toString()));
                } catch (Exception ex) {
                    try {
                        final TurtleParser parser = new TurtleParser(new StringReader(sb.toString()), model);
                        parser.parse();
                    } catch (Exception ex2) {
                        ex.printStackTrace();
                        throw new RuntimeException(ex2);
                    }
                }
            } catch (Exception x) {
            }
        }
    }

    @Override
    public void resolveRemoteFiltered(LemonModelImpl model, URI property, LemonElementImpl<?> element) {
        // can't filter, don't filter
        resolveRemote(model, element,0);
    }
    
    @Override
    public <T> List<T> resolveRemoteList(Object identifier, Class<T> clazz, LemonModelImpl model) {
        return null;
    }

    @Override
    public int resolveRemoteEntryCount(LemonModelImpl model, LexiconImpl lexicon) {
        throw new UnsupportedOperationException("TODO");
    }
}
