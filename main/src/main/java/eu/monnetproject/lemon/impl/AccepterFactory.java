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
import eu.monnetproject.lemon.impl.io.UnactualizedAccepter;
import eu.monnetproject.lemon.model.LexicalEntry;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author John McCrae
 */
public class AccepterFactory {

    final HashMap<Object, ReaderAccepter> accepters;
    final LemonModelImpl model;
    final LinguisticOntology lingOnto;
    LexicalEntryImpl firstEntry = null;

    public AccepterFactory(HashMap<Object, ReaderAccepter> accepters, LinguisticOntology lingOnto, LemonModelImpl model) {
        this.accepters = accepters;
        this.lingOnto = lingOnto;
        this.model = model;
    }

    private void addAccepter(Object value, ReaderAccepter accept) {
        if (accept != null) {
            if (!accepters.containsKey(value)) {
                accepters.put(value, accept);
            } else if (accepters.get(value) instanceof UnactualizedAccepter) {
                final Map<Object, ReaderAccepter> actualizedAs = ((UnactualizedAccepter) accepters.get(value)).actualizedAs(accept, lingOnto, this);
                for (Map.Entry<Object, ReaderAccepter> entry : actualizedAs.entrySet()) {
                    addAccepter(entry.getKey(), entry.getValue());
                }
                accepters.put(value, accept);
            } else {
                accepters.get(value).merge(accept, lingOnto, this);
            }
        }
    }

    private <E extends ReaderAccepter> E get(Class<E> clazz, URI uri) {
        if (accepters.containsKey(uri)) {
            if (clazz.isInstance(accepters.get(uri))) {
                return (E) accepters.get(uri);
            } else if (accepters.get(uri) instanceof UnactualizedAccepter) {
                final E accepter = make(clazz, uri);
                addAccepter(uri, accepter);
                return accepter;
            } else {
                throw new IllegalStateException("Model already contains object of type " + accepters.get(uri).getClass().getName() + " for URI: " + uri + " but was attempted to create as " + clazz.getName());
            }
        } else {
            return make(clazz, uri);
        }
    }

    private <E extends ReaderAccepter> E get(Class<E> clazz, String bNode) {
        if (accepters.containsKey(bNode)) {
            if (clazz.isInstance(accepters.get(bNode))) {
                return (E) accepters.get(bNode);
            } else if (accepters.get(bNode) instanceof UnactualizedAccepter) {
                final E accepter = make(clazz, bNode);
                addAccepter(bNode, accepter);
                return accepter;
            } else {
                throw new IllegalStateException("Model already contains object of type " + accepters.get(bNode).getClass().getName() + " for BNode: " + bNode + " but was attempted to create as " + clazz.getName());
            }
        } else {
            return make(clazz, bNode);
        }
    }

    public <E extends ReaderAccepter> E make(Class<E> clazz, URI uri) {
        if (clazz.equals(ArgumentImpl.class)) {
            return (E) new ArgumentImpl(uri,model);
        } else if (clazz.equals(ComponentImpl.class)) {
            return (E) new ComponentImpl(uri,model);
        } else if (clazz.equals(ConditionImpl.class)) {
            return (E) new ConditionImpl(uri,model);
        } else if (clazz.equals(ConstituentImpl.class)) {
            return (E) new ConstituentImpl(uri,model);
        } else if (clazz.equals(ContextImpl.class)) {
            return (E) new ContextImpl(uri,model);
        } else if (clazz.equals(DefinitionImpl.class)) {
            return (E) new DefinitionImpl(uri,model);
        } else if (clazz.equals(ExampleImpl.class)) {
            return (E) new ExampleImpl(uri,model);
        } else if (clazz.equals(FormImpl.class)) {
            return (E) new FormImpl(uri,model);
        } else if (clazz.equals(FrameImpl.class)) {
            return (E) new FrameImpl(uri,model);
        } else if (clazz.equals(LexicalEntryImpl.class)) {
            final LexicalEntryImpl name = new LexicalEntryImpl(uri,model);
            if(firstEntry == null) {
                firstEntry = (LexicalEntryImpl)name;
            }
            return (E)name;
        } else if (clazz.equals(LexicalSenseImpl.class)) {
            return (E) new LexicalSenseImpl(uri,model);
        } else if (clazz.equals(MorphPatternImpl.class)) {
            return (E) new MorphPatternImpl(uri,model);
        } else if (clazz.equals(MorphTransformImpl.class)) {
            return (E) new MorphTransformImpl(uri,model);
        } else if (clazz.equals(NodeImpl.class)) {
            return (E) new NodeImpl(uri,model);
        } else if (clazz.equals(PartImpl.class)) {
            return (E) new PartImpl(uri,model);
        } else if (clazz.equals(PhraseImpl.class)) {
            return (E) new PhraseImpl(uri,model);
        } else if (clazz.equals(PrototypeImpl.class)) {
            return (E) new PrototypeImpl(uri,model);
        } else if (clazz.equals(TopicImpl.class)) {
            return (E) new TopicImpl(uri,model);
        } else if (clazz.equals(WordImpl.class)) {
            return (E) new WordImpl(uri,model);
        } else {
            //return null;
            throw new RuntimeException("Unknown type");
        }
    }

    public <E extends ReaderAccepter> E make(Class<E> clazz, String bNode) {
        if (clazz.equals(ArgumentImpl.class)) {
            return (E) new ArgumentImpl(bNode,model);
        } else if (clazz.equals(ComponentImpl.class)) {
            return (E) new ComponentImpl(bNode,model);
        } else if (clazz.equals(ConditionImpl.class)) {
            return (E) new ConditionImpl(bNode,model);
        } else if (clazz.equals(ConstituentImpl.class)) {
            return (E) new ConstituentImpl(bNode,model);
        } else if (clazz.equals(ContextImpl.class)) {
            return (E) new ContextImpl(bNode,model);
        } else if (clazz.equals(DefinitionImpl.class)) {
            return (E) new DefinitionImpl(bNode,model);
        } else if (clazz.equals(ExampleImpl.class)) {
            return (E) new ExampleImpl(bNode,model);
        } else if (clazz.equals(FormImpl.class)) {
            return (E) new FormImpl(bNode,model);
        } else if (clazz.equals(FrameImpl.class)) {
            return (E) new FrameImpl(bNode,model);
        } else if (clazz.equals(LexicalEntryImpl.class)) {
            return (E) new LexicalEntryImpl(bNode,model);
        } else if (clazz.equals(LexicalSenseImpl.class)) {
            return (E) new LexicalSenseImpl(bNode,model);
        } else if (clazz.equals(MorphPatternImpl.class)) {
            final MorphPatternImpl pattern = new MorphPatternImpl(bNode,model);
            model.addPattern(pattern);
            return (E) pattern;
        } else if (clazz.equals(MorphTransformImpl.class)) {
            return (E) new MorphTransformImpl(bNode,model);
        } else if (clazz.equals(NodeImpl.class)) {
            return (E) new NodeImpl(bNode,model);
        } else if (clazz.equals(PartImpl.class)) {
            return (E) new PartImpl(bNode,model);
        } else if (clazz.equals(PhraseImpl.class)) {
            return (E) new PhraseImpl(bNode,model);
        } else if (clazz.equals(PrototypeImpl.class)) {
            return (E) new PrototypeImpl(bNode,model);
        } else if (clazz.equals(TopicImpl.class)) {
            return (E) new TopicImpl(bNode,model);
        } else if (clazz.equals(WordImpl.class)) {
            return (E) new WordImpl(bNode,model);
        } else {
            //return null;
            throw new RuntimeException("Unknown type");
        }
    }

    public ArgumentImpl getArgumentImpl(URI uri) {
        return get(ArgumentImpl.class, uri);
    }

    public ArgumentImpl getArgumentImpl(String bNode) {
        return get(ArgumentImpl.class, bNode);
    }

    public ComponentImpl getComponentImpl(URI uri) {
        return get(ComponentImpl.class, uri);
    }

    public ComponentImpl getComponentImpl(String bNode) {
        return get(ComponentImpl.class, bNode);
    }

    public ConditionImpl getConditionImpl(URI uri) {
        return get(ConditionImpl.class, uri);
    }

    public ConditionImpl getConditionImpl(String bNode) {
        return get(ConditionImpl.class, bNode);
    }

    public ConstituentImpl getConstituentImpl(URI uri) {
        return get(ConstituentImpl.class, uri);
    }

    public ConstituentImpl getConstituentImpl(String bNode) {
        return get(ConstituentImpl.class, bNode);
    }

    public ContextImpl getContextImpl(URI uri) {
        return get(ContextImpl.class, uri);
    }

    public ContextImpl getContextImpl(String bNode) {
        return get(ContextImpl.class, bNode);
    }

    public DefinitionImpl getDefinitionImpl(URI uri) {
        return get(DefinitionImpl.class, uri);
    }

    public DefinitionImpl getDefinitionImpl(String bNode) {
        return get(DefinitionImpl.class, bNode);
    }

    public ExampleImpl getExampleImpl(URI uri) {
        return get(ExampleImpl.class, uri);
    }

    public ExampleImpl getExampleImpl(String bNode) {
        return get(ExampleImpl.class, bNode);
    }

    public FormImpl getFormImpl(URI uri) {
        return get(FormImpl.class, uri);
    }

    public FormImpl getFormImpl(String bNode) {
        return get(FormImpl.class, bNode);
    }

    public FrameImpl getFrameImpl(URI uri) {
        return get(FrameImpl.class, uri);
    }

    public FrameImpl getFrameImpl(String bNode) {
        return get(FrameImpl.class, bNode);
    }

    public LexicalEntryImpl getLexicalEntryImpl(URI uri) {
        return get(LexicalEntryImpl.class, uri);
    }

    public LexicalEntryImpl getLexicalEntryImpl(String bNode) {
        return get(LexicalEntryImpl.class, bNode);
    }

    public LexicalSenseImpl getLexicalSenseImpl(URI uri) {
        return get(LexicalSenseImpl.class, uri);
    }

    public LexicalSenseImpl getLexicalSenseImpl(String bNode) {
        return get(LexicalSenseImpl.class, bNode);
    }

    public MorphPatternImpl getMorphPatternImpl(URI uri) {
        
        if (accepters.containsKey(uri)) {
            if(accepters.get(uri) instanceof LexiconImpl) {
                return (MorphPatternImpl) accepters.get(uri);
            } else {
                final MorphPatternImpl actual = new MorphPatternImpl(uri,model);
                addAccepter(uri, actual);
                return actual;
            }
        } else {
            final MorphPatternImpl patternImpl = new MorphPatternImpl(uri, model);
            model.addPattern(patternImpl);
            return patternImpl;
        }
    }

    public MorphPatternImpl getMorphPatternImpl(String bNode) {
         if (accepters.containsKey(bNode)) {
            return (MorphPatternImpl) accepters.get(bNode);
        } else {
            final MorphPatternImpl MorphPatternImpl = new MorphPatternImpl(bNode, model);
            model.addPattern(MorphPatternImpl);
            return MorphPatternImpl;
        }
    }

    public MorphTransformImpl getMorphTransformImpl(URI uri) {
        return get(MorphTransformImpl.class, uri);
    }

    public MorphTransformImpl getMorphTransformImpl(String bNode) {
        return get(MorphTransformImpl.class, bNode);
    }

    public NodeImpl getNodeImpl(URI uri) {
        return get(NodeImpl.class, uri);
    }

    public NodeImpl getNodeImpl(String bNode) {
        return get(NodeImpl.class, bNode);
    }

    public PartImpl getPartImpl(URI uri) {
        return get(PartImpl.class, uri);
    }

    public PartImpl getPartImpl(String bNode) {
        return get(PartImpl.class, bNode);
    }

    public PhraseImpl getPhraseImpl(URI uri) {
        return get(PhraseImpl.class, uri);
    }

    public PhraseImpl getPhraseImpl(String bNode) {
        return get(PhraseImpl.class, bNode);
    }

    public PrototypeImpl getPrototypeImpl(URI uri) {
        return get(PrototypeImpl.class, uri);
    }

    public PrototypeImpl getPrototypeImpl(String bNode) {
        return get(PrototypeImpl.class, bNode);
    }

    public TopicImpl getTopicImpl(URI uri) {
        return get(TopicImpl.class, uri);
    }

    public TopicImpl getTopicImpl(String bNode) {
        return get(TopicImpl.class, bNode);
    }

    public WordImpl getWordImpl(URI uri) {
        return get(WordImpl.class, uri);
    }

    public WordImpl getWordImpl(String bNode) {
        return get(WordImpl.class, bNode);
    }

    public LexiconImpl getLexiconImpl(URI uri) {
        if (accepters.containsKey(uri)) {
            if(accepters.get(uri) instanceof LexiconImpl) {
                return (LexiconImpl) accepters.get(uri);
            } else {
                final LexiconImpl actual = new LexiconImpl(uri,model);
                addAccepter(uri, actual);
                return actual;
            }
        } else {
            final LexiconImpl lexiconImpl = new LexiconImpl(uri, model);
            model.addLexicon(lexiconImpl);
            return lexiconImpl;
        }
    }

    public LexiconImpl getLexiconImpl(String bNode) {
        if (accepters.containsKey(bNode)) {
            return (LexiconImpl) accepters.get(bNode);
        } else {
            final LexiconImpl lexiconImpl = new LexiconImpl(bNode, model);
            model.addLexicon(lexiconImpl);
            return lexiconImpl;
        }
    }

    public LemonModel getModel() {
        return model;
    }

    public LexicalEntry getEntry() {
        return firstEntry;
    }
}
