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

import java.net.URI;
import java.util.Collection;
import java.util.Map;

/** 
 * A lexical sense indicates a meaning of an entry by mapping it to an ontology entity
 * @author John McCrae
 */
public interface LexicalSense extends LemonElement {
	enum ReferencePreference {
		prefRef,
		altRef,
		hiddenRef
	}	
	/** Get the URI of the referenced ontology entity */
	URI getReference();
	/** Set the URI of the referenced ontology entity */
	void setReference(final URI reference);
	/** Get the preference of the ontology entity for this sense's entry or null for no preference */
	ReferencePreference getRefPref();
	/** Set the preference of the ontology entity for this sense's entry or null for no preference */ 
	void setRefPref(final ReferencePreference refPref);
	/** Add a context to the sense */
	boolean addContext(final SenseContext context);
	/** Remove a context from the sense */
	boolean removeContext(final SenseContext context);
	/** Get the contexts of the lexical sense */
	Collection<SenseContext> getContexts();
	/** Get the conditions on this sense */
	Map<Condition,Collection<SenseCondition>> getConditions();
	/** Get the set of conditions for a particular predicate on this sense */
	Collection<SenseCondition> getCondition(Condition predicate);
	/** Add a condition to this sense */
	boolean addCondition(final Condition predicate,final SenseCondition condition);
	/** Remove a condition from this sense */
	boolean removeCondition(final Condition predicate,final SenseCondition condition);
	/** Get examples of this sense */
	Collection<Example> getExamples();
	/** Add an example to this sense */
	boolean addExample(final Example example);
	/** Remove an example from this sense */
	boolean removeExample(final Example example);
	/** Get the definitions of this sense */
	Map<Definition,Collection<SenseDefinition>> getDefinitions();
	/** Get the definitions of this sense for a particular predicate */
	Collection<SenseDefinition> getDefinition(Definition predicate);
	/** Add a definition to this sense */
	boolean addDefinition(final Definition predicate,final SenseDefinition definition);
	/** Remove a definition from this sense */
	boolean removeDefinition(final Definition predicate,final SenseDefinition definition);
	/** Get the arguments that are the subject of the ontology predicate */
	Collection<Argument> getSubjOfProps();
	/** Add an argument as the subject of the ontology predicate */
	boolean addSubjOfProp(final Argument argument);
	/** Remove an argument as the subject of the ontology predicate */
	boolean removeSubjOfProp(final Argument argument);
	/** Get the arguments that are the object of the ontology predicate */
	Collection<Argument> getObjOfProps();
	/** Add an argument as the subject of the onotology predicate */
	boolean addObjOfProp(final Argument argument);
	/** Remove an argument as the subject of the ontology predicate */
	boolean removeObjOfProp(final Argument argument);
	/** Get the arguments that are members of the ontology predicate */
	Collection<Argument> getIsAs();
	/** Add an argument that is a member of the ontology predicate */
	boolean addIsA(final Argument argument);
	/** Remove an argument this is a member of the ontology predicate */
	boolean removeIsA(final Argument argument);
	/** Get the atomic senses that compose this sense, if any */
	Collection<LexicalSense> getSubsenses();
	/** Add an atomic sense to the composition of this sense */
	boolean addSubsense(final LexicalSense sense);
	/** Remove all atomic senses from the composition of this sense */
	boolean removeSubsense(final LexicalSense sense);
	/** Get the relations to other sense */
	Map<SenseRelation,Collection<LexicalSense>> getSenseRelations();
	/** Get the set of sense related by a particular predicate */
	Collection<LexicalSense> getSenseRelation(final SenseRelation senseRelation);
	/** Add a related sense */
	boolean addSenseRelation(final SenseRelation senseRelation, final LexicalSense senseRelationVal);
	/** Remove a related sense */
	boolean removeSenseRelation(final SenseRelation senseRelation, final LexicalSense senseRelationVal);
        /** Get the lexical entry this sense is attached to */
        LexicalEntry getIsSenseOf();
        /** Set the lexical entry this sense is attached to (avoid calling) */
        void setIsSenseOf(LexicalEntry entry);
}
