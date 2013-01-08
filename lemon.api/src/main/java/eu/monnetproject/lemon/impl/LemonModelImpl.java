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
 ********************************************************************************
 */
package eu.monnetproject.lemon.impl;

import eu.monnetproject.lemon.RemoteUpdater;
import eu.monnetproject.lemon.*;
import eu.monnetproject.lemon.AbstractVisitor;
import eu.monnetproject.lemon.model.LemonElement;
import eu.monnetproject.lemon.model.LemonElementOrPredicate;
import eu.monnetproject.lemon.model.Lexicon;
import eu.monnetproject.lemon.model.MorphPattern;
import java.io.IOException;
import java.net.URI;
import java.util.*;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

/**
 * Instantiated via {@link LemonSerializerImpl}
 *
 * @author John McCrae
 */
public class LemonModelImpl implements LemonModel {

    private HashSet<Lexicon> lexica = new HashSet<Lexicon>();
    private HashSet<MorphPattern> patterns = new HashSet<MorphPattern>();
    private HashMap<URI, LemonElementOrPredicate> elements = new HashMap<URI, LemonElementOrPredicate>();
    private String baseURI;
    private final RemoteResolver resolver;
    private final RemoteUpdater updater;

    public LemonModelImpl(RemoteUpdaterFactory updaterFactory) {
        this.resolver = null;
        this.updater = updaterFactory == null ? null : updaterFactory.updaterForModel(this);
    }

    public LemonModelImpl(String baseURI, RemoteUpdaterFactory updaterFactory) {
        this.baseURI = baseURI;
        this.resolver = null;
        this.updater = updaterFactory == null ? null : updaterFactory.updaterForModel(this);
    }

    public LemonModelImpl(String baseURI, RemoteResolver resolver, RemoteUpdaterFactory updaterFactory) {
        this.baseURI = baseURI;
        this.resolver = resolver;
        this.updater = updaterFactory == null ? null : updaterFactory.updaterForModel(this);
    }

//    public void activate(Map properties) {
//        if (properties != null && properties.containsKey("baseURI")) {
//            baseURI = properties.get("baseURI").toString();
//        }
//    }
    @Override
    public Collection<Lexicon> getLexica() {
        if (resolver != null && resolver instanceof LexiconResolver && lexica.isEmpty()) {
            final Set<URI> lexicaURIs = ((LexiconResolver) resolver).getLexica();
            for (URI lexicaUri : lexicaURIs) {
                lexica.add(new LexiconImpl(lexicaUri, this));
            }
        }
        return lexica;
    }

    public LemonElementOrPredicate getLemonElement(URI uri) {
        return elements.get(uri);
    }

    @Override
    public URI getContext() {
        if (baseURI != null) {
            return URI.create(baseURI);
        } else {
            return null;
        }
    }
    private LemonFactory factory = new LemonFactoryImpl(elements, this);

    @Override
    public LemonFactory getFactory() {
        return factory;
    }

    @Override
    public Lexicon addLexicon(URI uri, String language) {
        Lexicon lexicon = new LexiconImpl(uri, this);
        if(updater != null) {
            updater.add(uri, URI.create("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"), URI.create(LemonModel.LEMON_URI+"Lexicon"));
        } 
        lexicon.setLanguage(language);
        lexica.add(lexicon);
        return lexicon;
    }

    @Override
    public void removeLexicon(Lexicon lexicon) {
        lexica.remove(lexicon);
    }

    void addLexicon(Lexicon lexicon) {
        lexica.add(lexicon);
    }

    @Override
    public void addPattern(MorphPattern pattern) {
        patterns.add(pattern);
    }

    @Override
    public Collection<MorphPattern> getPatterns() {
        return patterns;
    }

    @Override
    public <Elem extends LemonElementOrPredicate> Iterator<Elem> query(Class<Elem> target, String sparqlQuery) {
        if (resolver != null && resolver instanceof SPARQLResolver) {
            try {
                return ((SPARQLResolver) resolver).query(target, sparqlQuery, this);   
            } catch(IOException x) {
                throw new RuntimeException(x);
            } catch(ParserConfigurationException x) {
                throw new RuntimeException(x);
            } catch(SAXException x) {
                throw new RuntimeException(x);
            }
        } else {
            throw new RuntimeException("No SPARQL support in this model " + (resolver == null ? "null" : resolver.getClass().toString()));
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <Elem extends LemonElement> void merge(Elem from, Elem to) {
        if (to instanceof LemonElementImpl) {
            ((LemonElementImpl<Elem>) to).mergeIn(from);
        } else {
            throw new IllegalArgumentException("Cannot merge element I did not create");
        }
    }

    @Override
    public void purgeLexicon(Lexicon lxcn, LinguisticOntology lo) {
        final PurgeVisitor purgeVisitor = new PurgeVisitor(lo);
        if (lxcn instanceof LemonElementImpl) {
            ((LemonElementImpl) lxcn).accept(purgeVisitor);
            lexica.remove(lxcn);
        } else {
            throw new IllegalArgumentException("Lexicon not created by me");
        }
    }

    @Override
    public void importLexicon(Lexicon lxcn, LinguisticOntology lo) {
        final CopyVisitor copyVisitor = new CopyVisitor(lo, this);
        if (lxcn instanceof LexiconImpl) {
            ((LexiconImpl) lxcn).accept(copyVisitor);
        }
        lexica.add(lxcn);
    }

    public final boolean allowRemote() {
        return resolver != null;
    }

    public final boolean allowUpdate() {
        return updater != null;
    }

    public final RemoteResolver resolver() {
        return resolver;
    }

    public final RemoteUpdater updater() {
        return updater;
    }

    private static class PurgeVisitor extends AbstractVisitor {

        private HashSet<LemonElement> visited = new HashSet<LemonElement>();

        public PurgeVisitor(LinguisticOntology lingOnto) {
            super(lingOnto);
        }

        @Override
        public void visit(LemonElement _element) {
            if(!(_element instanceof LemonElementImpl)) {
                throw new IllegalArgumentException();
            }
            final LemonElementImpl<?> element = (LemonElementImpl)_element;
            element.clearAll();
            visited.add(element);
        }

        @Override
        public boolean hasVisited(LemonElement element) {
            return visited.contains(element);
        }
    }
    
    public void register(URI uri, LemonElementOrPredicate lep) {
        this.elements.put(uri, lep);
    }
}
