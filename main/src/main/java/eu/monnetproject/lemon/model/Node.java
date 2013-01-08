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

import java.util.Collection;
import java.util.Map;

/**
 * A node in a phrase structure tree
 * @author John McCrae
 */
public interface Node extends LemonElement {
	/** Get the constituent of the node, that is the phrase trype */
	Constituent getConstituent();
	/** Set the constituent of the node */
	void setConstituent(final Constituent constituent);
	/** Get the edges to other nodes */
	Map<Edge,Collection<Node>> getEdges();
	/** Get the edges to other nodes of a given arc type */
	Collection<Node> getEdge(final Edge edge);
	/** Add an edge to the tree */
	boolean addEdge(final Edge edge, final Node edgeVal);
	/** Remove an edge from the tree */
	boolean removeEdge(final Edge edge, final Node edgeVal);
	/** Get the leaf of the node. A node must have either a leaf xor zero or more edges */
	PhraseTerminal getLeaf();
	/** Set the leaf of the node */
	void setLeaf(final PhraseTerminal product);
	/** Get the separator value between the subnodes. This will most likely be a single space */
	Text getSeparator();
	/** Set the separator value between the subnodes*/ 
	void setSeparator(final Text separator);	
}
