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
package eu.monnetproject.lemon;

import eu.monnetproject.lemon.model.PropertyValue;
import eu.monnetproject.lemon.model.Definition;
import eu.monnetproject.lemon.model.LexicalVariant;
import eu.monnetproject.lemon.model.SenseRelation;
import eu.monnetproject.lemon.model.FormVariant;
import eu.monnetproject.lemon.model.Representation;
import eu.monnetproject.lemon.model.Condition;
import eu.monnetproject.lemon.model.Edge;
import eu.monnetproject.lemon.model.LemonElementOrPredicate;
import eu.monnetproject.lemon.model.Property;
import eu.monnetproject.lemon.model.SynArg;
import eu.monnetproject.lemon.model.SenseContext;
import eu.monnetproject.lemon.model.Text;
import java.net.URI;
import java.util.Collection;
import java.util.Map;

/**
 * Gives access to all values specified in a linguistic ontology
 * @author John McCrae
 */
public interface LinguisticOntology {
	
	/**
	 * Get a property reference by its name
	 * @throws IllegalArgumentException If the name is not recognised by the ontology
	 */
	public Property getProperty(String name);

	/**
	 * Get the set of properties listed in the linguistics ontology
	 */
	public Collection<Property> getProperties();
	
	/**
	 * Get a property value by its name
	 * @throws IllegalArgumentException If the name is not recognised by the ontology
	 */
	public PropertyValue getPropertyValue(String name);
		
	/**
	 * Get the set of property values listed in the linguistic ontology for a given property 
	 */
	public Collection<PropertyValue> getValues(Property property);
		
	/**
	 * Get a synArg predicate by its name 
	 * @throws IllegalArgumentException If the name is not recognised by the ontology
	 */
	 public SynArg getSynArg(String name);
	 
	 /**
	  * Get the set of synArg predicates listed in the linguistic ontology
	  */
	 public Collection<SynArg> getSynArgs();
	 
	 /**
	  * Get the necessary synArgs for a given frame
	  */
	 public Collection<SynArg> getSynArgsForFrame(URI frameClass);
	
	/**
	 * Get a condition predicate by its name 
	 * @throws IllegalArgumentException If the name is not recognised by the ontology
	 */
	 public Condition getCondition(String name);
	 
	 /**
	  * Get the set of condition predicates listed in the linguistic ontology
	  */
	 public Collection<Condition> getConditions();
	 
	 
	/**
	 * Get a context predicate by its name 
	 * @throws IllegalArgumentException If the name is not recognised by the ontology
	 */
	 public SenseContext getContext(String name);
	 
	 /**
	  * Get the set of context predicates listed in the linguistic ontology
	  */
	 public Collection<SenseContext> getContexts();
	 
	 
	/**
	 * Get a definition predicate by its name 
	 * @throws IllegalArgumentException If the name is not recognised by the ontology
	 */
	 public Definition getDefinition(String name);
	 
	 /**
	  * Get the set of definition predicates listed in the linguistic ontology
	  */
	 public Collection<Definition> getDefinitions();
	
	 
	/**
	 * Get a edge relation type by its name 
	 * @throws IllegalArgumentException If the name is not recognised by the ontology
	 */
	 public Edge getEdge(String name);
	 
	 /**
	  * Get the set of edge relation types listed in the linguistic ontology
	  */
	 public Collection<Edge> getEdge();
	 
	 
	/**
	 * Get a form variant relation type by its name 
	 * @throws IllegalArgumentException If the name is not recognised by the ontology
	 */
	 public FormVariant getFormVariant(String name);
	 
	 /**
	  * Get the set of form variant relation types listed in the linguistic ontology
	  */
	 public Collection<FormVariant> getFormVariant();
	 
	 
	/**
	 * Get a lexical variant relation type by its name 
	 * @throws IllegalArgumentException If the name is not recognised by the ontology
	 */
	 public LexicalVariant getLexicalVariant(String name);
	 
	 /**
	  * Get the set of lexical variant relation types listed in the linguistic ontology
	  */
	 public Collection<LexicalVariant> getLexicalVariant();
	 
	  
	/**
	 * Get a representation relation type by its name 
	 * @throws IllegalArgumentException If the name is not recognised by the ontology
	 */
	 public Representation getRepresentation(String name);
	 
	 /**
	  * Get the set of representation relation types listed in the linguistic ontology
	  */
	 public Collection<Representation> getRepresentation();
	 
	  
	/**
	 * Get a sense relation type by its name 
	 * @throws IllegalArgumentException If the name is not recognised by the ontology
	 */
	 public SenseRelation getSenseRelation(String name);
	 
	 /**
	  * Get the set of sense relation types listed in the linguistic ontology
	  */
	 public Collection<SenseRelation> getSenseRelation();
	 
	 /**
	  * Get all possible frame classes
	  */
	 public Collection<URI> getFrameClasses();
	 
	 /**
	  * Get a frame class by name
	 * @throws IllegalArgumentException If the name is not recognised by the ontology
	  */
	 public URI getFrameClass(String name);
	 	 
         /**
          * Convenience method to get a property map
          */
         public Map<Property,Collection<PropertyValue>> getPropertyMap(String... prop);
         
         /**
          * Get any definitions of an element or predicate
          */
         public Collection<String> getDefinitions(LemonElementOrPredicate elem);
         /**
          * Get any examples of a frame
          */
         public Collection<Text> getExamples(URI frameClass);
}
