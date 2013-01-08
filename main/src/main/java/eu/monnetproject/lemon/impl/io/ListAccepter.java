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
import eu.monnetproject.lemon.model.Component;
import java.net.URI;
import java.util.AbstractList;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 *
 * @author John McCrae
 */
public class ListAccepter extends AbstractList<Component> implements ReaderAccepter {

    private Component value;
    private ListAccepter next;
    private final Object head;
    
    public ListAccepter(Object head) {
        this.head = head;
    }

    public ListAccepter(Component value, ListAccepter next, Object head) {
        this.value = value;
        this.next = next;
        this.head = head;
    }

    // Identifies the first node of the list
    public Object head() { return head; }
    
    @Override
    public void add(int i, Component e) {
        if (i == 0) {
            this.next = new ListAccepter(value, next,null);
            this.value = e;
        } else {
            if (next != null) {
                next.add(i - 1, e);
            } else {
                throw new IndexOutOfBoundsException();
            }
        }
    }

    @Override
    public Component set(int i, Component e) {
        if (i == 0) {
            Component e2 = value;
            this.value = e;
            return e2;
        } else {
            if (next != null) {
                return next.set(i - 1, e);
            } else {
                throw new IndexOutOfBoundsException();
            }
        }
    }

    @Override
    public Component remove(int i) {
        if (i == 0) {
            // head removal
            Component e = this.value;
            this.value = next.value;
            this.next = next.next;
            return e;
        } else if (i == 1) {
            Component e = next.value;
            next = next.next;
            return e;
        } else {
            if (next != null) {
                return next.remove(i - 1);
            } else {
                throw new IndexOutOfBoundsException();
            }
        }
    }

    @Override
    public Component get(int i) {
        if(i == 0) {
            return value;
        } else {
            if(next != null) {
                return next.get(i-1);
            } else {
                throw new IndexOutOfBoundsException();
            }
        }
    }

    @Override
    public int size() {
        if(next == null) {
            return 0;
        } else {
            return 1+next.size();
        }
    }

    @Override
    public Iterator<Component> iterator() {
        return new ListAccepterIterator(this);
    }
    

    @Override
    public ReaderAccepter accept(URI pred, URI value, LinguisticOntology lingOnto, AccepterFactory factory) {
        if(pred.toString().equals("http://www.w3.org/1999/02/22-rdf-syntax-ns#first")) {
            this.value = factory.getComponentImpl(value);
            return (ReaderAccepter)this.value;
        } else if(pred.toString().equals("http://www.w3.org/1999/02/22-rdf-syntax-ns#rest")) {
            this.next = new ListAccepter(value);
            return this.next;
        } else {
            return null;
        }
    }

    @Override
    public ReaderAccepter accept(URI pred, String bNode, LinguisticOntology lingOnto, AccepterFactory factory) {
        if(pred.toString().equals("http://www.w3.org/1999/02/22-rdf-syntax-ns#first")) {
            this.value = factory.getComponentImpl(bNode);
            return (ReaderAccepter)this.value;
        } else if(pred.toString().equals("http://www.w3.org/1999/02/22-rdf-syntax-ns#rest")) {
            this.next = new ListAccepter(bNode);
            return this.next;
        } else {
            return null;
        }
    }

    @Override
    public void accept(URI pred, String value, String lang, LinguisticOntology lingOnto, AccepterFactory factory) {
        
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ListAccepter other = (ListAccepter) obj;
        if (this.head != other.head && (this.head == null || !this.head.equals(other.head))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + (this.head != null ? this.head.hashCode() : 0);
        return hash;
    }
    
    

    @Override
    public void merge(ReaderAccepter accepter, LinguisticOntology lingOnto, AccepterFactory factory) {
        if(accepter instanceof ListAccepter) {
            final ListAccepter accepter2 = (ListAccepter)accepter;
            if(this.next == null) {
                if(accepter2.next != null) {
                    this.next = accepter2.next;
                }
            } else {
                if(!accepter2.next.equals(this.next)) {
                    throw new RuntimeException("Incompatible merge");
                }
            }
            
            if(this.value == null) {
                if(accepter2.value != null) {
                    this.value = accepter2.value;
                }
            } else {
                if(!accepter2.value.equals(this.value)) {
                    throw new RuntimeException("Incompatible merge");
                }
            }
        } else if(accepter instanceof UnactualizedAccepter) {
            ((UnactualizedAccepter)accepter).actualizedAs(this, lingOnto,factory);
        }
    }
    
    
    
    private static class ListAccepterIterator implements Iterator<Component> {
        ListAccepter node;
        ListAccepter prev;

        public ListAccepterIterator(ListAccepter node) {
            this.node = node;
        }

        public boolean hasNext() {
            return node != null;
        }

        public Component next() {
            if(node != null) {
                prev = node;
                node = node.next;
                return prev.value;
            } else {
                throw new NoSuchElementException();
            }
        }

        public void remove() {
            if(prev == null) {
                throw new IllegalStateException();
            } else {
                prev.value = node.value;
                prev.next = node.next;
                node = prev;
                prev = null;
            }
        }
    }
}
