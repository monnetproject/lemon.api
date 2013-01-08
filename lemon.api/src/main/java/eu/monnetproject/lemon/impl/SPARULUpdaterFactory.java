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

import eu.monnetproject.lemon.LemonModel;
import eu.monnetproject.lemon.RemoteUpdater;
import eu.monnetproject.lemon.RemoteUpdaterFactory;
import eu.monnetproject.lemon.SPARQL;
import java.net.URI;

/**
 *
 * @author John McCrae
 */
public class SPARULUpdaterFactory implements RemoteUpdaterFactory {

    private final String url;
    private final URI graph;
    private final String password, username;
    private final SPARQL dialect;

    public SPARULUpdaterFactory(String url, URI graph, SPARQL dialect) {
        this.url = url;
        this.graph = graph;
        this.password = null;
        this.username = null;
        this.dialect = dialect;
    }
    
    public SPARULUpdaterFactory(String url, URI graph, String password, String username, SPARQL dialect) {
        this.url = url;
        this.graph = graph;
        this.password = password;
        this.username = username;
        this.dialect = dialect;
    }
    
    
    
    @Override
    public RemoteUpdater updaterForModel(LemonModel model) {
        if(username != null && password != null) {
            return new SPARULUpdater(url, graph, username, password, dialect);
        } else {
            return new SPARULUpdater(url, graph, dialect);
        }
    }

}
