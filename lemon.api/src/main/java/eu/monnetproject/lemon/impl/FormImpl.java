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

import eu.monnetproject.lemon.LemonModel;
import eu.monnetproject.lemon.LinguisticOntology;
import eu.monnetproject.lemon.impl.io.ReaderAccepter;
import eu.monnetproject.lemon.model.FormVariant;
import eu.monnetproject.lemon.model.LemonPredicate;
import eu.monnetproject.lemon.model.LexicalForm;
import eu.monnetproject.lemon.model.Representation;
import eu.monnetproject.lemon.model.Text;
import java.net.URI;
import java.util.*;

/**
 * Instantiated via {@link LemonFactoryImpl}
 *
 * @author John McCrae
 */
public class FormImpl extends LemonElementImpl implements LexicalForm {

    private static final long serialVersionUID = 5932803932322555340L;
    public HashMap<Representation, Collection<Text>> reps = new HashMap<Representation, Collection<Text>>();

    FormImpl(URI uri, LemonModelImpl model) {
        super(uri, "Form", model);
    }

    FormImpl(String id, LemonModelImpl model) {
        super(id, "Form", model);
    }

    @Override
    public Text getWrittenRep() {
        return getStrText("writtenRep");
    }

    @Override
    public void setWrittenRep(final Text writtenRep) {
        setStrText("writtenRep", writtenRep);
    }

    @Override
    public Map<Representation, Collection<Text>> getRepresentations() {
        if (checkRemote) {
            resolveRemote();
        }
        return Collections.unmodifiableMap(reps);
    }

    @Override
    public Collection<Text> getRepresentation(final Representation representation) {
        if (checkRemote) {
            resolveRemote();
        }
        return Collections.unmodifiableCollection(reps.get(representation));
    }

    @Override
    public boolean addRepresentation(final Representation representation, final Text representationVal) {
        checkRemote = false;
        if (model.allowUpdate()) {
            if (getURI() != null) {
                model.updater().add(getURI(), representation.getURI(), representationVal.value, representationVal.language);
            } else {
                model.updater().add(getID(), representation.getURI(), representationVal.value, representationVal.language);
            }
        }
        if (!reps.containsKey(representation)) {
            reps.put(representation, new HashSet<Text>());
        }
        return reps.get(representation).add(representationVal);
    }

    @Override
    public boolean removeRepresentation(final Representation representation, final Text representationVal) {
        checkRemote = false;
        if (model.allowUpdate()) {
            if (getURI() != null) {
                model.updater().remove(getURI(), representation.getURI(), representationVal.value, representationVal.language);
            } else {
                model.updater().remove(getID(), representation.getURI(), representationVal.value, representationVal.language);
            }
        }
        if (reps.containsKey(representation)) {
            return reps.get(representation).remove(representationVal);
        } else {
            return false;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<FormVariant, Collection<LexicalForm>> getFormVariants() {
        return (Map<FormVariant, Collection<LexicalForm>>) getPredElems(FormVariant.class);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<LexicalForm> getFormVariant(final FormVariant formVariant) {
        return (Collection<LexicalForm>) getPredElem(formVariant);
    }

    @Override
    public boolean addFormVariant(final FormVariant formVariant, final LexicalForm formVariantVal) {
        return addPredElem(formVariant, formVariantVal);
    }

    @Override
    public boolean removeFormVariant(final FormVariant formVariant, final LexicalForm formVariantVal) {
        return removePredElem(formVariant, formVariantVal);
    }

    @Override
    protected boolean refers() {
        return super.refers() || !reps.isEmpty();
    }

    @Override
    protected void printAsBlankNode(java.io.PrintWriter stream, SerializationState state, boolean first) {
        for (Representation rep : reps.keySet()) {
            if (!first) {
                stream.println(" ;");
            }
            boolean first2 = true;
            printURI(rep.getURI(), stream);
            for (Text txt : reps.get(rep)) {
                if (!first2) {
                    stream.println(" ,");
                }
                if (txt.language != null) {
                    stream.print("\"" + txt.value + "\"@" + txt.language);
                } else {
                    stream.print("\"" + txt.value + "\"");
                }
                first2 = false;
            }
        }
    }

    @Override
    protected boolean follow(LemonPredicate predicate) {
        return !(predicate instanceof FormVariant);
    }

    @Override
    public void clearAll() {
        reps.clear();
        super.clearAll();
    }

    @Override
    public ReaderAccepter accept(URI pred, final URI value, LinguisticOntology lingOnto, AccepterFactory factory) {
        if (lingOnto != null) {
            for (FormVariant var : lingOnto.getFormVariant()) {
                if (var.getURI().equals(pred)) {
                    final FormImpl formImpl = factory.getFormImpl(value);
                    addPredElemDirect(var, formImpl);
                    return formImpl;
                }
            }
        }
        return defaultAccept(pred, value, lingOnto);
    }

    @Override
    public ReaderAccepter accept(URI pred, String value, LinguisticOntology lingOnto, AccepterFactory factory) {
        if (lingOnto != null) {
            for (FormVariant var : lingOnto.getFormVariant()) {
                if (var.getURI().equals(pred)) {
                    final FormImpl formImpl = factory.getFormImpl(value);
                    addPredElemDirect(var, formImpl);
                    return formImpl;
                }
            }
        }
        return defaultAccept(pred, value);
    }

    @Override
    public void accept(URI pred, String value, String lang, LinguisticOntology lingOnto, AccepterFactory factory) {
        if (pred.toString().equals(LemonModel.LEMON_URI + "writtenRep")) {
            setStrTextDirect("writtenRep", new Text(value, lang));
            return;
        } else {
            for (Representation rep : lingOnto.getRepresentation()) {
                if (rep.getURI().equals(pred)) {
                    if (!reps.containsKey(rep)) {
                        reps.put(rep, new HashSet<Text>());
                    }
                    reps.get(rep).add(new Text(value, lang));
                    return;
                }
            }
        }
        defaultAccept(pred, value, lang);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<URI, Collection<Object>> getElements() {
        Map<URI, Collection<Object>> rv = super.getElements();
        for (Map.Entry<Representation, Collection<Text>> entry : reps.entrySet()) {
            if (!rv.containsKey(entry.getKey().getURI())) {
                rv.put(entry.getKey().getURI(), new LinkedList<Object>());
            }
            for (Text text : entry.getValue()) {
                ((Collection) rv.get(entry.getKey().getURI())).add(text);
            }
        }
        return rv;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void merge(ReaderAccepter accepter, LinguisticOntology lingOnto, AccepterFactory factory) {
        if (accepter instanceof FormImpl) {
            merge(this.reps, ((FormImpl) accepter).reps);
        }
        defaultMerge(accepter, lingOnto, factory);
    }
}
