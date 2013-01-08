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
import eu.monnetproject.lemon.LemonModel;
import java.util.Collection;

/**
 * A mono-lingual lexicon object composed of a set of lexical entries
 * @author John McCrae
 */
public interface Lexicon extends LemonElement {
	/** Get the language of the lexicon */
	String getLanguage();
	/** Set the language of the lexicon */
	void setLanguage(final String language);
	/** Get the entries that compose this lexicon */
	Collection<LexicalEntry> getEntrys();
	/** Add an entry to the lexicon */
	boolean addEntry(final LexicalEntry entry);
	/** Remove an entry from the lexicon */
	boolean removeEntry(final LexicalEntry entry);
	/** Check if the lexicon contains an entry. Significantly faster than call getEntrys().contains(entry) */
	boolean hasEntry(final LexicalEntry entry);
        /** Get the number of entries. Also significantly faster */
        int countEntrys();
	/** Get the topics of the lexicon */
	Collection<LexicalTopic> getTopics();
	/** Add a topic to the lexicon */
	boolean addTopic(final LexicalTopic topic);
	/** Remove a topic from the lexicon */
	boolean removeTopic(final LexicalTopic topic);
	/** Get the model this lexicon is contained in */
	LemonModel getModel();
        /** Get all patterns used by this lexicon */
        Collection<MorphPattern> getPatterns();
        /** Add a pattern to this lexicon */
        boolean addPattern(MorphPattern pattern);
        /** Remove a pattern from this lexicon */
        boolean removePattern(MorphPattern pattern);
}
