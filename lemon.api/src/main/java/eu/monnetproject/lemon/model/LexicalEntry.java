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
package eu.monnetproject.lemon.model;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * The lexical entry is the main representation of a term or word
 * @author John McCrae
 */
public interface LexicalEntry extends SyntacticRoleMarker {
	/** Get the canonical form of the entry */
	LexicalForm getCanonicalForm();
	/** Set the canonical form of the entry */
	void setCanonicalForm(final LexicalForm canonicalForm);
	/** Get the other forms of the entry */
	Collection<LexicalForm> getOtherForms();
	/** Add an other form to the entry */
	boolean addOtherForm(final LexicalForm otherForm);
	/** Remove an other form from the entry */
	boolean removeOtherForm(final LexicalForm otherForm);
	/** Get the abstract forms of the entry */
	Collection<LexicalForm> getAbstractForms();
	/** Add an abstract form to the entry */
	boolean addAbstractForm(final LexicalForm abstractForm);
	/** Remove an abstract form from the entry */
	boolean removeAbstractForm(final LexicalForm abstractForm);
	/** Get the topics of the entry */
	Collection<LexicalTopic> getTopics();
	/** Add a topic to the entry */
	boolean addTopic(final LexicalTopic topic);
	/** Remove a topic from the entry */
	boolean removeTopic(final LexicalTopic topic);
	/** Get the lexical variants of the entry */
	Map<LexicalVariant,Collection<LexicalEntry>> getLexicalVariants();
	/** Get the set entries that are a given variant of the entry */
	Collection<LexicalEntry> getLexicalVariant(final LexicalVariant lexicalVariant);
	/** Add a lexical variant of the entry */
	boolean addLexicalVariant(final LexicalVariant lexicalVariant, final LexicalEntry lexicalVariantVal);
	/** Remove a lexical variant of the entry */
	boolean removeLexicalVariant(final LexicalVariant lexicalVariant, final LexicalEntry lexicalVariantVal);
	/** Get the syntactic behaviors of the entry */
	Collection<Frame> getSynBehaviors();
	/** Add a syntactic behavior to the entry */
	boolean addSynBehavior(final Frame synBehavior);
	/** Remove a syntactic behavior from the entry */
	boolean removeSynBehavior(final Frame synBehavior);
	/** Get the decompositions of this entry */
	Collection<List<Component>> getDecompositions();
	/** Add a decomposition */
	void addDecomposition(List<Component> comps);
	/** Remove a decomposition */
	boolean removeDecomposition(List<Component> comps);
	/** Get the senses of the entry */
	Collection<LexicalSense> getSenses();
	/** Add a sense to the entry */
	boolean addSense(final LexicalSense sense);
	/** Remove a sense to the entry */
	boolean removeSense(final LexicalSense sense);
	/** Get the phrase structure trees of the entry */
	Collection<Node> getPhraseRoots();
	/** Add a phrase structure tree to the entry */
	boolean addPhraseRoot(final Node phraseRoot);
	/** Remove a phrase structure tree from the entry */
	boolean removePhraseRoot(final Node phraseRoot);
	/** Get all lexical forms of the entry */
	Collection<LexicalForm> getForms();
	/** Add a lexical form to the entry */
	boolean addForm(final LexicalForm form);
	/** Remove a lexical form from the entry */
	boolean removeForm(final LexicalForm form);
        /** Get all morphological patterns */
        Collection<MorphPattern> getPatterns();
        /** Add a morphological pattern */
        boolean addPattern(MorphPattern pattern);
        /** Remove a morphological pattern */
        boolean removePattern(MorphPattern pattern);
        /** Get the head component */
        Component getHead();
        /** Set the head component */
        void setHead(Component component);
}
