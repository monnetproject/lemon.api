/**
 * ********************************************************************************
 * Copyright (c) 2011, Monnet Project All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met: *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. * Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials provided
 * with the distribution. * Neither the name of the Monnet Project nor the names
 * of its contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE MONNET PROJECT BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * *******************************************************************************
 */
package eu.monnetproject.lemon.impl;

import eu.monnetproject.lemon.ElementVisitor;
import eu.monnetproject.lemon.LemonModel;
import eu.monnetproject.lemon.LinguisticOntology;
import eu.monnetproject.lemon.URIElement;
import eu.monnetproject.lemon.impl.io.ReaderAccepter;
import eu.monnetproject.lemon.impl.io.UnactualizedAccepter;
import eu.monnetproject.lemon.model.LemonElement;
import eu.monnetproject.lemon.model.LemonPredicate;
import eu.monnetproject.lemon.model.Property;
import eu.monnetproject.lemon.model.PropertyValue;
import eu.monnetproject.lemon.model.Text;
import java.io.Serializable;
import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Base class for element implementations
 *
 * @author John McCrae
 */
public abstract class LemonElementImpl<Elem extends LemonElement> extends URIElement implements LemonElement, ReaderAccepter, IntrospectableElement, Serializable {

    public static final URI RDF_TYPE = URI.create("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
    private final HashMap<LemonPredicate, Collection<LemonElement>> predElems =
            new HashMap<LemonPredicate, Collection<LemonElement>>();
    private final HashMap<String, Collection<LemonElement>> strElems =
            new HashMap<String, Collection<LemonElement>>();
    private final HashMap<String, LemonElement> strElem =
            new HashMap<String, LemonElement>();
    private final HashMap<String, Text> strText =
            new HashMap<String, Text>();
    private HashSet<URI> types = new HashSet<URI>();
    protected List<LemonElementImpl<?>> referencers = new LinkedList<LemonElementImpl<?>>();
    private final HashMap<URI, Collection<Object>> annotations = new HashMap<URI, Collection<Object>>();
    private final String modelName;
    protected final LemonModelImpl model;
    protected boolean checkRemote;

    protected LemonElementImpl(URI uri, String type, LemonModelImpl model) {
        super(uri);
        types.add(URI.create(LemonModel.LEMON_URI + type));
        modelName = type;
        this.model = model;
        this.checkRemote = model.allowRemote();
    }

    protected LemonElementImpl(String id, String type, LemonModelImpl model) {
        super(id);
        types.add(URI.create(LemonModel.LEMON_URI + type));
        modelName = type;
        this.model = model;
        this.checkRemote = model.allowRemote();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<Property, Collection<PropertyValue>> getPropertys() {
        if (checkRemote) {
            resolveRemote();
        }
        return (Map) getPredElems(Property.class);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<PropertyValue> getProperty(final Property property) {
        if (checkRemote) {
            resolveRemote();
        }
        return (Collection) getPredElem(property);
    }

    @Override
    public boolean addProperty(final Property property, final PropertyValue propertyVal) {
        checkRemote = false;
        return addPredElem(property, propertyVal);
    }

    @Override
    public boolean removeProperty(final Property property, final PropertyValue propertyVal) {
        checkRemote = false;
        return removePredElem(property, propertyVal);
    }

    @Override
    public Collection<URI> getTypes() {
        if (checkRemote) {
            resolveRemote();
        }
        return Collections.unmodifiableSet(types);
    }

    @Override
    public void addType(URI uri) {
        checkRemote = false;
        if (model.allowUpdate()) {
            if (getURI() != null) {
                model.updater().add(getURI(), RDF_TYPE, uri);
            }
        }
        addTypeDirect(uri);

    }

    protected boolean addStrElemDirect(String p, LemonElement e) {
        checkRemote = false;
        if (!strElems.containsKey(p)) {
            strElems.put(p, new HashSet<LemonElement>());
        }
        return strElems.get(p).add(e);
    }

    protected void addTypeDirect(URI uri) {
        checkRemote = false;
        types.add(uri);
    }

    @Override
    public void removeType(URI uri) {
        checkRemote = false;
        types.add(uri);
    }

    @Override
    public Map<URI, Collection<Object>> getAnnotations() {
        if (checkRemote) {
            resolveRemote();
        }
        return annotations;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<Object> getAnnotations(URI uri) {
        if (checkRemote) {
            resolveRemote();
        }
        final Collection<Object> annos = annotations.get(uri);
        return annos == null ? Collections.EMPTY_LIST : annos;
    }

    @Override
    public boolean addAnnotation(URI uri, Object o) {
        checkRemote = false;
        if (o instanceof URI || o instanceof String || o instanceof Text) {
            if (!annotations.containsKey(uri)) {
                annotations.put(uri, new LinkedList<Object>());
            }
            return annotations.get(uri).add(o);
        } else {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public boolean removeAnnotation(URI uri, Object o) {
        checkRemote = false;
        if (o instanceof URI || o instanceof String || o instanceof Text) {
            if (!annotations.containsKey(uri)) {
                annotations.put(uri, new LinkedList<Object>());
            }
            final boolean rval = annotations.get(uri).remove(o);
            if (annotations.get(uri).isEmpty()) {
                annotations.remove(uri);
            }
            return rval;
        } else {
            throw new IllegalArgumentException();
        }
    }

    ////////////////////
    // General functions
    protected LemonElement getStrElem(String name) {
        if (checkRemote) {
            resolveRemote();
        }
        return strElem.get(name);
    }

    protected void setStrElemDirect(String name, LemonElement elem) {
        checkRemote = false;
        if (elem != null && elem instanceof LemonElementImpl) {
            ((LemonElementImpl<?>) elem).addReference(this);
        }
        if (elem != null) {
            strElem.put(name, elem);
        } else {
            strElem.remove(name);
        }
    }

    protected void setStrElem(String name, LemonElement elem) {
        checkRemote = false;
        if (strElem.containsKey(name)) {
            if (strElem.get(name) instanceof LemonElementImpl) {
                ((LemonElementImpl<?>) strElem.get(name)).removeReference(this);
            }
        }
        if (model.allowUpdate()) {
            if (getURI() != null) {
                if (strElem.containsKey(name)) {
                    if (strElem.get(name).getURI() != null) {
                        model.updater().remove(getURI(), URI.create(LemonModel.LEMON_URI + name), strElem.get(name).getURI());
                    } else {
                        model.updater().remove(getURI(), URI.create(LemonModel.LEMON_URI + name), strElem.get(name).getID());
                    }
                }
                if (elem != null && elem.getURI() != null) {
                    model.updater().add(getURI(), URI.create(LemonModel.LEMON_URI + name), elem.getURI());
                } else if (elem != null) {
                    model.updater().add(getURI(), URI.create(LemonModel.LEMON_URI + name), elem.getID());
                }
            } else {
                if (strElem.containsKey(name)) {
                    if (strElem.get(name).getURI() != null) {
                        model.updater().remove(getID(), URI.create(LemonModel.LEMON_URI + name), strElem.get(name).getURI());
                    } else {
                        model.updater().remove(getID(), URI.create(LemonModel.LEMON_URI + name), strElem.get(name).getID());
                    }
                }
                if (elem != null && elem.getURI() != null) {
                    model.updater().add(getID(), URI.create(LemonModel.LEMON_URI + name), elem.getURI());
                } else if (elem != null) {
                    model.updater().add(getID(), URI.create(LemonModel.LEMON_URI + name), elem.getID());
                }
            }
        }
        setStrElemDirect(name, elem);
    }

    protected Text getStrText(String name) {
        if (checkRemote) {
            resolveRemote();
        }
        return strText.get(name);
    }

    protected void setStrText(String name, Text txt) {
        checkRemote = false;
        if (model.allowUpdate()) {
            if (strText.containsKey(name)) {
                if (getURI() != null) {
                    model.updater().remove(getURI(), URI.create(LemonModel.LEMON_URI + name), strText.get(name).value, strText.get(name).language);
                } else {
                    model.updater().remove(getID(), URI.create(LemonModel.LEMON_URI + name), strText.get(name).value, strText.get(name).language);
                }
            }
            if (txt != null) {
                if (getURI() != null) {
                    model.updater().add(getURI(), URI.create(LemonModel.LEMON_URI + name), txt.value, txt.language);
                } else {
                    model.updater().add(getID(), URI.create(LemonModel.LEMON_URI + name), txt.value, txt.language);
                }
            }
        }
        setStrTextDirect(name, txt);
    }

    protected void setStrTextDirect(String name, Text txt) {
        checkRemote = false;
        if (txt != null) {
            strText.put(name, txt);
        } else {
            strText.remove(name);
        }
    }

    @SuppressWarnings("unchecked")
    protected <Pred extends LemonPredicate> Map<Pred, Collection<LemonElement>> getPredElems(Class<Pred> clazz) {
        if (checkRemote) {
            resolveRemote();
        }
        Map<Pred, Collection<LemonElement>> rval = new HashMap<Pred, Collection<LemonElement>>();
        for (LemonPredicate p : predElems.keySet()) {
            if (clazz.isInstance(p)) {
                rval.put((Pred) p, predElems.get(p));
            }
        }
        return Collections.unmodifiableMap(rval);
    }

    @SuppressWarnings("unchecked")
    protected Collection<LemonElement> getPredElem(LemonPredicate p) {
        if (checkRemote) {
            resolveRemote();
        }
        if (predElems.containsKey(p)) {
            return Collections.unmodifiableCollection(new LinkedList<LemonElement>(predElems.get(p)));
        } else {
            return Collections.EMPTY_LIST;
        }
    }

    protected boolean addPredElem(LemonPredicate p, LemonElement e) {
        checkRemote = false;
        updateAddPredElem(e, p);
        return addPredElemDirect(p, e);
    }

    protected boolean addPredElemDirect(LemonPredicate p, LemonElement e) {
        checkRemote = false;
        if (e instanceof LemonElementImpl) {
            ((LemonElementImpl<?>) e).addReference(this);
        }
        if (!predElems.containsKey(p)) {
            predElems.put(p, new HashSet<LemonElement>());
        }
        return predElems.get(p).add(e);
    }

    protected void updateAddPredElem(LemonElement e, LemonPredicate p) {
        if (model.allowUpdate()) {
            if (getURI() != null) {
                if (e.getURI() != null) {
                    model.updater().add(getURI(), p.getURI(), e.getURI());
                } else {
                    model.updater().add(getURI(), p.getURI(), e.getID());
                }
            } else {
                if (e.getURI() != null) {
                    model.updater().add(getID(), p.getURI(), e.getURI());
                } else {
                    model.updater().add(getID(), p.getURI(), e.getID());
                }
            }
        }
    }

    protected boolean removePredElem(LemonPredicate p, LemonElement e) {
        checkRemote = false;
        updateRemovePredElem(e, p);
        if (e instanceof LemonElementImpl) {
            ((LemonElementImpl<?>) e).removeReference(this);
        }
        if (predElems.containsKey(p)) {
            return predElems.get(p).remove(e);
        } else {
            return false;
        }
    }

    private void updateRemovePredElem(LemonElement e, LemonPredicate p) {
        if (model.allowUpdate()) {
            if (getURI() != null) {
                if (e.getURI() != null) {
                    model.updater().remove(getURI(), p.getURI(), e.getURI());
                } else {
                    model.updater().remove(getURI(), p.getURI(), e.getID());
                }
            } else {
                if (e.getURI() != null) {
                    model.updater().remove(getID(), p.getURI(), e.getURI());
                } else {
                    model.updater().remove(getID(), p.getURI(), e.getID());
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    protected Collection<LemonElement> getStrElems(String name) {
        if (checkRemote) {
            resolveRemote();
        }
        if (strElems.containsKey(name)) {
            return Collections.unmodifiableCollection(new LinkedList<LemonElement>(strElems.get(name)));
        } else {
            return Collections.EMPTY_LIST;
        }
    }

    protected boolean addStrElem(String p, LemonElement e) {
        checkRemote = false;
        if (e instanceof LemonElementImpl) {
            ((LemonElementImpl<?>) e).addReference(this);
        }
        if (model.allowUpdate()) {
            if (getURI() != null) {
                if (e.getURI() != null) {
                    model.updater().add(getURI(), URI.create(LemonModel.LEMON_URI + p), e.getURI());
                } else {
                    model.updater().add(getURI(), URI.create(LemonModel.LEMON_URI + p), e.getID());
                }
            } else {
                if (e.getURI() != null) {
                    model.updater().add(getID(), URI.create(LemonModel.LEMON_URI + p), e.getURI());
                } else {
                    model.updater().add(getID(), URI.create(LemonModel.LEMON_URI + p), e.getID());
                }
            }
        }
        return addStrElemDirect(p, e);
    }

    protected boolean removeStrElem(String p, LemonElement e) {
        checkRemote = false;
        if (e instanceof LemonElementImpl) {
            ((LemonElementImpl<?>) e).removeReference(this);
        }
        if (model.allowUpdate()) {
            if (getURI() != null) {
                if (e.getURI() != null) {
                    model.updater().remove(getURI(), URI.create(LemonModel.LEMON_URI + p), e.getURI());
                } else {
                    model.updater().remove(getURI(), URI.create(LemonModel.LEMON_URI + p), e.getID());
                }
            } else {
                if (e.getURI() != null) {
                    model.updater().remove(getID(), URI.create(LemonModel.LEMON_URI + p), e.getURI());
                } else {
                    model.updater().remove(getID(), URI.create(LemonModel.LEMON_URI + p), e.getID());
                }
            }
        }
        if (strElems.containsKey(p)) {
            return strElems.get(p).remove(e);
        } else {
            return false;
        }
    }

    ////////////////
    // Merge support
    protected void addReference(LemonElementImpl<?> element) {
        referencers.add(element);
    }

    protected void removeReference(LemonElementImpl<?> element) {
        referencers.remove(element);
    }

    protected void updateReference(LemonElement from, LemonElement to) {
        for (LemonPredicate pred : predElems.keySet()) {
            if (predElems.get(pred).contains(from)) {
                predElems.get(pred).remove(from);
                predElems.get(pred).add(to);
            }
        }
        for (String pred : strElems.keySet()) {
            if (strElems.get(pred).contains(from)) {
                strElems.get(pred).remove(from);
                strElems.get(pred).add(to);
            }
        }
        List<String> preds = new LinkedList<String>();
        for (String pred : strElem.keySet()) {
            if (strElem.get(pred).equals(from)) {
                // Avoid concurrent modification
                preds.add(pred);
            }
        }
        for (String pred : preds) {
            strElem.put(pred, to);
        }
    }

    protected void mergeIn(Elem elem) {
        if (!(elem instanceof LemonElementImpl)) {
            throw new IllegalArgumentException("Cannot merge non-simple lemon element into simple store");
        }
        LemonElementImpl<?> lesp = (LemonElementImpl<?>) elem;
        predElems.putAll(lesp.predElems);
        strElems.putAll(lesp.strElems);
        strElem.putAll(lesp.strElem);
        strText.putAll(lesp.strText);
        types.addAll(lesp.types);
        for (Iterator<LemonElementImpl<?>> it = lesp.referencers.iterator(); it.hasNext();) {
            LemonElementImpl<?> rlesp = it.next();
            rlesp.updateReference(elem, this);
        }
    }

    ///////////////////////////
    // Serialization related
    protected boolean refers() {
        return !(predElems.isEmpty() && strElems.isEmpty() && strElem.isEmpty()
                && strText.isEmpty() && types.isEmpty());
    }

    public void visit(java.io.PrintWriter stream, SerializationState state) {
        boolean refers = refers();
        // If the object is referenced at mutliple points we only put its ID
        if (referencers.size() > 1) {
            if (this.getURI() != null) {
                printURI(this.getURI(), stream);
            } else {
                stream.print("_:" + this.getID());
            }
        } else {
            if (this.getURI() != null) {
                printURI(this.getURI(), stream);
            } else if (!refers || state.serialized.contains(this)) {
                stream.print("_:" + this.getID());
            } else {
                stream.print("[ ");
                printAsBlankNode(stream, state);
                stream.print(" ]");
            }
        }
    }
    
    private static String LS = System.getProperty("line.separator");

    protected void printAsBlankNode(java.io.PrintWriter stream, SerializationState state) {
        state.serialized.add(this);
        boolean first = true;

        for (LemonPredicate pred : predElems.keySet()) {
            if (!first) {
                stream.println(" ;");
            }
            boolean first2 = true;
            printURI(pred.getURI(), stream);
            for (LemonElement elem : predElems.get(pred)) {
                if (!first2) {
                    stream.println(" ,");
                } else {
                    stream.print(" ");
                }
                if (elem instanceof LemonElementImpl) {
                    ((LemonElementImpl) elem).visit(stream, state);
                    if (((LemonElementImpl) elem).isMultiReferenced() && elem.getURI() != null) {
                        state.postponed.add(elem);
                    }
                    first2 = false;
                } else {
                    if (elem.getURI() != null) {
                        stream.print(" ");
                        printURI(elem.getURI(), stream);
                    } else {
                        throw new RuntimeException("Not an element and blank... can't serialize");
                    }
                    first2 = false;
                }
            }
            first = false;
        }

        for (String pred : strElems.keySet()) {
            if (!first) {
                stream.println(" ;");
            }
            boolean first2 = true;
            stream.print(" lemon:" + pred + " ");
            for (LemonElement elem : strElems.get(pred)) {
                if (!first2) {
                    stream.println(" ,");
                }
                ((LemonElementImpl) elem).visit(stream, state);
                if (((LemonElementImpl) elem).isMultiReferenced() && elem.getURI() != null) {
                    state.postponed.add(elem);
                }
                first2 = false;
            }
            first = false;
        }

        for (String pred : strElem.keySet()) {
            if (!first) {
                stream.println(" ;");
            }
            stream.print(" lemon:" + pred + " ");
            LemonElement elem = strElem.get(pred);
            if (elem instanceof LemonElementImpl) {
                ((LemonElementImpl) elem).visit(stream, state);
                if (((LemonElementImpl) elem).isMultiReferenced() && elem.getURI() != null) {
                    state.postponed.add(elem);
                }
            } else {
                if(elem.getURI() != null) {
                    stream.print(" <" + elem.getURI().toString() + "> ");
                } else {
                    stream.print(" _:" + elem.getID() + " ");
                }
            }
            first = false;
        }

        for (String pred : strText.keySet()) {
            if (!first) {
                stream.println(" ;");
            }
            stream.print(" lemon:" + pred + " ");
            Text txt = strText.get(pred);
            if (txt.language != null) {
                stream.print("\"" + txt.value + "\"@" + txt.language);
            } else {
                stream.print("\"" + txt.value + "\"");
            }
            first = false;
        }

        boolean first2 = true;
        for (URI typeURI : types) {
            if (first) {
                stream.print(" a ");
            } else if (first2) {
                stream.print(" ;"+LS+" a ");
            } else {
                stream.println(" ,");
            }
            printURI(typeURI, stream);
            first2 = false;
            first = false;
        }

        printAsBlankNode(stream, state, first);
    }

    protected void printURI(URI uri, java.io.PrintWriter stream) {
        if (uri.toString().startsWith(LemonModel.LEMON_URI)) {
            stream.print("lemon:" + uri.toString().substring(LemonModel.LEMON_URI.length()));
        } else {
            stream.print("<" + uri + ">");
        }
    }

    protected void printAsBlankNode(java.io.PrintWriter stream, SerializationState state, boolean first) {
    }

    public void write(java.io.PrintWriter stream, SerializationState state) {
        if (!refers() || (referencers.size() == 1 && getURI() == null)) {
            return;
        }
        if (this.getURI() != null) {
            printURI(this.getURI(), stream);
        } else {
            stream.print("_:" + getID() + " ");
        }
        printAsBlankNode(stream, state);
        stream.println(" ."+LS);
    }

    protected boolean follow(LemonPredicate predicate) {
        return true;
    }

    protected boolean follow(String predName) {
        return true;
    }

    protected void doAccept(ElementVisitor visitor) {
    }

    @SuppressWarnings("unchecked")
    public final void accept(ElementVisitor visitor) {
        if (visitor.hasVisited((Elem) this)) {
            return;
        }
        if (visitor.visitFirst()) {
            visitor.visit((Elem) this);
        }
        for (Map.Entry<String, Collection<LemonElement>> elemsES : strElems.entrySet()) {
            final String pred = elemsES.getKey();
            final Collection<LemonElement> elems = elemsES.getValue();
            for (LemonElement elem : elems) {
                if (elem instanceof LemonElementImpl) {
                    if (!visitor.follow(URI.create(LemonModel.LEMON_URI + pred)) || follow(pred)) {
                        ((LemonElementImpl) elem).accept(visitor);
                    }
                } else {
                    //log.info("Could not visit " + elem);
                }
            }
        }
        for (Map.Entry<LemonPredicate, Collection<LemonElement>> elemsES : predElems.entrySet()) {
            final LemonPredicate pred = elemsES.getKey();
            final Collection<LemonElement> elems = elemsES.getValue();
            for (LemonElement elem : elems) {
                if (elem instanceof LemonElementImpl) {
                    if (!visitor.follow(pred.getURI()) || follow(pred)) {
                        ((LemonElementImpl) elem).accept(visitor);
                    }
                } else {
                    //log.info("Could not visit " + elem);
                }
            }
        }
        for (Map.Entry<String, LemonElement> es : strElem.entrySet()) {
            final String pred = es.getKey();
            final LemonElement elem = es.getValue();
            if (elem instanceof LemonElementImpl) {
                if (visitor.follow(URI.create(LemonModel.LEMON_URI + pred)) || follow(pred)) {
                    ((LemonElementImpl) elem).accept(visitor);
                }
            } else {
                //log.info("Could not visit " + elem);
            }
        }
        doAccept(visitor);
        if (!visitor.visitFirst()) {
            visitor.visit(this);
        }
    }

    public void clearAll() {
        for (Collection<LemonElement> elems : predElems.values()) {
            for (LemonElement lemonElement : elems) {
                if (lemonElement instanceof LemonElementImpl) {
                    ((LemonElementImpl) lemonElement).referencers.remove(this);
                }
            }
        }
        for (Collection<LemonElement> elems : strElems.values()) {
            for (LemonElement lemonElement : elems) {
                if (lemonElement instanceof LemonElementImpl) {
                    ((LemonElementImpl) lemonElement).referencers.remove(this);
                }
            }
        }
        for (LemonElement lemonElement : strElem.values()) {
            if (lemonElement instanceof LemonElementImpl) {
                ((LemonElementImpl) lemonElement).referencers.remove(this);
            }
        }
        predElems.clear();
        strElem.clear();
        strElems.clear();
        strText.clear();
    }

    protected ReaderAccepter defaultAccept(URI pred, URI value, LinguisticOntology lingOnto) {
        if (pred.equals(RDF_TYPE)) {
            if (!value.toString().startsWith(LemonModel.LEMON_URI)) {
                addTypeDirect(value);
                return null;
            } else {
                return null;
            }
        }
        if (!acceptProperties(pred, value, lingOnto)) {
            addAnnotation(pred, value);
        }
        return null;
    }

    protected ReaderAccepter defaultAccept(URI pred, String value) {
        addAnnotation(pred, value);
        return null;
    }

    protected void defaultAccept(URI pred, String val, String lang) {
        addAnnotation(pred, new Text(val, lang));
    }

    protected <X, Y> void merge(Map<X, Collection<Y>> map1, Map<X, Collection<Y>> map2) {
        for (X x : map2.keySet()) {
            if (!map1.containsKey(x)) {
                map1.put(x, map2.get(x));
            } else {
                for (Y y : map2.get(x)) {
                    if (!map1.get(x).contains(y)) {
                        map1.get(x).add(y);
                    }
                }
            }
        }
    }

    protected <X> void merge(Collection<X> list1, Collection<X> list2) {
        for (X x : list2) {
            if (!list1.contains(x)) {
                list1.add(x);
            }
        }
    }

    protected void defaultMerge(ReaderAccepter accepter, LinguisticOntology lingOnto, AccepterFactory factory) {
        if (this == accepter) {
            return;
        }
        if (accepter instanceof LemonElementImpl) {
            final LemonElementImpl<?> sle = (LemonElementImpl<?>) accepter;
            merge(this.annotations, sle.annotations);
            merge(this.predElems, sle.predElems);
            merge(this.referencers, sle.referencers);
            this.strElem.putAll(sle.strElem);
            merge(this.strElems, sle.strElems);
            this.strText.putAll(sle.strText);
            merge(this.types, sle.types);
        } else if (accepter instanceof UnactualizedAccepter) {
            ((UnactualizedAccepter) accepter).actualizedAs(this, lingOnto, factory);
        }
    }

    protected boolean acceptProperties(URI pred, URI value, LinguisticOntology lingOnto) {
        try {
            if (pred.getFragment() != null && lingOnto.getProperty(pred.getFragment()) != null
                    && value.getFragment() != null && lingOnto.getPropertyValue(value.getFragment()) != null) {
                addPredElemDirect(lingOnto.getProperty(pred.getFragment()), lingOnto.getPropertyValue(value.getFragment()));
                return true;
            }
        } catch (Exception x) {
        }
        return false;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<URI, Collection<Object>> getElements() {
        Map<URI, Collection<Object>> rval = new HashMap<URI, Collection<Object>>();
        for (Map.Entry<LemonPredicate, Collection<LemonElement>> entry : predElems.entrySet()) {
            rval.put(entry.getKey().getURI(), (Collection) entry.getValue());
        }

        for (Map.Entry<String, LemonElement> entry : strElem.entrySet()) {
            URI uri = URI.create(LemonModel.LEMON_URI + entry.getKey());
            if (!rval.containsKey(uri)) {
                rval.put(uri, new LinkedList<Object>());
            }
            rval.get(uri).add(entry.getValue());
        }

        for (Map.Entry<String, Collection<LemonElement>> entry : strElems.entrySet()) {
            URI uri = URI.create(LemonModel.LEMON_URI + entry.getKey());
            if (!rval.containsKey(uri)) {
                rval.put(uri, new LinkedList<Object>());
            }
            rval.get(uri).addAll(entry.getValue());
        }

        for (Map.Entry<String, Text> entry : strText.entrySet()) {
            URI uri = URI.create(LemonModel.LEMON_URI + entry.getKey());
            if (!rval.containsKey(uri)) {
                rval.put(uri, new LinkedList<Object>());
            }
            rval.get(uri).add(entry.getValue());
        }

        if (!rval.containsKey(RDF_TYPE) && !types.isEmpty()) {
            rval.put(RDF_TYPE, new LinkedList<Object>());
        }
        for (URI type : types) {
            rval.get(RDF_TYPE).add(type);
        }
        rval.putAll(annotations);
        return rval;
    }

    @Override
    public String getModelName() {
        return modelName;
    }

    public boolean isMultiReferenced() {
        return referencers.size() > 1;
    }

    protected void resolveRemote() {
        checkRemote = false;
        model.resolver().resolveRemote(model, this, 1);
    }
}
