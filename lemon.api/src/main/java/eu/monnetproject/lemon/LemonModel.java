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
package eu.monnetproject.lemon;

import eu.monnetproject.lemon.model.LemonElement;
import eu.monnetproject.lemon.model.LemonElementOrPredicate;
import eu.monnetproject.lemon.model.Lexicon;
import eu.monnetproject.lemon.model.MorphPattern;
import java.io.Reader;
import java.net.URI;
import java.util.Collection;
import java.util.Iterator;

/**
 * Interface that contains a set of lexica and manages the URIs for all elements of the model.
 * The seperation of URIs and the structure of the lemon model is intended to allow for models
 * to be created quickly as individual elements frequently have stereotypical URIs.
 * @author John McCrae 
 */
public interface LemonModel {
	
	/** The URI for lemon */
	static final String LEMON_URI = "http://www.monnet-project.eu/lemon#";
	/**
	 * Get the set of lexica contained by this model
	 * @return The set of lexica
	 */
	Collection<Lexicon> getLexica();
	
	/**
	 * Get the base URI of the model. If this is not clear return the prefix of the first lexicon object
	 * @return The base URI, or null if the model has no context
	 */
	URI getContext();
	
	/**
	 * Get the factory for making new elements
	 */
	LemonFactory getFactory();
        	
	/** 
	 * Add a (new blank) lexicon
	 */
	Lexicon addLexicon(URI uri, String language);
	
	/**
	 * Remove a lexicon from the repository. Note this does not remove any entries or
	 * other information contained in the lexicon, this must be done manually
	 */
	void removeLexicon(Lexicon lexicon);
        
        /**
         * Remove a lexicon and all entries
         * @param lexicon The lexicon
         * @param lingOnto The linguistic ontology used for links in the lexicon. If this
         * is incorrect it may result in links being followed that are not in the model
         * resulting in to many entries being deleted
         */
        void purgeLexicon(Lexicon lexicon, LinguisticOntology lingOnto);
	
        /**
         * Import a lexicon from another model
         * @param lexicon The lexicon
         * @param lingOnto The linguistic ontology used for links in the lexicon. If this
         * is incorrect it may result in links being followed that are not in the model
         * resulting in to many entries being imported
         */
        void importLexicon(Lexicon lexicon, LinguisticOntology lingOnto);
        
	/**
	 * Return all elements in the lemon model matching some certain SPARQL query
	 * @param target The class of the target
	 * @param sparqlQuery The query in sparql, must be a select query returning a single variable
	 * @return A Collection of elements matching the given query
	 */
	<Elem extends LemonElementOrPredicate> Iterator<Elem> query(Class<Elem> target, String sparqlQuery);
	
	/**
	 * Merge two elements 
	 * @param from The element to be removed
	 * @param to The element to be preserved
	 */
	<Elem extends LemonElement> void merge(Elem from, Elem to); 
        
        /**
         * Add a pattern to the model
         * @param pattern The pattern
         */
        void addPattern(MorphPattern pattern);
        
        /**
         * Get all patterns now in the model
         * @return The set of patterns
         */
        Collection<MorphPattern> getPatterns();
}
