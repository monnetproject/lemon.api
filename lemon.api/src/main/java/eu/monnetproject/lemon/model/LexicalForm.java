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
import java.util.Map;

/**
 * A form of a lexical entry
 * @author John McCrae
 */
public interface LexicalForm extends LemonElement {
	/** Get the primary written representation of the form */
	Text getWrittenRep();
	/** Set the primary written representation of the form */
	void setWrittenRep(final Text writtenRep);
	/** Get the alternative representations of the form */
	Map<Representation,Collection<Text>> getRepresentations();
	/** Get a particular set of representations of the form */
	Collection<Text> getRepresentation(final Representation representation);
	/** Add an alternative representation of the form */
	boolean addRepresentation(final Representation representation, final Text representationVal);
	/** Remove an alternative representation of the form */
	boolean removeRepresentation(final Representation representation, final Text representationVal);
	/** Get any variants of this form */
	Map<FormVariant,Collection<LexicalForm>> getFormVariants();
	/** Get a particular variant set of this form */
	Collection<LexicalForm> getFormVariant(final FormVariant formVariant);
	/** Add a variant of this form */
	boolean addFormVariant(final FormVariant formVariant, final LexicalForm formVariantVal);
	/** Remove a variant of this form */
	boolean removeFormVariant(final FormVariant formVariant, final LexicalForm formVariantVal);
}
