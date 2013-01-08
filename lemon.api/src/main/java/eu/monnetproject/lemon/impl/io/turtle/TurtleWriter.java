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
package eu.monnetproject.lemon.impl.io.turtle;

import eu.monnetproject.lemon.LemonModel;
import eu.monnetproject.lemon.LinguisticOntology;
import eu.monnetproject.lemon.AbstractVisitor;
import eu.monnetproject.lemon.impl.SerializationState;
import eu.monnetproject.lemon.impl.LemonElementImpl;
import eu.monnetproject.lemon.model.LemonElement;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 *
 * @author John McCrae
 */
public class TurtleWriter extends AbstractVisitor {

    private final SerializationState state = new SerializationState();
    private final HashSet<LemonElementImpl> visited = new HashSet<LemonElementImpl>();
    private final HashMap<String, String> header = new HashMap<String, String>();
    private final StringWriter sw = new StringWriter();
    private final PrintWriter writer = new PrintWriter(sw);

    public TurtleWriter(LinguisticOntology lingOnto) {
        super(lingOnto);
        header.put("lemon", LemonModel.LEMON_URI);
        header.put("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
    }

    private void updateHeaders(URI uri) {
        if(uri == null)
            return;
        final int idx = Math.max(uri.toString().lastIndexOf("#"), uri.toString().lastIndexOf("/"));
        if (idx <= 0) {
            return;
        }
        final String prefixURI = uri.toString().substring(0, idx + 1);
        final int idx2 = prefixURI.substring(0,prefixURI.length()-1).lastIndexOf("/");
        if (idx2 <= 0) {
            return;
        }
        final String prefix = prefixURI.substring(idx2 + 1, idx);
        if (!header.containsValue(prefixURI)) {
            header.put(prefix, prefixURI);
        }
    }

    @Override
    public void visit(LemonElement _element) {
        if(!(_element instanceof LemonElementImpl)) {
            throw new IllegalArgumentException();
        }
        final LemonElementImpl<?> element = (LemonElementImpl)_element;
        element.write(writer, state);
        if (element.getURI() != null) {
            updateHeaders(element.getURI());
        }
        for (Collection<Object> objs : (Collection<Collection<Object>>) element.getElements().values()) {
            for (Object obj : objs) {
                if (obj instanceof LemonElement) {
                    updateHeaders(((LemonElement)obj).getURI());
                }
            }
        }
        visited.add(element);
    }

    @Override
    public boolean hasVisited(LemonElement element) {
        return visited.contains(element);
    }

    private final String LS = System.getProperty("line.separator");
    
    private StringBuilder getHeaders() {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, String> head : header.entrySet()) {
            if (head.getKey().equals("")) {
                builder.append("@base <").append(head.getValue()).append("> . "+LS);
            } else {
                builder.append("@prefix ").append(head.getKey()).append(": <").append(head.getValue()).append("> . "+LS);
            }
        }
        return builder.append(LS);
    }

    public String getDocument() {
        while (!state.postponed.isEmpty()) {
            ((LemonElementImpl) state.postponed.pop()).write(writer, state);
        }

        return getHeaders().append(sw.toString()).toString();
    }
}
