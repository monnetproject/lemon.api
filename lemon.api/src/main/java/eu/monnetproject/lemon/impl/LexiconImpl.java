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
package eu.monnetproject.lemon.impl;

import eu.monnetproject.lemon.impl.io.ReaderAccepter;
import eu.monnetproject.lemon.model.MorphPattern;
import java.net.URI;
import java.util.*;
import eu.monnetproject.lemon.*;
import eu.monnetproject.lemon.model.LexicalEntry;
import eu.monnetproject.lemon.model.LexicalTopic;
import eu.monnetproject.lemon.model.Lexicon;

/**
 * Instantiated via {@link LemonModelImpl}
 *
 * @author John McCrae
 */
public class LexiconImpl extends LemonElementImpl implements Lexicon {
    public static final URI LANGUAGE = URI.create(LemonModel.LEMON_URI+"language");

    private static final long serialVersionUID = 2163929528117965687L;
    private String language;
    private boolean checkRemoteLang, checkRemoteTopic, checkRemoteEntry;
    
    public LexiconImpl(URI uri, LemonModelImpl model) {
        super(uri, "Lexicon", model);
        checkRemoteLang = checkRemoteTopic = checkRemoteEntry = model.allowRemote();
    }

    public LexiconImpl(String id, LemonModelImpl model) {
        super(id, "Lexicon", model);
        checkRemoteLang = checkRemoteTopic = checkRemoteEntry = model.allowRemote();
    }
    
    

    @Override
    public String getLanguage() {
        if (checkRemoteLang) {
            checkRemoteLang = false;
            model.resolver().resolveRemoteFiltered(model, LANGUAGE, this);
        }
        return language != null ? language : "und";
    }

    @Override
    public void setLanguage(final String language) {
        checkRemoteLang = false;
        if (model.allowUpdate()) {
            if (this.language != null) {
                if (getURI() != null) {
                    model.updater().remove(getURI(), LANGUAGE, this.language, (String) null);
                } else {
                    model.updater().remove(getID(), LANGUAGE, this.language, (String) null);
                }
            }
            if (language != null) {
                if (getURI() != null) {
                    model.updater().add(getURI(), LANGUAGE, language, (String) null);
                } else {
                    model.updater().add(getID(), LANGUAGE, this.language, (String) null);
                }
            }
        }
        this.language = language;
    }

    @Override
    public boolean hasEntry(final LexicalEntry entry) {
        return getStrElems("entry").contains(entry);
    }

    @Override
    public int countEntrys() {
        if(model.resolver() != null) {
            return model.resolver().resolveRemoteEntryCount(model, this);
        } else {
            return getStrElems("entry").size();
        }
    }

    @Override
    public Collection<LexicalEntry> getEntrys() {
        if (checkRemoteEntry) {
            checkRemoteEntry = false;
            model.resolver().resolveRemoteFiltered(model, URI.create(LemonModel.LEMON_URI+"entry"), this);
        }
        return (Collection<LexicalEntry>) getStrElems("entry");
    }

    @Override
    public boolean addEntry(final LexicalEntry entry) {
        return addStrElem("entry", entry);
    }

    @Override
    public boolean removeEntry(final LexicalEntry entry) {
        return removeStrElem("entry", entry);
    }

    @Override
    public Collection<LexicalTopic> getTopics() {
        if (checkRemoteTopic) {
            checkRemoteTopic = false;
            model.resolver().resolveRemoteFiltered(model, URI.create(LemonModel.LEMON_URI+"topic"), this);
        }
        return (Collection<LexicalTopic>) getStrElems("topic");
    }

    @Override
    public boolean addTopic(final LexicalTopic topic) {
        return addStrElem("topic", topic);
    }

    @Override
    public boolean removeTopic(final LexicalTopic topic) {
        return removeStrElem("topic", topic);
    }

    @Override
    public LemonModel getModel() {
        return model;
    }

    @Override
    public ReaderAccepter accept(URI pred, URI value, LinguisticOntology lingOnto, AccepterFactory factory) {
        if (pred.toString().equals(LemonModel.LEMON_URI + "entry")) {
            final LexicalEntryImpl lexicalEntryImpl = factory.getLexicalEntryImpl(value);
            addStrElemDirect("entry", lexicalEntryImpl);
            return lexicalEntryImpl;
        } else if (pred.toString().equals(LemonModel.LEMON_URI + "topic")) {
            final TopicImpl topicImpl = factory.getTopicImpl(value);
            addStrElemDirect("topic", topicImpl);
            return topicImpl;
        }
        return defaultAccept(pred, value, lingOnto);
    }

    @Override
    public ReaderAccepter accept(URI pred, String value, LinguisticOntology lingOnto, AccepterFactory factory) {
        if (pred.toString().equals(LemonModel.LEMON_URI + "entry")) {
            final LexicalEntryImpl lexicalEntryImpl = factory.getLexicalEntryImpl(value);
            addStrElemDirect("entry", lexicalEntryImpl);
            return lexicalEntryImpl;
        } else if (pred.toString().equals(LemonModel.LEMON_URI + "topic")) {
            final TopicImpl topicImpl = factory.getTopicImpl(value);
            addStrElemDirect("topic", topicImpl);
            return topicImpl;
        }
        return defaultAccept(pred, value);
    }

    @Override
    public void accept(URI pred, String value, String lang, LinguisticOntology lingOnto, AccepterFactory factory) {
        if (pred.toString().equals(LemonModel.LEMON_URI + "language")) {
            language = value;
        } else {
            defaultAccept(pred, value, lang);
        }
    }

    @Override
    public Collection<MorphPattern> getPatterns() {
        return getStrElems("pattern");
    }

    @Override
    public boolean addPattern(MorphPattern mp) {
        return addStrElem("pattern", mp);
    }

    @Override
    public boolean removePattern(MorphPattern mp) {
        return removeStrElem("pattern", mp);
    }

    @Override
    public Map<URI, Collection<Object>> getElements() {
        final Map<URI, Collection<Object>> elements = super.getElements();
        if(language != null) {
            elements.put(LANGUAGE, Collections.singletonList((Object) language));
        }
        return elements;
    }

    @Override
    public void merge(ReaderAccepter accepter, LinguisticOntology lingOnto, AccepterFactory factory) {
        if (accepter instanceof LexiconImpl) {
            final LexiconImpl li = (LexiconImpl) accepter;
            if (this.language == null && li.language != null) {
                this.language = li.language;
            } else if (this.language != null && li.language != null && !this.language.equals(li.language)) {
                throw new RuntimeException("Merge exception");
            }
        }
        defaultMerge(accepter, lingOnto, factory);
    }

    @Override
    protected void resolveRemote() {
        checkRemote = false;
    }
}
