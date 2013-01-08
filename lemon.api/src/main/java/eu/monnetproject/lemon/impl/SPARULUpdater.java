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

import eu.monnetproject.lemon.RemoteUpdater;
import eu.monnetproject.lemon.SPARQL;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import static eu.monnetproject.lemon.SPARQL.*;
import java.net.HttpURLConnection;
import java.net.URLConnection;
import java.util.Collections;
import javax.net.ssl.HttpsURLConnection;

/**
 *
 * @author John McCrae
 */
public class SPARULUpdater implements RemoteUpdater {

    private static final String INSERT_INTO = "INSERT INTO";
    private static final String DELETE_FROM = "DELETE FROM";
    private final String url;
    private final URI graph;
    private final String username, password;
    private final SPARQL dialect;

    /**
     * Create a connection to a SPARUL endpoint with no authentication
     *
     * @param url The URL of the endpoint, must include query parameter, e.g.,
     * "http://localhost:8080/sparql-auth?query="
     */
    public SPARULUpdater(String url, URI graph, SPARQL dialect) {
        this.url = url;
        this.graph = graph;
        this.username = null;
        this.password = null;
        this.dialect = dialect;
    }

    /**
     * Create a connection to a SPARUL endpoint with authentication
     *
     * @param url The URL of the endpoint, must include query parameter, e.g.,
     * "http://localhost:8080/sparql-auth?query="
     * @param username The user name (or null for no authentication)
     * @param password The pass word (or null for no authentication)
     */
    public SPARULUpdater(String url, URI graph, String username, String password, SPARQL dialect) {
        this.url = url;
        this.graph = graph;
        this.username = username;
        this.password = password;
        this.dialect = dialect;
    }

    private void prepSPARULUpdate(String action, String subject, String predicate, String object) throws IOException {
        StringBuilder query = new StringBuilder();
        if(dialect == SPARUL) {
            query.append(action).append("<").append(graph.toString()).append("> { ");
        } else if(dialect == SPARQL11) {
            query.append("WITH <").append(graph.toString()).append("> ").append(action.substring(0, 6)).append(" {");
        } else {
            throw new UnsupportedOperationException("Unsupported SPARQL dialect " + dialect);
        }
        if (!subject.startsWith("_:") && !object.startsWith("_:")) {
            query.append(subject).append(" ");
            query.append(predicate).append(" ");
            query.append(object).append(" }");
        } else if(object.startsWith("_:")) {
            // Some endpoints do not support insertion of blank nodes by name
            // subject a URI, object a BNode
            query.append(subject).append(" ");
            query.append(predicate).append(" ");
            query.append("?o").append(" } where { filter(str(?o) =\"").append(object);
            query.append("\"). optional { ?o ?p2 ?s2 } . optional { ?s3 ?p3 ?o } }");
        } else if(subject.startsWith("_:")) {
            // object a URI, subject a BNode
            query.append("?s ");
            query.append(predicate).append(" ");
            query.append(object).append(" } where { filter(str(?s) =\"").append(subject);
            query.append("\"). optional { ?s ?p2 ?s2 } . optional { ?s3 ?p3 ?s } }");
        } else {
            // Both BNodes
            query.append("?s ");
            query.append(predicate).append(" ");
            query.append("?o } where { filter(str(?s) =\"").append(subject);
            query.append("\"). filter(str(?o) =\"").append(object);
            query.append("\"). optional { ?s2 ?p2 ?s } . optional { ?s ?p3 ?s3 } . optional { ?o ?p3 ?o3 } . optional { ?s4 ?p4 ?o } }");
        }
        doSPARULUpdate(query);
    }

    private void doSPARULUpdate(StringBuilder query) throws UnsupportedEncodingException, MalformedURLException, IOException {
        System.err.println(query.toString());
        if(dialect == SPARUL) {
            URL queryURL = new URL(url + URLEncoder.encode(query.toString(), "UTF-8"));
            final InputStream stream = HttpAuthenticate.connectAuthenticate(queryURL, username, password);
            // Yes, just read. We really don't care what the result is just that 
            // the response is 200 OK
            final byte[] buf = new byte[1024];
            stream.read(buf, 0, 1024);
        } else {
            final URL queryURL;
            final String param;
            if(url.contains("?")) {
                queryURL = new URL(url.substring(0,url.indexOf("?")));
                param = url.substring(url.indexOf("?")+1,url.length()-1);
            } else {
                queryURL = new URL(url);
                param = "update";
            }
            System.err.println(queryURL);
            System.err.println(param);
            
            final InputStream stream = HttpAuthenticate.postAuthenticate(queryURL, username, password, Collections.singletonMap(param, query.toString()));
            // Yes, just read. We really don't care what the result is just that 
            // the response is 200 OK
            final byte[] buf = new byte[1024];
            int read;
            while((read = stream.read(buf, 0, 1024)) != -1) {
                System.out.print(new String(buf,0,read));
            }
        }
    }

    @Override
    public void add(URI subject, URI predicate, URI object) {
        try {
            prepSPARULUpdate(INSERT_INTO, "<" + subject + ">", "<" + predicate + ">", "<" + object + ">");
        } catch (IOException x) {
            throw new RemoteUpdateException(x);
        }
    }

    @Override
    public void add(String subject, URI predicate, URI object) {
        try {
            prepSPARULUpdate(INSERT_INTO, "_:" + subject, "<" + predicate + ">", "<" + object + ">");
        } catch (IOException x) {
            throw new RemoteUpdateException(x);
        }
    }

    @Override
    public void add(URI subject, URI predicate, String bNode) {
        try {
            prepSPARULUpdate(INSERT_INTO, "<" + subject + ">", "<" + predicate + ">", "_:" + bNode);
        } catch (IOException x) {
            throw new RemoteUpdateException(x);
        }
    }

    @Override
    public void add(String subject, URI predicate, String bNode) {
        try {
            prepSPARULUpdate(INSERT_INTO, "_:" + subject, "<" + predicate + ">", "_:" + bNode);
        } catch (IOException x) {
            throw new RemoteUpdateException(x);
        }
    }

    private static String escapeLiteral(String s) {
        return s.replaceAll("\\\"", "\\\\\"").replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r");
    }

    @Override
    public void add(URI subject, URI predicate, String literal, String language) {
        try {
            prepSPARULUpdate(INSERT_INTO, "<" + subject + ">", "<" + predicate + ">", "\"" + escapeLiteral(literal) + "\"" + (language == null ? "" : "@" + language));
        } catch (IOException x) {
            throw new RemoteUpdateException(x);
        }
    }

    @Override
    public void add(String subject, URI predicate, String literal, String language) {
        try {
            prepSPARULUpdate(INSERT_INTO, "_:" + subject, "<" + predicate + ">", "\"" + escapeLiteral(literal) + "\"" + (language == null ? "" : "@" + language));
        } catch (IOException x) {
            throw new RemoteUpdateException(x);
        }
    }

    @Override
    public void add(URI subject, URI predicate, String literal, URI datatype) {
        try {
            prepSPARULUpdate(INSERT_INTO, "<" + subject + ">", "<" + predicate + ">", "\"" + escapeLiteral(literal) + "\"" + (datatype == null ? "" : "^^<" + datatype + ">"));
        } catch (IOException x) {
            throw new RemoteUpdateException(x);
        }
    }

    @Override
    public void add(String subject, URI predicate, String literal, URI datatype) {
        try {
            prepSPARULUpdate(INSERT_INTO, "_:" + subject, "<" + predicate + ">", "\"" + escapeLiteral(literal) + "\"" + (datatype == null ? "" : "^^<" + datatype + ">"));
        } catch (IOException x) {
            throw new RemoteUpdateException(x);
        }
    }

    @Override
    public void remove(URI subject, URI predicate, URI object) {
        try {
            prepSPARULUpdate(DELETE_FROM, "<" + subject + ">", "<" + predicate + ">", "<" + object + ">");
        } catch (IOException x) {
            throw new RemoteUpdateException(x);
        }
    }

    @Override
    public void remove(String subject, URI predicate, URI object) {
        try {
            prepSPARULUpdate(DELETE_FROM, "_:" + subject, "<" + predicate + ">", "<" + object + ">");
        } catch (IOException x) {
            throw new RemoteUpdateException(x);
        }
    }

    @Override
    public void remove(URI subject, URI predicate, String bNode) {
        try {
            prepSPARULUpdate(DELETE_FROM, "<" + subject + ">", "<" + predicate + ">", "_:" + bNode);
        } catch (IOException x) {
            throw new RemoteUpdateException(x);
        }
    }

    @Override
    public void remove(String subject, URI predicate, String bNode) {
        try {
            prepSPARULUpdate(DELETE_FROM, "_:" + subject, "<" + predicate + ">", "_:" + bNode);
        } catch (IOException x) {
            throw new RemoteUpdateException(x);
        }
    }

    @Override
    public void remove(URI subject, URI predicate, String literal, String language) {
        try {
            prepSPARULUpdate(DELETE_FROM, "<" + subject + ">", "<" + predicate + ">", "\"" + escapeLiteral(literal) + "\"" + (language == null ? "" : "@" + language));
        } catch (IOException x) {
            throw new RemoteUpdateException(x);
        }
    }

    @Override
    public void remove(String subject, URI predicate, String literal, String language) {
        try {
            prepSPARULUpdate(DELETE_FROM, "_:" + subject, "<" + predicate + ">", "\"" + escapeLiteral(literal) + "\"" + (language == null ? "" : "@" + language));
        } catch (IOException x) {
            throw new RemoteUpdateException(x);
        }
    }

    @Override
    public void remove(URI subject, URI predicate, String literal, URI datatype) {
        try {
            prepSPARULUpdate(DELETE_FROM, "<" + subject + ">", "<" + predicate + ">", "\"" + escapeLiteral(literal) + "\"" + (datatype == null ? "" : "^^<" + datatype + ">"));
        } catch (IOException x) {
            throw new RemoteUpdateException(x);
        }
    }

    @Override
    public void remove(String subject, URI predicate, String literal, URI datatype) {
        try {
            prepSPARULUpdate(DELETE_FROM, "_:" + subject, "<" + predicate + ">", "\"" + escapeLiteral(literal) + "\"" + (datatype == null ? "" : "^^<" + datatype + ">"));
        } catch (IOException x) {
            throw new RemoteUpdateException(x);
        }
    }

    @Override
    public void addList(String subject, URI predicate, List<Object> list) {
        StringBuilder query = new StringBuilder("PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> ");
        if(dialect == SPARUL) {
            query.append(INSERT_INTO).append(" <").append(graph).append("> { ");
        } else if(dialect == SPARQL11) {
            query.append("WITH <").append(graph).append("> INSERT {");
        } else {
            throw new UnsupportedOperationException("Unsupported SPARQL dialect " + dialect);
        }
        query.append("_:").append(subject).append(" ");
        query.append("<").append(predicate.toString()).append(">  ( ");
        for (Object o : list) {
            if (o instanceof URI) {
                query.append("<").append(o.toString()).append("> ");
            } else if (o instanceof String) {
                query.append("_:").append(o.toString()).append(" ");
            } else {
                throw new IllegalArgumentException("List contained element not a URI or String");
            }
        }
        query.append(")");
        try {
            doSPARULUpdate(query);
        } catch (IOException x) {
            throw new RemoteUpdateException(x);
        }
    }

    @Override
    public void addList(URI subject, URI predicate, List<Object> list) {
        StringBuilder query = new StringBuilder("PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> ");
        if(dialect == SPARUL) {
            query.append(INSERT_INTO).append(" <").append(graph).append("> { ");
        } else if(dialect == SPARQL11) {
            query.append("WITH <").append(graph).append("> INSERT {");
        } else {
            throw new UnsupportedOperationException("Unsupported SPARQL dialect " + dialect);
        }
        query.append("<").append(subject).append("> ");
        query.append("<").append(predicate.toString()).append(">  ( ");
        for (Object o : list) {
            if (o instanceof URI) {
                query.append("<").append(o.toString()).append("> ");
            } else if (o instanceof String) {
                query.append("_:").append(o.toString()).append(" ");
            } else {
                throw new IllegalArgumentException("List contained element not a URI or String");
            }
        }
        query.append(")");
        try {
            doSPARULUpdate(query);
        } catch (IOException x) {
            throw new RemoteUpdateException(x);
        }
    }

    @Override
    public void removeList(String subject, URI predicate, List<Object> list) {
        int i = 0;
        StringBuilder query = new StringBuilder("PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> ");
        if(dialect == SPARUL) {
            query.append(DELETE_FROM).append(" <").append(graph).append("> { ");
        } else if(dialect == SPARQL11) {
            query.append("WITH <").append(graph).append("> DELETE {");
        } else {
            throw new UnsupportedOperationException("Unsupported SPARQL dialect " + dialect);
        }
        query.append("_:").append(subject).append(" ");
        query.append("<").append(predicate.toString()).append(">  ?n").append(i).append(" . ");
        for (Object o : list) {
            query.append("?n").append(i).append(" rdf:first ");
            if (o instanceof URI) {
                query.append("<").append(o.toString()).append("> . ");
            } else if (o instanceof String) {
                query.append("_:").append(o.toString()).append(" . ");
            } else {
                throw new IllegalArgumentException("List contained element not a URI or String");
            }
            query.append("?n").append(i).append(" rdf:rest ").append("?n").append(++i).append(" . ");
        }
        query.append("} WHERE {");
        i = 0;
        query.append("_:").append(subject).append(" ");
        query.append("<").append(predicate.toString()).append(">  ?n").append(i).append(" . ");
        for (Object o : list) {
            query.append("?n").append(i).append(" rdf:first ");
            if (o instanceof URI) {
                query.append("<").append(o.toString()).append("> . ");
            } else if (o instanceof String) {
                query.append("_:").append(o.toString()).append(" . ");
            } else {
                throw new IllegalArgumentException("List contained element not a URI or String");
            }
            query.append("?n").append(i).append(" rdf:rest ").append("?n").append(++i).append(" . ");
        }
        query.append(" }");
        try {
            doSPARULUpdate(query);
        } catch (IOException x) {
            throw new RemoteUpdateException(x);
        }
    }

    @Override
    public void removeList(URI subject, URI predicate, List<Object> list) {

        int i = 0;
        StringBuilder query = new StringBuilder("PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> ");
        if(dialect == SPARUL) {
            query.append(DELETE_FROM).append(" <").append(graph).append("> { ");
        } else if(dialect == SPARQL11) {
            query.append("WITH <").append(graph).append("> DELETE {");
        } else {
            throw new UnsupportedOperationException("Unsupported SPARQL dialect " + dialect);
        }
        query.append("<").append(subject).append("> ");
        query.append("<").append(predicate.toString()).append(">  ?n").append(i).append(" . ");
        for (Object o : list) {
            query.append("?n").append(i).append(" rdf:first ");
            if (o instanceof URI) {
                query.append("<").append(o.toString()).append("> . ");
            } else if (o instanceof String) {
                query.append("_:").append(o.toString()).append(" . ");
            } else {
                throw new IllegalArgumentException("List contained element not a URI or String");
            }
            query.append("?n").append(i).append(" rdf:rest ").append("?n").append(++i).append(" . ");
        }
        query.append("} WHERE {");
        i = 0;
        query.append("<").append(subject).append("> ");
        query.append("<").append(predicate.toString()).append(">  ?n").append(i).append(" . ");
        for (Object o : list) {
            query.append("?n").append(i).append(" rdf:first ");
            if (o instanceof URI) {
                query.append("<").append(o.toString()).append("> . ");
            } else if (o instanceof String) {
                query.append("_:").append(o.toString()).append(" . ");
            } else {
                throw new IllegalArgumentException("List contained element not a URI or String");
            }
            query.append("?n").append(i).append(" rdf:rest ").append("?n").append(++i).append(" . ");
        }
        query.append(" }");
        try {
            doSPARULUpdate(query);
        } catch (IOException x) {
            throw new RemoteUpdateException(x);
        }
    }
}
