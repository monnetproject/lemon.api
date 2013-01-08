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
package eu.monnetproject.lemon;

import eu.monnetproject.lemon.impl.HttpResolver;
import eu.monnetproject.lemon.impl.LemonModelImpl;
import eu.monnetproject.lemon.impl.LemonSerializerImpl;
import eu.monnetproject.lemon.model.LexicalEntry;
import eu.monnetproject.lemon.model.LexicalTopic;
import eu.monnetproject.lemon.model.Lexicon;
import eu.monnetproject.lemon.model.MorphPattern;
import eu.monnetproject.lemon.model.Property;
import eu.monnetproject.lemon.model.PropertyValue;
import eu.monnetproject.lemon.model.Text;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.net.URI;
import java.net.URL;
import java.util.Collection;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Interface for I/O on lemon models
 *
 * @author John McCrae
 */
public abstract class LemonSerializer {

    /**
     * Read a lemon model from a given data source
     */
    public abstract LemonModel read(Reader source);

    /**
     * Read a lemon model putting the data in a given model
     */
    public abstract void read(LemonModel model, Reader source);

    /**
     * Read a single entry
     */
    public abstract LexicalEntry readEntry(Reader source);

    /**
     * Create a blank model.
     */
    public abstract LemonModel create();

    /**
     * Create a blank model
     *
     * @param graph The context of the model or null for no context
     * @deprecated Deprecated, use either {@code create()} if the context is
     * irrelevant or methods of particular implementations such as {@code LemonModels.sparqlEndpoint()}
     * to define a context.
     */
    @Deprecated
    public abstract LemonModel create(URI graph);

    /**
     * Write a lemon model to a given data source
     *
     * @param model The model to write
     * @param target The target to write to
     */
    public abstract void write(LemonModel model, Writer target);

    /**
     * Write a single entry to a file
     *
     * @param model The model to write from
     * @param entry The entry to write
     * @param lingOnto The linguistic ontology (necessary to avoid following to
     * other entries)
     * @param target The target to write to
     */
    public abstract void writeEntry(LemonModel model, LexicalEntry entry, LinguisticOntology lingOnto,
            Writer target);

    /**
     * Write a single lexicon to a file
     *
     * @param model The model to write from
     * @param lexicon The lexicon to write
     * @param lingOnto The linguistic ontology (necessary to avoid following to
     * other entries)
     * @param target The target to write to
     */
    public abstract void writeLexicon(LemonModel model, Lexicon lexicon, LinguisticOntology lingOnto,
            Writer target);
    private final String XMLnameStartChar = ":A-Z_a-z\u00C0-\u00D6\u00D8-\u00F6\u00F8-\u02FF\u0370-\u037D\u037F-\u1FFF\u200C-\u200D\u2070-\u218F\u2C00-\u2FEF\u3001-\uD7FF\uF900-\uFDCF\uFDF0-\uFFFD";
    private final String XMLnameChar = "\\-.0-9\u00B7\u0300-\u036F\u0203F-\u2040";
    private final Pattern extractXMLName = Pattern.compile("^(.*[^" + XMLnameStartChar + "])([" + XMLnameStartChar + "][" + XMLnameChar + "]*)$");

    private String escapeXMLLiteral(String s) {
        final StringBuilder sb = new StringBuilder(s);
        for (int i = 0; i < sb.length(); i++) {
            final char c = sb.charAt(i);
            if (c == '&') {
                sb.replace(i, i + 1, "&amp;");
            } else if (c == '<') {
                sb.replace(i, i + 1, "&lt;");
            } else if (c == '>') {
                sb.replace(i, i + 1, "&gt;");
            } else if (c > 127) {
                sb.replace(i, i + 1, "&#x" + Integer.toHexString(c) + ";");
            }
        }
        return sb.toString();
    }

    private static final String LS = System.getProperty("line.separator");
    
    /**
     * Write the description of a lexicon, i.e., only links to entries not the
     * entries' descriptions
     *
     * @param model The model
     * @param lexicon The lexicon
     * @param target The target to write to
     */
    public void writeLexiconDescription(LemonModel model, Lexicon lexicon, Writer target) {
        try {
            target.append("<?xml version=\"1.0\" encoding=\"US-ASCII\"?>"+LS
                    + "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">"+LS
                    + "  <lemon:Lexicon rdf:about=\"" + lexicon.getURI() + "\" xmlns:lemon=\"http://www.monnet-project.eu/lemon#\">"+LS);
            if(lexicon.getLanguage() != null) {
                target.append("    <lemon:language>" + lexicon.getLanguage()+"</lemon:language>"+LS);
            } 
            for (LexicalEntry entry : lexicon.getEntrys()) {
                if (entry.getURI() != null) {
                    target.append("    <lemon:entry rdf:resource=\"" + entry.getURI() + "\"/>"+LS);
                } else {
                    target.append("    <lemon:entry rdf:nodeID=\"" + entry.getID() + "\"/>"+LS);
                }
            }
            for (Map.Entry<URI, Collection<Object>> annoEntrys : lexicon.getAnnotations().entrySet()) {
                final Matcher matcher = extractXMLName.matcher(annoEntrys.getKey().toString());
                for (Object annoVal : annoEntrys.getValue()) {
                    if (matcher.matches()) {
                        target.append("    <p1:" + matcher.group(1) + " xmlns:p1=\"" + matcher.group(2) + "\"");
                    } else {
                        target.append("    <" + annoEntrys.getKey().toString());
                    }
                    if (annoVal instanceof URI) {
                        target.append(" rdf:resource=\"" + annoVal.toString() + "\"/>"+LS);
                    } else if (annoVal instanceof String) {
                        target.append(">" + escapeXMLLiteral(annoVal.toString()) + "</" + (matcher.matches() ? ("p1:" + matcher.group(1)) : (annoEntrys.getKey().toString())) + ">"+LS);
                    } else if (annoVal instanceof Text) {
                        final Text value = (Text) annoVal;
                        target.append(" xml:lang=\"" + value.language + "\">" + escapeXMLLiteral(value.value) + "</" + (matcher.matches() ? ("p1:" + matcher.group(1)) : (annoEntrys.getKey().toString())) + ">"+LS);
                    } else {
                        throw new RuntimeException("Unexpected annotation type " + annoVal.getClass().getName());
                    }
                }
            }
            for (Map.Entry<Property, Collection<PropertyValue>> propEntrys : lexicon.getPropertys().entrySet()) {
                final Matcher matcher = extractXMLName.matcher(propEntrys.getKey().toString());
                for (Object propVal : propEntrys.getValue()) {
                    if (matcher.matches()) {
                        target.append("    <p1:" + matcher.group(1) + " xmlns:p1=\"" + matcher.group(2) + "\"");
                    } else {
                        target.append("    <" + propEntrys.getKey().toString());
                    }
                    if (propVal instanceof URI) {
                        target.append(" rdf:resource=\"" + propVal.toString() + "\"/>"+LS);
                    } else if (propVal instanceof String) {
                        target.append(">" + escapeXMLLiteral(propVal.toString()) + "</" + (matcher.matches() ? ("p1:" + matcher.group(1)) : (propEntrys.getKey().toString())) + ">"+LS);
                    } else if (propVal instanceof Text) {
                        final Text value = (Text) propVal;
                        target.append(" xml:lang=\"" + value.language + "\">" + escapeXMLLiteral(value.value) + "</" + (matcher.matches() ? ("p1:" + matcher.group(1)) : (propEntrys.getKey().toString())) + ">"+LS);
                    } else {
                        throw new RuntimeException("Unexpected annotation type " + propVal.getClass().getName());
                    }
                }
            }
            
            for(MorphPattern pattern : lexicon.getPatterns()) {
                if(pattern.getURI() != null) {
                    target.append("    <lemon:pattern rdf:resource=\"" + pattern.getURI() + "\"/>"+LS);
                } else {
                    target.append("    <lemon:pattern rdf:nodeID=\"" + pattern.getID() + "\"/>"+LS);
                }
            }
            
            for(LexicalTopic topic : lexicon.getTopics()) {
                if(topic.getURI() != null) {
                    target.append("    <lemon:topic rdf:resource=\"" + topic.getURI() + "\"/>"+LS);
                } else {
                    target.append("    <lemon:topic rdf:nodeID=\"" + topic.getID() + "\"/>"+LS);
                }
                
            }
            target.append("  </lemon:Lexicon>"+LS+"</rdf:RDF>");
        } catch (IOException x) {
            throw new RuntimeException(x);
        }
    }

    /**
     * Write a lemon model to a given data source
     *
     * @param model The model to write
     * @param target The target to write to
     * @param xml Write as XML?
     */
    public abstract void write(LemonModel model, Writer target, boolean xml);

    /**
     * Write a single entry to a file
     *
     * @param model The model to write from
     * @param entry The entry to write
     * @param lingOnto The linguistic ontology (necessary to avoid following to
     * other entries)
     * @param target The target to write to
     * @param xml Write as XML?
     */
    public abstract void writeEntry(LemonModel model, LexicalEntry entry, LinguisticOntology lingOnto,
            Writer target, boolean xml);

    /**
     * Write a single lexicon to a file
     *
     * @param model The model to write from
     * @param lexicon The lexicon to write
     * @param lingOnto The linguistic ontology (necessary to avoid following to
     * other entries)
     * @param target The target to write to
     * @param xml Write as XML?
     */
    public abstract void writeLexicon(LemonModel model, Lexicon lexicon, LinguisticOntology lingOnto,
            Writer target, boolean xml);

    /**
     * Move a lexicon from one model to another. Note this may not work if the
     * models were created by different serializers
     *
     * @param lexicon The lexicon to move
     * @param from The source model containing the lexicon
     * @param to The target model
     */
    public abstract void moveLexicon(Lexicon lexicon, LemonModel from, LemonModel to);

    /**
     * Get a new instance of a lemon serializer. This will use LexInfo as the
     * linguistic ontology
     *
     * @return A lemon serializer
     */
    public final static LemonSerializer newInstance() {
        return new LemonSerializerImpl(null);
    }

    /**
     * Get a new instance of a lemon serializer
     *
     * @param lingOnto The linguistic ontology used in all models
     * @return A lemon serializer
     */
    public final static LemonSerializer newInstance(LinguisticOntology lingOnto) {
        return new LemonSerializerImpl(lingOnto);
    }

    /**
     * Get a model from the web starting from a given URL.
     *
     * @param url The URL of the lemon data
     * @return The lemon model
     * @throws IOException If an error occurred accessing the URL
     */
    public static LemonModel modelFromURL(URL url) throws IOException {
        final LemonModelImpl model = new LemonModelImpl(url.toString(), new HttpResolver(), null);
        new LemonSerializerImpl(null).read(model, new InputStreamReader(url.openConnection().getInputStream()));
        return model;
    }

    /**
     * Close the connection to the serializer.
     *
     */
    public abstract void close();
    protected RemoteUpdaterFactory remoteUpdateFactory;

    /**
     * Set the value of the remote updater factory used to copy changes to a
     * remote repository
     *
     * @param remoteUpdateFactory
     */
    public void setRemoteUpdateFactory(RemoteUpdaterFactory remoteUpdateFactory) {
        this.remoteUpdateFactory = remoteUpdateFactory;
    }
}
