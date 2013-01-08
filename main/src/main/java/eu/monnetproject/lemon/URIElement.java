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

import eu.monnetproject.lemon.model.PropertyValue;
import eu.monnetproject.lemon.model.LemonElement;
import eu.monnetproject.lemon.model.Property;
import java.util.*;
import java.net.URI;

/**
 * Super-class for creating lemon elements. This is extended in the context of linguistic ontologies
 */
public abstract class URIElement extends URIValue implements LemonElement {

    private final String id;

    protected URIElement(URI uri) {
        super(uri);
        this.id = null;
    }

    protected URIElement(String id) {
        super(null);
        this.id = id;
    }

    @Override
    public Map<Property, Collection<PropertyValue>> getPropertys() {
        return Collections.EMPTY_MAP;
    }

    @Override
    public Collection<PropertyValue> getProperty(Property prop) {
        return null;
    }

    @Override
    public boolean addProperty(Property prop, PropertyValue propVal) {
        throw new UnsupportedOperationException("Wrapped URI cannot have properties");
    }

    @Override
    public boolean removeProperty(Property prop, PropertyValue propVal) {
        throw new UnsupportedOperationException("Wrapped URI cannot have properties");
    }

    @Override
    public String getID() {
        return id;
    }

    @Override
    public Collection<URI> getTypes() {
        LinkedList<URI> types = new LinkedList<URI>();
        for (Class c : this.getClass().getInterfaces()) {
            if (c.getName().matches("eu\\.monnetproject\\.lemon\\.[a-z]+")) {
                types.add(URI.create("http://www.monnet-project.eu/lemon#" + c.getSimpleName()));
            }
        }
        return types;
    }

    @Override
    public void addType(URI uri) {
        throw new UnsupportedOperationException("Cannot modify class defined by URIElement");
    }

    @Override
    public void removeType(URI uri) {
        throw new UnsupportedOperationException("Cannot modify class defined by URIElement");
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final URIElement other = (URIElement) obj;
        if ((this.id == null) ? (other.id != null) : !this.id.equals(other.id)) {
            return false;
        }
        if (this.getURI() != other.getURI() && (this.getURI() == null || !this.getURI().equals(other.getURI()))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = getInfHash();
        hash = hash + (this.id != null ? this.id.hashCode() : 0);
        hash = hash + (this.getURI() != null ? this.getURI().hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        if (getURI() != null) {
            return getURI().toString();
        } else {
            return "_:" + getID();
        }
    }

    public Map<URI, Collection<Object>> getAnnotations() {
        return Collections.EMPTY_MAP;
    }

    public Collection<Object> getAnnotations(URI annotation) {
        return Collections.EMPTY_LIST;
    }

    public boolean addAnnotation(URI annotationProperty, Object annotation) {
        throw new UnsupportedOperationException("Not supported.");
    }

    public boolean removeAnnotation(URI annotationProperty, Object annotation) {
        throw new UnsupportedOperationException("Not supported.");
    }
}
