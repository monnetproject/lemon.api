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
package eu.monnetproject.lemon.model;

import eu.monnetproject.lemon.LemonModel;
import eu.monnetproject.lemon.URIValue;
import java.net.URI;

/**
 * A predicate used to relate senses
 * @author John McCrae
 */
public interface SenseRelation extends LemonPredicate {

    /** The lemon super-property of all relations */
    final SenseRelation senseRelation = new SenseRelationImpl(LemonModel.LEMON_URI + "senseRelation");
    /** Indicates equivalence between the senses */
    final SenseRelation equivalent = new SenseRelationImpl(LemonModel.LEMON_URI + "equivalent");
    /** Indicates incompatibility (disjointness) between the senses */
    final SenseRelation incompatible = new SenseRelationImpl(LemonModel.LEMON_URI + "incompatible");
    /** Indicates that the target sense is narrower */
    final SenseRelation narrower = new SenseRelationImpl(LemonModel.LEMON_URI + "narrower");
    /** Indicates that the target sense is broader */
    final SenseRelation broader = new SenseRelationImpl(LemonModel.LEMON_URI + "broader");
}

class SenseRelationImpl extends URIValue implements SenseRelation {

    public SenseRelationImpl(String uri) {
        super(URI.create(uri));
    }
    
}