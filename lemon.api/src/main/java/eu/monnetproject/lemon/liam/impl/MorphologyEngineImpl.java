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

import eu.monnetproject.lemon.LemonFactory;
import eu.monnetproject.lemon.impl.LemonSerializerImpl;
import eu.monnetproject.lemon.liam.*;
import eu.monnetproject.lemon.model.LexicalEntry;
import eu.monnetproject.lemon.model.LexicalForm;
import eu.monnetproject.lemon.model.MorphPattern;
import eu.monnetproject.lemon.model.MorphTransform;
import eu.monnetproject.lemon.model.Property;
import eu.monnetproject.lemon.model.PropertyValue;
import eu.monnetproject.lemon.model.Prototype;
import eu.monnetproject.lemon.model.Text;
import java.util.Collection;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author John McCrae
 */
public class MorphologyEngineImpl implements MorphologyEngine {

    private LexicalForm asForm(String value, Prototype prototype) {
        final LexicalForm form = new VirtualForm();
        form.setWrittenRep(new Text(value, null));
        for (Map.Entry<Property, Collection<PropertyValue>> props : prototype.getPropertys().entrySet()) {
            for (PropertyValue pv : props.getValue()) {
                form.addProperty(props.getKey(), pv);
            }
        }
        return form;
    }

    private LexicalForm applyRule(TransformProtoype transform, LexicalForm form) throws MorphologyApplicationException {
        for (String rule : transform.getTransform().getRules()) {
            if (rule.contains("/")) {
                final String[] split = rule.split("/");
                if (split.length != 2) {
                    throw new MorphologyApplicationException("Rule contains more than one /");
                }
                final String[] lrhs = realizeRegexes(split[0], split[1]);
                final String lhs = lrhs[0];
                if (form.getWrittenRep().value.matches(lhs)) {
                    final String rhs = lrhs[1];
                    return asForm(form.getWrittenRep().value.replaceAll("^" + lhs + "$", rhs), transform.getPrototype());
                } else {
                    //log.warning(form.getWrittenRep().value + " does not match " + lhs);
                }
            }
        }
        for (String rule : transform.getTransform().getRules()) {
            if (!rule.contains("/")) {
                if (rule.indexOf("~") < 0) {
                    throw new MorphologyApplicationException("RHS does not contain a ~");
                }
                final String rhs = rule.replaceAll("~", "\\$1");
                return asForm(form.getWrittenRep().value.replaceAll("^(.*)$", rhs), transform.getPrototype());
            }
        }
        throw new MorphologyApplicationException("Could not apply a rule");
    }
    
    private boolean canApplyRule(TransformProtoype transform, LexicalForm form) throws MorphologyApplicationException {
        for (String rule : transform.getTransform().getRules()) {
            if (rule.contains("/")) {
                final String[] split = rule.split("/");
                if (split.length != 2) {
                    throw new MorphologyApplicationException("Rule contains more than one /");
                }
                final String[] lrhs = realizeRegexes(split[0], split[1]);
                final String lhs = lrhs[0];
                if (form.getWrittenRep().value.matches(lhs)) {
                    return true;
                } 
            } else {
                return true;
            }
        }
        return false;
    }

    private String[] realizeRegexes(String lhs, String rhs) {
        if (lhs.indexOf("~") < 0) {
            throw new MorphologyApplicationException("LHS does not contain a ~");
        }
        if (rhs.indexOf("~") < 0) {
            throw new MorphologyApplicationException("RHS does not contain a ~");
        }
        int leftBracks = 1;
        for (int i = 0; i < lhs.indexOf("~"); i++) {
            if (lhs.charAt(i) == '(' && lhs.charAt(i + 1) != '?') {
                leftBracks++;
            }
        }
        int highestIdx = 0;
        final Matcher matcher = Pattern.compile("\\$(\\d+)").matcher(rhs);
        while (matcher.find()) {
            int idx = Integer.parseInt(matcher.group(1));
            if (idx > highestIdx) {
                highestIdx = idx;
            }
        }
        for (int i = leftBracks; i <= highestIdx; i++) {
            rhs = rhs.replaceAll("\\$"+i, "\\$"+(i+1));
        }
        rhs = rhs.replaceAll("~", "\\$" + leftBracks);
        lhs = lhs.replaceAll("~", "(.*?)");
        return new String[] { lhs,rhs };
    }

    private TransformProtoype findGenerator(MorphPattern mp, Map<Property, Collection<PropertyValue>> map) {
        for (MorphTransform transform : mp.getTransforms()) {
            PROTOS:
            for (Prototype generation : transform.getGenerates()) {
                for (Map.Entry<Property, Collection<PropertyValue>> propEntry : map.entrySet()) {
                    final Collection<PropertyValue> propVal = generation.getProperty(propEntry.getKey());
                    for (PropertyValue pv : propEntry.getValue()) {
                        if (!propVal.contains(pv)) {
                            continue PROTOS;
                        }
                    }
                }
                return new TransformProtoype(transform, generation);
            }
        }
        return null;
    }

    private LexicalForm findForm(LexicalEntry entry, MorphTransform transform) {
        if (transform.getOnStem() == null) {
            return entry.getCanonicalForm();
        }
        FORMS:
        for (LexicalForm form : entry.getForms()) {
            for (Map.Entry<Property, Collection<PropertyValue>> propEntry : transform.getOnStem().getPropertys().entrySet()) {
                final Collection<PropertyValue> propVal = form.getProperty(propEntry.getKey());
                for (PropertyValue pv : propEntry.getValue()) {
                    if (!propVal.contains(pv)) {
                        continue FORMS;
                    }
                }
            }
            return form;
        }
        return null;
    }


    public LexicalForm generate(LexicalEntry entry, MorphPattern mp, Map<Property, Collection<PropertyValue>> map) {
        // Find a suitable generator
        final TransformProtoype transform = findGenerator(mp, map);
        // Try compound generation
        if (transform == null) {
            LexicalForm result = compoundGeneration(entry, mp, map);
            if (result != null) {
                return result;
            }
        }
        if (transform == null) {
            throw new MorphologyApplicationException("No suitable transform for given prop values");
        }
        // Find stem
        LexicalForm form = findForm(entry, transform.getTransform());
        if (form == null) {
            throw new MorphologyApplicationException("No suitable form for given prop value");
        }
        // Apply to stem
        final LexicalForm result = applyRule(transform, form);
        if (result == null) {
            throw new MorphologyApplicationException("Could not find suitable rule to apply");
        } else {
            return result;
        }
    }

    private LexicalForm compoundGeneration(LexicalEntry entry, MorphPattern mp, Map<Property, Collection<PropertyValue>> map) {
        for (MorphTransform transform : mp.getTransforms()) {
            LexicalForm form = findForm(entry, transform);
            if (form != null) {
                for (Prototype generation : transform.getGenerates()) {
                    for (Map.Entry<Property, Collection<PropertyValue>> props : generation.getPropertys().entrySet()) {
                        for (PropertyValue pv : props.getValue()) {
                            if (map.containsKey(props.getKey()) && map.get(props.getKey()).contains(pv)) {
                                final LexicalForm form2 = compoundGeneration(entry, mp, map, new TransformProtoype(transform, generation), form);
                                if (satisfies(form2, map)) {
                                    return form2;
                                }
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    private boolean satisfies(LexicalForm form, Map<Property, Collection<PropertyValue>> map) {
        if (form == null) {
            return false;
        }
        for (Map.Entry<Property, Collection<PropertyValue>> props : map.entrySet()) {
            if (form.getProperty(props.getKey()) == null || !form.getProperty(props.getKey()).containsAll(props.getValue())) {
                return false;
            }
        }
        return true;
    }

    private LexicalForm compoundGeneration(LexicalEntry entry, MorphPattern mp, Map<Property, Collection<PropertyValue>> map, TransformProtoype transform, LexicalForm form) {
        if(!canApplyRule(transform, form))  {
            return null;
        }
        final LexicalForm newForm = applyRule(transform, form);
        if (newForm != null) {
            for (Map.Entry<Property, Collection<PropertyValue>> props : form.getPropertys().entrySet()) {
                for (PropertyValue pv : props.getValue()) {
                    newForm.addProperty(props.getKey(), pv);
                }
            }
            if (satisfies(newForm, map)) {
                return newForm;
            }

            for (MorphTransform next : transform.getTransform().getNextTransforms()) {
                for (Prototype generation : next.getGenerates()) {
                    for (Map.Entry<Property, Collection<PropertyValue>> props : generation.getPropertys().entrySet()) {
                        for (PropertyValue pv : props.getValue()) {
                            if (map.get(props.getKey()).contains(pv)) {
                                final LexicalForm form2 = compoundGeneration(entry, mp, map, new TransformProtoype(next, generation), newForm);
                                if (satisfies(form2, map)) {
                                    return form2;
                                }
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    private static class TransformProtoype {

        private final MorphTransform transform;
        private final Prototype prototype;

        public TransformProtoype(MorphTransform transform, Prototype prototype) {
            this.transform = transform;
            this.prototype = prototype;
        }

        public Prototype getPrototype() {
            return prototype;
        }

        public MorphTransform getTransform() {
            return transform;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final TransformProtoype other = (TransformProtoype) obj;
            if (this.transform != other.transform && (this.transform == null || !this.transform.equals(other.transform))) {
                return false;
            }
            if (this.prototype != other.prototype && (this.prototype == null || !this.prototype.equals(other.prototype))) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hash = 3;
            hash = 79 * hash + (this.transform != null ? this.transform.hashCode() : 0);
            hash = 79 * hash + (this.prototype != null ? this.prototype.hashCode() : 0);
            return hash;
        }
    }
}
