/****************************************************************************
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
 ********************************************************************************/
package eu.monnetproject.lemon.impl.io;

import eu.monnetproject.lemon.impl.AccepterFactory;
import eu.monnetproject.lemon.LinguisticOntology;
import java.net.URI;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 *
 * @author John McCrae
 */
public class UnactualizedAccepter implements ReaderAccepter {

    private LinkedList<URIURI> uriuris = new LinkedList<URIURI>();
    private LinkedList<URIString> uRIStrings = new LinkedList<URIString>();
    private LinkedList<URIStrStr> uRIStrStrs = new LinkedList<URIStrStr>();

    @Override
    public ReaderAccepter accept(URI pred, URI value, LinguisticOntology lingOnto, AccepterFactory factory) {
        uriuris.add(new URIURI(pred, value));
        return new UnactualizedAccepter();
    }

    @Override
    public ReaderAccepter accept(URI pred, String bNode, LinguisticOntology lingOnto, AccepterFactory factory) {
        uRIStrings.add(new URIString(pred, bNode));
        return new UnactualizedAccepter();
    }

    @Override
    public void accept(URI pred, String value, String lang, LinguisticOntology lingOnto, AccepterFactory factory) {
        uRIStrStrs.add(new URIStrStr(pred, value, lang));
    }
    
    public void addAll(UnactualizedAccepter accepter) {
        this.uriuris.addAll(accepter.uriuris);
        this.uRIStrings.addAll(accepter.uRIStrings);
        this.uRIStrStrs.addAll(this.uRIStrStrs);
    }
    
    public Map<Object,ReaderAccepter> actualizedAs(ReaderAccepter actual, LinguisticOntology lingOnto, AccepterFactory factory) {
        HashMap<Object,ReaderAccepter> rval = new HashMap<Object, ReaderAccepter>();
        for(URIURI uriuri : uriuris) {
            final ReaderAccepter accept = actual.accept(uriuri.getPred(), uriuri.getValue(), lingOnto,factory);
            if(accept != null) {
                rval.put(uriuri.getValue(), accept);
            }
        }
        for(URIString uriString : uRIStrings) {
            final ReaderAccepter accept = actual.accept(uriString.getPred(), uriString.getVal(), lingOnto,factory);
            if(accept != null) {
                rval.put(uriString.getVal(), accept);
            }
        }
        for(URIStrStr uRIStrStr : uRIStrStrs) {
            actual.accept(uRIStrStr.getUri(), uRIStrStr.getVal(), uRIStrStr.getLang(), lingOnto,factory);
        }
        return rval;
    }

    @Override
    public void merge(ReaderAccepter accepter, LinguisticOntology lingOnto, AccepterFactory factory) {
        if(!(accepter instanceof UnactualizedAccepter)) {
            throw new RuntimeException("Merging actualized accepter into non-actualized accepter!");
        } else {
            final UnactualizedAccepter unact = (UnactualizedAccepter)accepter;
            uRIStrStrs.addAll(unact.uRIStrStrs);
            uRIStrings.addAll(unact.uRIStrings);
            uriuris.addAll(unact.uriuris);
        }
    }
    
    

    private static class URIURI {

        private final URI pred;
        private final URI value;

        public URIURI(URI pred, URI value) {
            this.pred = pred;
            this.value = value;
        }

        public URI getPred() {
            return pred;
        }

        public URI getValue() {
            return value;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final URIURI other = (URIURI) obj;
            if (this.pred != other.pred && (this.pred == null || !this.pred.equals(other.pred))) {
                return false;
            }
            if (this.value != other.value && (this.value == null || !this.value.equals(other.value))) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hash = 3;
            hash = 29 * hash + (this.pred != null ? this.pred.hashCode() : 0);
            hash = 29 * hash + (this.value != null ? this.value.hashCode() : 0);
            return hash;
        }
    }

    private final static class URIString {

        private final URI pred;
        private final String val;

        public URIString(URI pred, String val) {
            this.pred = pred;
            this.val = val;
        }

        public URI getPred() {
            return pred;
        }

        public String getVal() {
            return val;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final URIString other = (URIString) obj;
            if (this.pred != other.pred && (this.pred == null || !this.pred.equals(other.pred))) {
                return false;
            }
            if ((this.val == null) ? (other.val != null) : !this.val.equals(other.val)) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hash = 5;
            hash = 97 * hash + (this.pred != null ? this.pred.hashCode() : 0);
            hash = 97 * hash + (this.val != null ? this.val.hashCode() : 0);
            return hash;
        }
    }

    private static class URIStrStr {

        private final URI uri;
        private final String val;
        private final String lang;

        public URIStrStr(URI uri, String val, String lang) {
            this.uri = uri;
            this.val = val;
            this.lang = lang;
        }

        public String getLang() {
            return lang;
        }

        public URI getUri() {
            return uri;
        }

        public String getVal() {
            return val;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final URIStrStr other = (URIStrStr) obj;
            if (this.uri != other.uri && (this.uri == null || !this.uri.equals(other.uri))) {
                return false;
            }
            if ((this.val == null) ? (other.val != null) : !this.val.equals(other.val)) {
                return false;
            }
            if ((this.lang == null) ? (other.lang != null) : !this.lang.equals(other.lang)) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 31 * hash + (this.uri != null ? this.uri.hashCode() : 0);
            hash = 31 * hash + (this.val != null ? this.val.hashCode() : 0);
            hash = 31 * hash + (this.lang != null ? this.lang.hashCode() : 0);
            return hash;
        }
    }
}
