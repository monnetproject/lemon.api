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

import eu.monnetproject.lemon.LemonModel;
import eu.monnetproject.lemon.LinguisticOntology;
import eu.monnetproject.lemon.impl.io.ReaderAccepter;
import eu.monnetproject.lemon.model.SenseDefinition;
import eu.monnetproject.lemon.model.Text;
import java.net.URI;

/**
 * Instantiated via {@link LemonFactoryImpl}
 * @author John McCrae
 */
public class DefinitionImpl extends LemonElementImpl<SenseDefinition> implements SenseDefinition {
    private static final long serialVersionUID = 31654907228618356L;

    DefinitionImpl(URI uri, LemonModelImpl model) {
        super(uri, "Definition",model);
    }

    DefinitionImpl(String id, LemonModelImpl model) {
        super(id, "Definition",model);
    }

    @Override
    public Text getValue() {
        return getStrText("value");
    }

    @Override
    public void setValue(final Text value) {
        setStrText("value", value);
    }

    @Override
    public ReaderAccepter accept(URI pred, URI value, LinguisticOntology lingOnto, AccepterFactory factory) {
        return defaultAccept(pred, value,lingOnto);
    }

    @Override
    public ReaderAccepter accept(URI pred, String bNode, LinguisticOntology lingOnto, AccepterFactory factory) {
        return defaultAccept(pred, bNode);
    }

    @Override
    public void accept(URI pred, String value, String lang, LinguisticOntology lingOnto, AccepterFactory factory) {
        if(pred.toString().equals(LemonModel.LEMON_URI+"value")) {
            setStrTextDirect("value",new Text(value, lang));
        } else {
            defaultAccept(pred, value, lang);
        }
    }
    
    @Override
    public void merge(ReaderAccepter accepter, LinguisticOntology lingOnto, AccepterFactory factory) {
        defaultMerge(accepter, lingOnto, factory);
    }
}
