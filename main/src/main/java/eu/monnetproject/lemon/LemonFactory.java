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

import eu.monnetproject.lemon.model.SenseDefinition;
import eu.monnetproject.lemon.model.Node;
import eu.monnetproject.lemon.model.LemonElement;
import eu.monnetproject.lemon.model.SenseCondition;
import eu.monnetproject.lemon.model.LexicalForm;
import eu.monnetproject.lemon.model.Argument;
import eu.monnetproject.lemon.model.LexicalEntry;
import eu.monnetproject.lemon.model.Example;
import eu.monnetproject.lemon.model.LexicalSense;
import eu.monnetproject.lemon.model.Frame;
import eu.monnetproject.lemon.model.Component;
import eu.monnetproject.lemon.model.MorphPattern;
import java.net.URI;
import eu.monnetproject.lemon.model.MorphTransform;
import eu.monnetproject.lemon.model.Part;
import eu.monnetproject.lemon.model.Phrase;
import eu.monnetproject.lemon.model.Prototype;
import eu.monnetproject.lemon.model.Word;

/**
 * The factory for making new lemon elements
 *
 * @author John McCrae
 */
public interface LemonFactory {

    /**
     * Create a new blank node argument
     * @deprecated It is strongly advised that all elements in the lexicon have a name (URI).
     */
    @Deprecated
    Argument makeArgument();

    /**
     * Create a new named argument
     */
    Argument makeArgument(URI uri);

    /**
     * Create a new blank node component
     * @deprecated It is strongly advised that all elements in the lexicon have a name (URI).
     */
    @Deprecated
    Component makeComponent();

    /**
     * Create a new named component
     */
    Component makeComponent(URI uri);

    /**
     * Create a new blank node condition
     * @deprecated It is strongly advised that all elements in the lexicon have a name (URI).
     */
    @Deprecated
    SenseCondition makeCondition();

    /**
     * Create a new named condition
     */
    SenseCondition makeCondition(URI uri);

    /**
     * Create a new blank node definition
     * @deprecated It is strongly advised that all elements in the lexicon have a name (URI).
     */
    @Deprecated
    SenseDefinition makeDefinition();

    /**
     * Create a new named definition
     */
    SenseDefinition makeDefinition(URI uri);

    /**
     * Create a new blank node example
     * @deprecated It is strongly advised that all elements in the lexicon have a name (URI).
     */
    @Deprecated
    Example makeExample();

    /**
     * Create a new named example
     */
    Example makeExample(URI uri);

    /**
     * Create a new blank node form
     * @deprecated It is strongly advised that all elements in the lexicon have a name (URI).
     */
    @Deprecated
    LexicalForm makeForm();

    /**
     * Create a new named form
     */
    LexicalForm makeForm(URI uri);

    /**
     * Create a new blank node frame
     * @deprecated It is strongly advised that all elements in the lexicon have a name (URI).
     */
    @Deprecated
    Frame makeFrame();

    /**
     * Create a new named frame
     */
    Frame makeFrame(URI uri);

    /**
     * Create a new lexical entry
     */
    LexicalEntry makeLexicalEntry(URI uri);

    /**
     * Create a new part (lexical entry)
     */
    Part makePart(URI uri);

    /**
     * Create a new phrase (lexical entry)
     */
    Phrase makePhrase(URI uri);

    /**
     * Create a new word (lexical entry)
     */
    Word makeWord(URI uri);

    /**
     * Create a morphological pattern
     */
    MorphPattern makeMorphPattern(URI uri);

    /**
     * Create a morphological pattern
     * @deprecated It is strongly advised that all elements in the lexicon have a name (URI).
     */
    @Deprecated
    MorphPattern makeMorphPattern();

    /**
     * Create a morphological transform
     */
    MorphTransform makeMorphTransform(URI uri);

    /**
     * Create a morphological transform
     * @deprecated It is strongly advised that all elements in the lexicon have a name (URI).
     */
    @Deprecated
    MorphTransform makeMorphTransform();

    /**
     * Create a new blank node entry
     * @deprecated It is strongly advised that all elements in the lexicon have a name (URI).
     */
    @Deprecated
    Node makeNode();

    /**
     * Create a new named node
     */
    Node makeNode(URI uri);

    /**
     * Create a prototype
     */
    Prototype makePrototype(URI uri);

    /**
     * Create a prototype
     * @deprecated It is strongly advised that all elements in the lexicon have a name (URI).
     */
    @Deprecated
    Prototype makePrototype();

    /**
     * Create a new blank node sense
     * @deprecated It is strongly advised that all elements in the lexicon have a name (URI).
     */
    @Deprecated
    LexicalSense makeSense();

    /**
     * Create a new named sense
     */
    LexicalSense makeSense(URI uri);

    /**
     * Make a generic element of the lemon model
     * @deprecated It is strongly advised that all elements in the lexicon have a name (URI).
     */
    @Deprecated
    <C extends LemonElement> C make(Class<C> lemonInterface, URI rdfClass);

    /**
     * Make a generic named element of the lemon model
     */
    <C extends LemonElement> C make(Class<C> lemonInterface, URI rdfClass, URI uri);
    
    /**
     * Returns true if the URI is already used by an existing entity
     * @param uri The URI
     * @return {@code true} if an element with this URI exists
     */
    boolean isURIUsed(URI uri);
}
