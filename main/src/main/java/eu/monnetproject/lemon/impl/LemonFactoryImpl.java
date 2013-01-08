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

import eu.monnetproject.lemon.*;
import eu.monnetproject.lemon.model.Argument;
import eu.monnetproject.lemon.model.Component;
import eu.monnetproject.lemon.model.Example;
import eu.monnetproject.lemon.model.Frame;
import eu.monnetproject.lemon.model.LemonElement;
import eu.monnetproject.lemon.model.LemonElementOrPredicate;
import eu.monnetproject.lemon.model.LexicalEntry;
import eu.monnetproject.lemon.model.LexicalForm;
import eu.monnetproject.lemon.model.LexicalSense;
import eu.monnetproject.lemon.model.MorphPattern;
import eu.monnetproject.lemon.model.MorphTransform;
import eu.monnetproject.lemon.model.Node;
import eu.monnetproject.lemon.model.Part;
import eu.monnetproject.lemon.model.Phrase;
import eu.monnetproject.lemon.model.Prototype;
import eu.monnetproject.lemon.model.SenseCondition;
import eu.monnetproject.lemon.model.SenseDefinition;
import eu.monnetproject.lemon.model.Word;
import java.lang.reflect.Constructor;
import java.net.URI;
import java.util.*;

/**
 * Instantiated via {@link LemonModelImpl}
 * @author John McCrae
 */
public class LemonFactoryImpl implements LemonFactory {
    private final HashMap<URI, LemonElementOrPredicate> elements;
    private final LemonModelImpl model;

    LemonFactoryImpl(HashMap<URI, LemonElementOrPredicate> elements, LemonModelImpl model) {
        this.elements = elements;
        this.model = model;
    }
    private final Random random = new Random();

    private String bn() {
        return "n"+Long.toString(Math.abs(random.nextLong()));
    }

    @Override
    public boolean isURIUsed(URI uri) {
        return elements.containsKey(uri);
    }
    
    private <Elem extends LemonElement> Elem log(Elem elem, URI uri) {
        if (elements.containsKey(uri)) {
            LemonElementOrPredicate e = elements.get(uri);
            if (elem.getClass().isInstance(e)) {
                return (Elem) e;
            } else {
                return elem;
            }
        } else {
            elements.put(uri, elem);
            return elem;
        }
    }

    public Argument makeArgument() {
        return new ArgumentImpl(bn(),model);
    }

    public Argument makeArgument(URI uri) {
        return log(new ArgumentImpl(uri,model), uri);
    }

    public SenseCondition makeCondition() {
        return new ConditionImpl(bn(),model);
    }

    public SenseCondition makeCondition(URI uri) {
        return log(new ConditionImpl(uri,model), uri);
    }

    public Component makeComponent() {
        return new ComponentImpl(bn(),model);
    }

    public Component makeComponent(URI uri) {
        return log(new ComponentImpl(uri,model), uri);
    }

    public SenseDefinition makeDefinition() {
        return new DefinitionImpl(bn(),model);
    }

    public SenseDefinition makeDefinition(URI uri) {
        return log(new DefinitionImpl(uri,model), uri);
    }

    public Example makeExample() {
        return new ExampleImpl(bn(),model);
    }

    public Example makeExample(URI uri) {
        return log(new ExampleImpl(uri,model), uri);
    }

    public LexicalForm makeForm() {
        return new FormImpl(bn(),model);
    }

    public LexicalForm makeForm(URI uri) {
        return log(new FormImpl(uri,model), uri);
    }

    public Frame makeFrame() {
        return new FrameImpl(bn(),model);
    }

    public Frame makeFrame(URI uri) {
        return log(new FrameImpl(uri,model), uri);
    }

    public LexicalEntry makeLexicalEntry() {
        return new LexicalEntryImpl(bn(),model);
    }

    public LexicalEntry makeLexicalEntry(URI uri) {
        return log(new LexicalEntryImpl(uri,model), uri);
    }

    public Part makePart(URI uri) {
        return log(new PartImpl(uri,model), uri);
    }

    public Phrase makePhrase(URI uri) {
        return log(new PhraseImpl(uri,model), uri);
    }

    public Word makeWord(URI uri) {
        return log(new WordImpl(uri,model), uri);
    }

    public MorphPattern makeMorphPattern() {
        final MorphPatternImpl pattern = new MorphPatternImpl(bn(),model);
        model.addPattern(pattern);
        return pattern;
    }

    public MorphPattern makeMorphPattern(URI uri) {
        return log(new MorphPatternImpl(uri,model), uri);
    }

    public MorphTransform makeMorphTransform() {
        return new MorphTransformImpl(bn(),model);
    }

    public MorphTransform makeMorphTransform(URI uri) {
        return log(new MorphTransformImpl(uri,model), uri);
    }

    public Prototype makePrototype() {
        return new PrototypeImpl(bn(),model);
    }

    public Prototype makePrototype(URI uri) {
        return log(new PrototypeImpl(uri,model), uri);
    }
//	public Lexicon makeLexicon() { return new LexiconImpl(bn()); }
//	public Lexicon makeLexicon(URI uri) { return log(new LexiconImpl(uri),uri); }

    public Node makeNode() {
        return new NodeImpl(bn(),model);
    }

    public Node makeNode(URI uri) {
        return log(new NodeImpl(uri,model), uri);
    }

    public LexicalSense makeSense() {
        return new LexicalSenseImpl(bn(),model);
    }

    public LexicalSense makeSense(URI uri) {
        return log(new LexicalSenseImpl(uri,model), uri);
    }

    public <C extends LemonElement> C make(Class<C> lemonInterface, URI rdfClass) {
        String iName = lemonInterface.getSimpleName();
        try {
            Class c = Class.forName("eu.monnetproject.lemon.impl.simple." + iName + "Impl");
            Constructor cons = c.getConstructor(String.class);
            return (C) cons.newInstance(bn());
        } catch (Exception x) {
            throw new RuntimeException("Could not create lemon object instance", x);
        }
    }

    public <C extends LemonElement> C make(Class<C> lemonInterface, URI rdfClass, URI uri) {
        String iName = lemonInterface.getSimpleName();
        try {
            Class c = Class.forName("eu.monnetproject.lemon.impl.simple." + iName + "Impl");
            Constructor cons = c.getConstructor(URI.class);
            return (C) cons.newInstance(uri);
        } catch (Exception x) {
            throw new RuntimeException("Could not create lemon object instance", x);
        }
    }
}
