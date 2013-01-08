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
package eu.monnetproject.lemon.impl;

import eu.monnetproject.lemon.ElementVisitor;
import eu.monnetproject.lemon.LemonModel;
import eu.monnetproject.lemon.LinguisticOntology;
import eu.monnetproject.lemon.impl.io.ListAccepter;
import eu.monnetproject.lemon.impl.io.ReaderAccepter;
import eu.monnetproject.lemon.model.Argument;
import eu.monnetproject.lemon.model.Component;
import eu.monnetproject.lemon.model.Frame;
import eu.monnetproject.lemon.model.Node;
import eu.monnetproject.lemon.model.SynArg;
import java.net.URI;
import java.util.*;

/**
 * Instantiated via {@link LemonFactoryImpl}
 * @author John McCrae
 */
@SuppressWarnings("unchecked")
public class FrameImpl extends LemonElementImpl implements Frame {
    private static final long serialVersionUID = -2731269230726069536L;

    private final HashSet<List<Component>> components = new HashSet<List<Component>>();

    FrameImpl(URI uri, LemonModelImpl model) {
        super(uri, "Frame",model);
    }

    FrameImpl(String id, LemonModelImpl model) {
        super(id, "Frame",model);
    }

    @Override
    public Map<SynArg, Collection<Argument>> getSynArgs() {
        return (Map<SynArg, Collection<Argument>>) getPredElems(SynArg.class);
    }

    @Override
    public Collection<Argument> getSynArg(final SynArg synArg) {
        return (Collection<Argument>) getPredElem(synArg);
    }

    @Override
    public boolean addSynArg(final SynArg synArg, final Argument synArgVal) {
        return addPredElem(synArg, synArgVal);
    }

    @Override
    public boolean removeSynArg(final SynArg synArg, final Argument synArgVal) {
        return removePredElem(synArg, synArgVal);
    }

    @Override
    public Collection<Node> getTrees() {
        return (Collection<Node>) getStrElems("tree");
    }

    @Override
    public boolean addTree(final Node node) {
        return addStrElem("tree", node);
    }

    @Override
    public boolean removeTree(final Node node) {
        return removeStrElem("tree", node);
    }

    boolean resolveRemoteList = model.allowRemote();
    
    @Override
    public Collection<List<Component>> getDecompositions() {
        if(resolveRemoteList) {
            if(checkRemote) {
                resolveRemote();
            }
            final ArrayList<List<Component>> compCopy = new ArrayList<List<Component>>(components);
            for(List<Component> comps : compCopy) {
                if(comps instanceof ListAccepter) {
                    final List<Component> list = model.resolver().resolveRemoteList(((ListAccepter)comps).head(),Component.class,model);
                    if(list != null) {
                        components.remove(comps);
                        components.add(list);
                    } 
                } 
            }
            resolveRemoteList = false;
        }
        return Collections.unmodifiableSet(components);
    }

    @Override
    public void addDecomposition(List<Component> comps) {
        checkRemote = false;
        for (Component comp : comps) {
            if (comp instanceof LemonElementImpl) {
                ((LemonElementImpl) comp).addReference(this);
            }
        }
        if(model.allowUpdate()) {
            List<Object> compIds = new ArrayList<Object>(comps.size());
            for(Component comp : comps) {
                compIds.add(comp.getURI() == null ? comp.getID() : comp.getURI());
            }
            if(getURI() != null) {
                model.updater().addList(getURI(), URI.create(LemonModel.LEMON_URI+"decompositon"), compIds);
            } else {
                model.updater().addList(getID(), URI.create(LemonModel.LEMON_URI+"decompositon"), compIds);
            }
        }
        components.add(comps);
    }

    @Override
    public boolean removeDecomposition(List<Component> comps) {
        checkRemote = false;
        if(model.allowUpdate()) {
            List<Object> compIds = new ArrayList<Object>(comps.size());
            for(Component comp : comps) {
                compIds.add(comp.getURI() == null ? comp.getID() : comp.getURI());
            }
            if(getURI() != null) {
                model.updater().removeList(getURI(), URI.create(LemonModel.LEMON_URI+"decompositon"), compIds);
            } else {
                model.updater().removeList(getID(), URI.create(LemonModel.LEMON_URI+"decompositon"), compIds);
            }
        }
        for (Component comp : comps) {
            if (comp instanceof LemonElementImpl) {
                ((LemonElementImpl) comp).removeReference(this);
            }
        }
        return components.remove(comps);
    }

    @Override
    public void doAccept(ElementVisitor visitor) {
        for (List<Component> compList : components) {
            for (Component comp : compList) {
                if (comp instanceof LemonElementImpl) {
                    ((LemonElementImpl) comp).accept(visitor);
                }
            }
        }
    }
    
    
    @Override
    public Map<URI, Collection<Object>> getElements() {
        final Map<URI, Collection<Object>> elements = super.getElements();
        final URI decomposition = URI.create(LemonModel.LEMON_URI+"decomposition");
        final Collection<List<Component>> decompositions = getDecompositions();
        if(!decompositions.isEmpty()) {
            elements.put(decomposition,new LinkedList());
        }
        for(List<Component> compList : decompositions) {
            elements.get(decomposition).add(compList);
        }
        return elements;
    }

    @Override
    public void clearAll() {
        for (List<Component> compList : components) {
            for (Component comp : compList) {
                if (comp instanceof LemonElementImpl) {
                    ((LemonElementImpl) comp).referencers.remove(this);
                }
            }
        }
        components.clear();
        super.clearAll();
    }

    @Override
    public ReaderAccepter accept(URI pred, URI value, LinguisticOntology lingOnto, AccepterFactory factory) {
        if (pred.toString().equals(LemonModel.LEMON_URI + "decomposition")) {
            final ListAccepter listAccepter = new ListAccepter(value);
            addDecomposition((List<Component>) listAccepter);
            return listAccepter;
        } else if (pred.toString().equals(LemonModel.LEMON_URI + "tree")) {
            final NodeImpl nodeImpl = factory.getNodeImpl(value);//new NodeImpl(value,model);
            addStrElemDirect("tree",nodeImpl);
            return nodeImpl;
        } else if (lingOnto != null) {
            for (SynArg synArg : lingOnto.getSynArgs()) {
                if (synArg.getURI().equals(pred)) {
                    final ArgumentImpl argumentImpl = factory.getArgumentImpl(value);
                    addPredElemDirect(synArg, argumentImpl);
                    return argumentImpl;
                }
            }
        }
        return defaultAccept(pred, value, lingOnto);
    }

    @Override
    public ReaderAccepter accept(URI pred, String value, LinguisticOntology lingOnto, AccepterFactory factory) {
        if (pred.toString().equals(LemonModel.LEMON_URI + "decomposition")) {
            final ListAccepter listAccepter = new ListAccepter(value);
            components.add((List<Component>) listAccepter);
            return listAccepter;
        } else if (pred.toString().equals(LemonModel.LEMON_URI + "tree")) {
            final NodeImpl nodeImpl = factory.getNodeImpl(value);
            addStrElemDirect("tree",nodeImpl);
            return nodeImpl;
        } else if (lingOnto != null) {
            for (SynArg synArg : lingOnto.getSynArgs()) {
                if (synArg.getURI().equals(pred)) {
                    final ArgumentImpl argumentImpl = factory.getArgumentImpl(value);
                    addPredElemDirect(synArg, argumentImpl);
                    return argumentImpl;
                }
            }
        }
        return defaultAccept(pred, value);
    }

    @Override
    public void accept(URI pred, String value, String lang, LinguisticOntology lingOnto, AccepterFactory factory) {
        defaultAccept(pred, value, lang);
    }
    
    @Override
    public void merge(ReaderAccepter accepter, LinguisticOntology lingOnto, AccepterFactory factory) {
        if(accepter instanceof FrameImpl) {
            this.components.addAll(((FrameImpl)accepter).components);
        }
        defaultMerge(accepter, lingOnto, factory);
    }
}
