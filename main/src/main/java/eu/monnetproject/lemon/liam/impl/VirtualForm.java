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
package eu.monnetproject.lemon.liam.impl;

import eu.monnetproject.lemon.model.FormVariant;
import eu.monnetproject.lemon.model.LexicalForm;
import eu.monnetproject.lemon.model.Property;
import eu.monnetproject.lemon.model.PropertyValue;
import eu.monnetproject.lemon.model.Representation;
import eu.monnetproject.lemon.model.Text;
import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 *
 * @author John McCrae
 */
public class VirtualForm implements LexicalForm {
    private Text writtenRep;
    
    public Text getWrittenRep() {
        return writtenRep;
    }

    public void setWrittenRep(Text text) {
        this.writtenRep = text;
    }

    public Map<Representation, Collection<Text>> getRepresentations() {
        return Collections.EMPTY_MAP;
    }

    public Collection<Text> getRepresentation(Representation r) {
        return Collections.EMPTY_LIST;
    }

    public boolean addRepresentation(Representation r, Text text) {
        throw new UnsupportedOperationException("Cannot modify virtual element.");
    }

    public boolean removeRepresentation(Representation r, Text text) {
        throw new UnsupportedOperationException("Cannot modify virtual element.");
    }

    public Map<FormVariant, Collection<LexicalForm>> getFormVariants() {
        return Collections.EMPTY_MAP;
    }

    public Collection<LexicalForm> getFormVariant(FormVariant fv) {
        return Collections.EMPTY_LIST;
    }

    public boolean addFormVariant(FormVariant fv, LexicalForm lf) {
        throw new UnsupportedOperationException("Cannot modify virtual element.");
    }

    public boolean removeFormVariant(FormVariant fv, LexicalForm lf) {
        throw new UnsupportedOperationException("Cannot modify virtual element.");
    }

    private final Map<Property,Collection<PropertyValue>> props = new HashMap<Property, Collection<PropertyValue>>();
    
    public Map<Property, Collection<PropertyValue>> getPropertys() {
        return props;
    }

    public Collection<PropertyValue> getProperty(Property prprt) {
        return props.get(prprt);
    }

    public boolean addProperty(Property prprt, PropertyValue pv) {
        if(!props.containsKey(prprt)) {
            props.put(prprt, new LinkedList<PropertyValue>());
        }
        return props.get(prprt).add(pv);
    }

    public boolean removeProperty(Property prprt, PropertyValue pv) {
        throw new UnsupportedOperationException("Cannot modify virtual element.");
    }

    public String getID() {
        throw new UnsupportedOperationException("Virtual element has no underlying RDF element.");
    }

    public Collection<URI> getTypes() {
        throw new UnsupportedOperationException("Cannot modify virtual element.");
    }

    public void addType(URI uri) {
        throw new UnsupportedOperationException("Cannot modify virtual element.");
    }

    public void removeType(URI uri) {
        throw new UnsupportedOperationException("Cannot modify virtual element.");
    }

    public URI getURI() {
        throw new UnsupportedOperationException("Virtual element has no underlying RDF element.");
    }

    public Map<URI, Collection<Object>> getAnnotations() {
        return Collections.EMPTY_MAP;
    }

    public Collection<Object> getAnnotations(URI uri) {
        return Collections.EMPTY_LIST;
    }

    public boolean addAnnotation(URI uri, Object o) {
        throw new UnsupportedOperationException("Cannot modify virtual element.");
    }

    public boolean removeAnnotation(URI uri, Object o) {
        throw new UnsupportedOperationException("Cannot modify virtual element.");
    }
    
}
