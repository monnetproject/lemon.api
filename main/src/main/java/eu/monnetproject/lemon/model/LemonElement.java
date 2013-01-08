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
 * The super-class for all lemon elements (individuals)
 * @author John McCrae
 */
public interface LemonElement extends LemonElementOrPredicate {
	/** Get any properties of this element */
	Map<Property,Collection<PropertyValue>> getPropertys();
	/** Get the set of values for a property */
	Collection<PropertyValue> getProperty(final Property property);
	/** Add a property to this element */
	boolean addProperty(final Property property, final PropertyValue propertyVal);
	/** Remove a property from this element */
	boolean removeProperty(final Property property, final PropertyValue propertyVal);
	/**
	 * Get the blank node ID for this node or <code>null</code> if this is a named node
	 */
	String getID();
	/** Get any types of the element */ 
	Collection<URI> getTypes();
	/** Add a type to this element */
	void addType(URI uri);
	/** Remove a type from this element */
	void removeType(URI uri);
        /** 
         * Get any annotations for this element 
         * @return A map where the value is a collection of either java.net.URI, java.lang.String (blank nodes) or eu.monnetproject.lemon.model.Text
         */
        Map<URI,Collection<Object>> getAnnotations();
        /** 
         * Get the set of annotations for this property 
         * @return A collection of either java.net.URI, java.lang.String (blank nodes) or eu.monnetproject.lemon.model.Text
         */
        Collection<Object> getAnnotations(final URI annotation);
        /** 
         * Add an annotation to this element 
         * @param annotationProperty The annotation property
         * @param annotation Either a java.net.URI, java.lang.String (blank nodes) or eu.monnetproject.lemon.model.Text
         * @return true if the element changed
         * @throws IllegalArgumentException If the annotation is not of the appropriate type
         */
        boolean addAnnotation(final URI annotationProperty, Object annotation);
        /**  
         * Remove an annotation from this element 
         * @param annotationProperty The annotation property
         * @param annotation Either a java.net.URI, java.lang.String (blank nodes) or eu.monnetproject.lemon.model.Text
         * @return true if the element changed
         * @throws IllegalArgumentException If the annotation is not of the appropriate type
         */
        boolean removeAnnotation(final URI annotationProperty, Object annotation);
}
