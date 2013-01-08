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
import eu.monnetproject.lemon.LinguisticOntology;
import eu.monnetproject.lemon.impl.io.ReaderAccepter;
import eu.monnetproject.lemon.model.MorphTransform;
import eu.monnetproject.lemon.model.Prototype;
import java.net.URI;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author John McCrae
 */
public class MorphTransformImpl extends LemonElementImpl<MorphTransform> implements MorphTransform {
    private static final long serialVersionUID = 5402920069758179942L;

    private Set<String> rules = new HashSet<String>();
    
    public MorphTransformImpl(String id, LemonModelImpl model) {
        super(id, "MorphTransform",model);
    }

    public MorphTransformImpl(URI uri, LemonModelImpl model) {
        super(uri, "MorphTransform",model);
    }

    @Override
    public Collection<String> getRules() {
        if(checkRemote) {
            resolveRemote();
        }
        return rules;
    }

    @Override
    public boolean addRule(String string) {
        checkRemote = false;
        if(model.allowUpdate()) {
            if(getURI() != null) {
                model.updater().add(getURI(), URI.create(LemonModel.LEMON_URI+"rule"), string, (String)null);
            } else {
                model.updater().add(getID(), URI.create(LemonModel.LEMON_URI+"rule"), string, (String)null);
            }
        }
        return rules.add(string);
    }
    
    
    @Override
    public boolean removeRule(String string) {
        checkRemote = false;
        if(model.allowUpdate()) {
            if(getURI() != null) {
                model.updater().remove(getURI(), URI.create(LemonModel.LEMON_URI+"rule"), string, (String)null);
            } else {
                model.updater().remove(getID(), URI.create(LemonModel.LEMON_URI+"rule"), string, (String)null);
            }
        }
        return rules.remove(string);
    }

    @Override
    public Prototype getOnStem() {
        return (Prototype)getStrElem("onStem");
    }

    @Override
    public void setOnStem(Prototype prtp) {
        setStrElem("onStem",prtp);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<Prototype> getGenerates() {
        return (Collection)getStrElems("generates");
    }

    @Override
    public boolean addGenerates(Prototype prtp) {
        return addStrElem("generates", prtp);
    }

    @Override
    public boolean removeGenerates(Prototype prtp) {
        return removeStrElem("generates", prtp);
    }


    @Override
    @SuppressWarnings("unchecked")
    public Collection<MorphTransform> getNextTransforms() {
        return (Collection)getStrElems("nextTransform");
    }

    @Override
    public boolean addNextTransform(MorphTransform mt) {
        return addStrElem("nextTransform", mt);
    }

    @Override
    public boolean removeNextTransform(MorphTransform mt) {
        return removeStrElem("nextTransform",mt);
    }

    @Override
    public ReaderAccepter accept(URI pred, URI value, LinguisticOntology lingOnto, AccepterFactory factory) {
        if(pred.toString().equals(LemonModel.LEMON_URI+"generates")) {
            final PrototypeImpl prototypeImpl = factory.getPrototypeImpl(value);
            addStrElemDirect("generates", prototypeImpl);
            return prototypeImpl;
        } else if(pred.toString().equals(LemonModel.LEMON_URI+"nextTransform")) {
            final MorphTransformImpl morphTransformImpl = factory.getMorphTransformImpl(value);
            addStrElemDirect("nextTransform", morphTransformImpl);
            return morphTransformImpl;
        } else if(pred.toString().equals(LemonModel.LEMON_URI+"onStem")) {
            final PrototypeImpl prototypeImpl = factory.getPrototypeImpl(value);
            setStrElemDirect("onStem", prototypeImpl);
            return prototypeImpl;
        } else {
            return defaultAccept(pred, value,lingOnto);
        }
    }

    @Override
    public ReaderAccepter accept(URI pred, String value, LinguisticOntology lingOnto, AccepterFactory factory) {
        if(pred.toString().equals(LemonModel.LEMON_URI+"generates")) {
            final PrototypeImpl prototypeImpl = factory.getPrototypeImpl(value);
            addStrElemDirect("generates", prototypeImpl);
            return prototypeImpl;
        } else if(pred.toString().equals(LemonModel.LEMON_URI+"nextTransform")) {
            final MorphTransformImpl morphTransformImpl = factory.getMorphTransformImpl(value);
            addStrElemDirect("nextTransform", morphTransformImpl);
            return morphTransformImpl;
        } else if(pred.toString().equals(LemonModel.LEMON_URI+"onStem")) {
            final PrototypeImpl prototypeImpl = factory.getPrototypeImpl(value);
            setStrElemDirect("onStem", prototypeImpl);
            return prototypeImpl;
        } else {
            return defaultAccept(pred, value);
        }
    }

    @Override
    public void accept(URI pred, String value, String lang, LinguisticOntology lingOnto, AccepterFactory factory) {
        if(pred.toString().equals(LemonModel.LEMON_URI+"rule")) {
            rules.add(value);
        } else {
            defaultAccept(pred, value, lang);
        }
    }
    
    @Override
    public void merge(ReaderAccepter accepter, LinguisticOntology lingOnto, AccepterFactory factory) {
        if(accepter instanceof MorphTransformImpl) {
            rules.addAll(((MorphTransformImpl)accepter).rules);
        }
        defaultMerge(accepter, lingOnto, factory);
    }
}
