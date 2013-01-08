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

import eu.monnetproject.lemon.AbstractVisitor;
import eu.monnetproject.lemon.LemonFactory;
import eu.monnetproject.lemon.LemonModel;
import eu.monnetproject.lemon.LinguisticOntology;
import eu.monnetproject.lemon.model.LemonElement;
import eu.monnetproject.lemon.model.LemonPredicate;
import eu.monnetproject.lemon.model.LexicalEntry;
import eu.monnetproject.lemon.model.Lexicon;
import eu.monnetproject.lemon.model.Part;
import eu.monnetproject.lemon.model.Phrase;
import eu.monnetproject.lemon.model.Word;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author John McCrae
 */
public class CopyVisitor extends AbstractVisitor {

    private final LemonModelImpl target;
    private final HashMap<LemonElement, LemonElement> copiedMap = new HashMap<LemonElement, LemonElement>();
    private final HashSet<LemonElement> visited = new HashSet<LemonElement>();

    public CopyVisitor(LinguisticOntology lingOnto, LemonModelImpl target) {
        super(lingOnto);
        this.target = target;
    }

    @Override
    public void visit(LemonElement element) {
        System.err.println("copy @" + element.getURI());
        LemonElement newElement = translate(element);
        final Class[] interfaces = element.getClass().getInterfaces();
        for (Class interfays : interfaces) {
            if (interfays.getName().startsWith("eu.monnetproject.lemon.model")) {
                for (Method method : interfays.getDeclaredMethods()) {
                    if (method.getName().startsWith("get")) {
                        if (method.getReturnType().isAssignableFrom(Collection.class) && method.getParameterTypes().length == 0) {
                            try {
                                final Collection result = (Collection) method.invoke(element);
                                Method pairMethod = getPairMethod(method, interfays);
                                for (Object obj : result) {
                                    Object newObj;
                                    if (obj instanceof LemonElement) {
                                        newObj = translate((LemonElement) obj);
                                    } else {
                                        newObj = obj;
                                    }
                                    pairMethod.invoke(newElement, newObj);
                                }
                            } catch (Exception x) {
                                System.err.println(method.getName());
                                x.printStackTrace();
                            }
                        } else if (method.getReturnType().isAssignableFrom(Map.class)) {
                            try {
                                Method pairMethod = getPairMethod(method, interfays);
                                final Map result = (Map) method.invoke(element);
                                for (Map.Entry<Object, Object> entry : (Set<Map.Entry<Object, Object>>) result.entrySet()) {
                                    LemonPredicate pred = (LemonPredicate) entry.getKey();
                                    Collection objs = (Collection) entry.getValue();
                                    for (Object obj : objs) {
                                        Object newObj;
                                        if (obj instanceof LemonElement) {
                                            newObj = translate((LemonElement) obj);
                                        } else {
                                            newObj = obj;
                                        }
                                        pairMethod.invoke(newElement, pred, newObj);
                                    }
                                }
                            } catch (Exception x) {
                                System.err.println(method.getName());
                                x.printStackTrace();
                            }
                        } else if(method.getParameterTypes().length == 0 && !method.getName().equals("getModel")) {
                            try {
                                Method pairMethod = getPairMethod(method, interfays);
                                final Object obj = method.invoke(element);
                                Object newObj;
                                if (obj instanceof LemonElement) {
                                    newObj = translate((LemonElement) obj);
                                } else {
                                    newObj = obj;
                                }
                                pairMethod.invoke(newElement, newObj);
                            } catch (Exception x) {
                                System.err.println(method.getName());
                                x.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
        visited.add(element);
    }

    private Method getPairMethod(Method method, Class interfays) {
        for (Method m2 : interfays.getDeclaredMethods()) {
            if(method.getName().replace("get", "add").replaceAll("s$", "").equals(m2.getName())) {
                return m2;
            }
            if(method.getName().replace("get", "set").equals(m2.getName())) {
                return m2;
            }
        }
        System.err.println("Failed: " + method.getName());
        return null;
    }

    private LemonElement translate(LemonElement element) {
        LemonElement newElement;
        if (!copiedMap.containsKey(element)) {
            newElement = make(element);
            if (newElement == null) {
                newElement = element;
                if(newElement.getURI() != null) {
                    target.register(newElement.getURI(), newElement);
                }
                copiedMap.put(element, element);
            } else {
                copiedMap.put(element, newElement);
            }
        } else {
            newElement = copiedMap.get(element);
        }
        return newElement;
    }

    public <E extends LemonElement> E make(E e) {
        if (e.getURI() != null) {
            return make((Class<E>) e.getClass(), e.getURI(), e);
        } else {
            return make((Class<E>) e.getClass(), e.getID());
        }
    }

    public <E extends LemonElement> E make(Class<E> clazz, String bNode) {
        final LemonFactory factory = target.getFactory();
        if (clazz.equals(ArgumentImpl.class)) {
            return (E) factory.makeArgument();
        } else if (clazz.equals(ComponentImpl.class)) {
            return (E) factory.makeComponent();
        } else if (clazz.equals(ConditionImpl.class)) {
            return (E) factory.makeCondition();
        } else if (clazz.equals(DefinitionImpl.class)) {
            return (E) factory.makeDefinition();
        } else if (clazz.equals(ExampleImpl.class)) {
            return (E) factory.makeExample();
        } else if (clazz.equals(FormImpl.class)) {
            return (E) factory.makeForm();
        } else if (clazz.equals(FrameImpl.class)) {
            return (E) factory.makeFrame();
        } else if (clazz.equals(LexicalSenseImpl.class)) {
            return (E) factory.makeSense();
        } else if (clazz.equals(MorphPatternImpl.class)) {
            return (E) factory.makeMorphPattern();
        } else if (clazz.equals(MorphTransformImpl.class)) {
            return (E) factory.makeMorphTransform();
        } else if (clazz.equals(NodeImpl.class)) {
            return (E) factory.makeNode();
        } else if (clazz.equals(PrototypeImpl.class)) {
            return (E) factory.makePrototype();
        } else {
            return null;
        }
    }

    public <E extends LemonElement> E make(Class<E> clazz, URI uri, E e) {
        final LemonFactory factory = target.getFactory();
        if (clazz.equals(ArgumentImpl.class)) {
            return (E) factory.makeArgument(uri);
        } else if (clazz.equals(ComponentImpl.class)) {
            return (E) factory.makeComponent(uri);
        } else if (clazz.equals(ConditionImpl.class)) {
            return (E) factory.makeCondition(uri);
        } else if (clazz.equals(DefinitionImpl.class)) {
            return (E) factory.makeDefinition(uri);
        } else if (clazz.equals(ExampleImpl.class)) {
            return (E) factory.makeExample(uri);
        } else if (clazz.equals(FormImpl.class)) {
            return (E) factory.makeForm(uri);
        } else if (clazz.equals(FrameImpl.class)) {
            return (E) factory.makeFrame(uri);
        } else if (clazz.equals(LexicalSenseImpl.class)) {
            return (E) factory.makeSense(uri);
        } else if (clazz.equals(MorphPatternImpl.class)) {
            return (E) factory.makeMorphPattern(uri);
        } else if (clazz.equals(MorphTransformImpl.class)) {
            return (E) factory.makeMorphTransform(uri);
        } else if (clazz.equals(NodeImpl.class)) {
            return (E) factory.makeNode(uri);
        } else if (clazz.equals(PrototypeImpl.class)) {
            return (E) factory.makePrototype(uri);
        } else if (clazz.equals(LexicalEntry.class)) {
            return (E) factory.makeLexicalEntry(uri);
        } else if (clazz.equals(Phrase.class)) {
            return (E) factory.makePhrase(uri);
        } else if (clazz.equals(Part.class)) {
            return (E) factory.makePart(uri);
        } else if (clazz.equals(Word.class)) {
            return (E) factory.makeWord(uri);
        } else if (clazz.equals(Lexicon.class)) {
            return (E) target.addLexicon(uri, ((Lexicon) e).getLanguage());
        } else {
            return null;
        }
    }

    @Override
    public boolean hasVisited(LemonElement element) {
        return visited.contains(element);
    }
}
