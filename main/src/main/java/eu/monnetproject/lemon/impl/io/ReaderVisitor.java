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
package eu.monnetproject.lemon.impl.io;

import eu.monnetproject.lemon.impl.AccepterFactory;
import eu.monnetproject.lemon.LemonModel;
import eu.monnetproject.lemon.LinguisticOntology;
import eu.monnetproject.lemon.impl.LemonModelImpl;
import eu.monnetproject.lemon.model.LexicalEntry;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import net.lexinfo.LexInfo;

/**
 *
 * @author John McCrae
 */
public class ReaderVisitor implements Visitor {

    private final LinguisticOntology lingOnto;
    private final HashMap<Object, ReaderAccepter> accepters = new HashMap<Object, ReaderAccepter>();
    private final AccepterFactory accepterFactory;

    public ReaderVisitor(LemonModelImpl model) {
        lingOnto = new LexInfo();
        this.accepterFactory  = new AccepterFactory(accepters,lingOnto,model);
    }

    public ReaderVisitor(LinguisticOntology lingOnto,LemonModelImpl model) {
        this.lingOnto = lingOnto;
        this.accepterFactory = new AccepterFactory(accepters,lingOnto,model);
    }

    @Override
    public void accept(URI subj, URI pred, URI value) {
        final ReaderAccepter accepter = getAccepter(subj, pred, value);
        final ReaderAccepter accept = accepter.accept(pred, value, lingOnto, accepterFactory);
        addAccepter(value, accept);
    }

    @Override
    public void accept(URI subj, URI pred, String id) {
        final ReaderAccepter accepter = getAccepter(subj, pred, null);
        final ReaderAccepter accept = accepter.accept(pred, id, lingOnto, accepterFactory);
        addAccepter(id, accept);
    }

    @Override
    public void accept(URI subj, URI pred, String val, String lang) {
        final ReaderAccepter accepter = getAccepter(subj, pred, null);
        accepter.accept(pred, val, lang, lingOnto, accepterFactory);

    }

    @Override
    public void accept(String subj, URI pred, URI value) {
        final ReaderAccepter accepter = getAccepter(subj, pred, value);
        final ReaderAccepter accept = accepter.accept(pred, value, lingOnto, accepterFactory);
        addAccepter(value, accept);
    }

    @Override
    public void accept(String subj, URI pred, String id) {
        final ReaderAccepter accepter = getAccepter(subj, pred, null);
        final ReaderAccepter accept = accepter.accept(pred, id, lingOnto, accepterFactory);
        addAccepter(id, accept);
    }

    @Override
    public void accept(String subj, URI pred, String val, String lang) {
        final ReaderAccepter accepter = getAccepter(subj, pred, null);
        accepter.accept(pred, val, lang, lingOnto, accepterFactory);
    }

    private void addAccepter(Object value, ReaderAccepter accept) {
        if (accept != null) {
            if (!accepters.containsKey(value)) {
                accepters.put(value, accept);
            } else if(accepters.get(value) instanceof UnactualizedAccepter && accept instanceof UnactualizedAccepter) {
                ((UnactualizedAccepter)accepters.get(value)).addAll((UnactualizedAccepter)accept);
            } else if (accepters.get(value) instanceof UnactualizedAccepter) {
                final Map<Object, ReaderAccepter> actualizedAs = ((UnactualizedAccepter) accepters.get(value)).actualizedAs(accept, lingOnto, accepterFactory);
                for (Map.Entry<Object, ReaderAccepter> entry : actualizedAs.entrySet()) {
                    addAccepter(entry.getKey(), entry.getValue());
                }
                accepters.put(value, accept);
            } else {
                accepters.get(value).merge(accept, lingOnto, accepterFactory);
            }
        }
    }

    ReaderAccepter getAccepter(Object subj, URI pred, URI value) {
        if (accepters.containsKey(subj) && !(accepters.get(subj) instanceof UnactualizedAccepter)) {
            return accepters.get(subj);
        } else {
            ReaderAccepter accepter = null;
            if (pred.toString().equals("http://www.w3.org/1999/02/22-rdf-syntax-ns#type")) {
                if (value.toString().equals(LemonModel.LEMON_URI + "Lexicon")) {
                    if (subj instanceof URI) {
                        accepter = accepterFactory.getLexiconImpl((URI) subj);
                    } else {
                        accepter = accepterFactory.getLexiconImpl((String)subj);
                    }
                } else if (value.toString().equals(LemonModel.LEMON_URI + "LexicalEntry")) {
                    if(subj instanceof URI) {
                        accepter = accepterFactory.getLexicalEntryImpl((URI) subj);
                    } else {
                        accepter = accepterFactory.getLexicalEntryImpl((String)subj);
                    }
                } else if(value.toString().equals(LemonModel.LEMON_URI + "MorphPattern")) {
                    if(subj instanceof URI) {
                        accepter = accepterFactory.getMorphPatternImpl((URI)subj);
                    } else {
                        accepter = accepterFactory.getMorphPatternImpl((String)subj);
                    }
                } else {
                    accepter = accepters.containsKey(subj) ? accepters.get(subj) : new UnactualizedAccepter();
                }
            } else {
                accepter = accepters.containsKey(subj) ? accepters.get(subj) : new UnactualizedAccepter();
            }
            // Avoid actualizing an unactualized as a an unactualized
            if (!(accepter instanceof UnactualizedAccepter) || !accepters.containsKey(subj) || !(accepters.get(subj) instanceof UnactualizedAccepter)) {
                addAccepter(subj, accepter);
            }
            return accepter;
        }
    }
    
    public LemonModel getModel() { return accepterFactory.getModel(); }

    public LexicalEntry getEntry() {
        return accepterFactory.getEntry();
    }
}
