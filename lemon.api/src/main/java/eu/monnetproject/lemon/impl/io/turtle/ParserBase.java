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
package eu.monnetproject.lemon.impl.io.turtle;

import eu.monnetproject.lemon.impl.LemonModelImpl;
import eu.monnetproject.lemon.impl.io.ReaderVisitor;
import eu.monnetproject.lemon.impl.io.Visitor;
import java.net.URI;
import java.util.HashMap;

/**
 *
 * @author John McCrae
 */
public class ParserBase {
    // Should be the same as ARQ ParserBase and Prologues.

    protected final Node XSD_TRUE = createLiteral("true", null, "http://www.w3.org/2001/XMLSchema#boolean");
    protected final Node XSD_FALSE = createLiteral("false", null, "http://www.w3.org/2001/XMLSchema#boolean");
    protected final Node nRDFtype = createNode("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
    protected final Node nRDFfirst = createNode("http://www.w3.org/1999/02/22-rdf-syntax-ns#first");
    protected final Node nRDFrest = createNode("http://www.w3.org/1999/02/22-rdf-syntax-ns#rest");
    protected final Node nRDFnil = createNode("http://www.w3.org/1999/02/22-rdf-syntax-ns#nil");
    protected final Node nOwlSameAs = createNode("http://www.w3.org/2002/07/owl#sameAs");
    protected final Node nLogImplies = createNode("http://www.w3.org/2000/10/swap/log#implies");
    protected final String SWAP_NS = "http://www.w3.org/2000/10/swap/";
    protected final String SWAP_LOG_NS = "http://www.w3.org/2000/10/swap/log#";
    protected boolean strictTurtle = true;
    protected boolean skolomizedBNodes = true;
    
    
    private final Visitor defaultAccepter;

    public ParserBase(LemonModelImpl model) {
        defaultAccepter = new ReaderVisitor(model);
    }
    
    public ParserBase(Visitor visitor) {
        defaultAccepter = visitor;
    }

    private final HashMap<String,String> resolver = new HashMap<String,String>();
    
    protected String getBaseURI() {
        return resolver.get("");
    }

    public void setBaseURI(String u) {
        resolver.put("", u);
    }

    protected void setBase(String iriStr, int line, int column) {
        // Already resolved.
        setBaseURI(iriStr);
    }

    protected void emitTriple(int line, int col, Triple triple) {
        if(triple.s instanceof IRI) {
            if(triple.o instanceof IRI) {
                defaultAccepter.accept(URI.create(triple.s.getURI()), URI.create(triple.p.getURI()), URI.create(triple.o.getURI()));
            } else if(triple.o instanceof BNode) {
                defaultAccepter.accept(URI.create(triple.s.getURI()), URI.create(triple.p.getURI()), ((BNode)triple.o).id);
            } else {
                Literal lit = (Literal)triple.o;
                defaultAccepter.accept(URI.create(triple.s.getURI()), URI.create(triple.p.getURI()), lit.getLabel(), lit.getLanguage());
            }
        } else {
            if(triple.o instanceof IRI) {
                defaultAccepter.accept(((BNode)triple.s).getId(), URI.create(triple.p.getURI()), URI.create(triple.o.getURI()));
            } else if(triple.o instanceof BNode) {
                defaultAccepter.accept(((BNode)triple.s).getId(), URI.create(triple.p.getURI()), ((BNode)triple.o).id);
            } else {
                Literal lit = (Literal)triple.o;
                defaultAccepter.accept(((BNode)triple.s).getId(), URI.create(triple.p.getURI()), lit.getLabel(), lit.getLanguage());
            }
        }
    }

    protected void startFormula(int line, int col) {
        //handler.startFormula(line, col);
    }

    protected void endFormula(int line, int col) {
        //handler.endFormula(line, col);
    }

    protected void setPrefix(int line, int col, String prefix, String uri) {
        resolver.put(prefix, uri);
    }

    protected int makePositiveInteger(String lexicalForm) {
        if (lexicalForm == null) {
            return -1;
        }

        return Integer.parseInt(lexicalForm);
    }

    protected Node createLiteralInteger(String lexicalForm) {
        return createLiteral(lexicalForm, null, "http://www.w3.org/2001/XMLSchema#integer");
    }

    protected Node createLiteralDouble(String lexicalForm) {
        return createLiteral(lexicalForm, null, "http://www.w3.org/2001/XMLSchema#double");
    }

    protected Node createLiteralDecimal(String lexicalForm) {
        return createLiteral(lexicalForm, null, "http://www.w3.org/2001/XMLSchema#decimal");
    }

    protected Node createLiteral(String lexicalForm, String langTag, Node datatype) {
        String uri = (datatype == null) ? null : datatype.getURI();
        return createLiteral(lexicalForm, langTag, uri);
    }

    protected Node createLiteral(String lexicalForm, String langTag, String datatypeURI) {
        Node n = null;
        // Can't have type and lang tag.
        if (datatypeURI != null) {
            n = new Literal(lexicalForm, null, datatypeURI);
        } else {
            n = new Literal(lexicalForm, langTag, null);
        }
        return n;
    }

    protected long integerValue(String s) {
        if (s.startsWith("+")) {
            s = s.substring(1);
        }
        if (s.startsWith("0x")) {
            // Hex
            s = s.substring(2);
            return Long.parseLong(s, 16);
        }
        return Long.parseLong(s);
    }

    protected double doubleValue(String s) {
        if (s.startsWith("+")) {
            s = s.substring(1);
        }
        double valDouble = Double.parseDouble(s);
        return valDouble;
    }

    protected String stripQuotes(String s) {
        return s.substring(1, s.length() - 1);
    }

    protected String stripQuotes3(String s) {
        return s.substring(3, s.length() - 3);
    }

    protected String stripChars(String s, int n) {
        return s.substring(n, s.length());
    }

    protected String resolveQuotedIRI(String iriStr, int line, int column) {
        iriStr = stripQuotes(iriStr);
        return resolveIRI(iriStr, line, column);
    }

    protected String resolveIRI(String iriStr, int line, int column) {
        if (isBNodeIRI(iriStr)) {
            return iriStr;
        }

        if (resolver != null) {
            iriStr = _resolveIRI(iriStr, line, column);
        }
        return iriStr;
    }

    private String _resolveIRI(String iriStr, int line, int column) {
//            iriStr = resolver.resolve(iriStr);
        return iriStr;
    }

    protected String resolvePName(String qname, int line, int column) {
        final String[] s = qname.split(":");
        if(s.length != 2 || !resolver.containsKey(s[0])) {
            throwParseException("Unresolved prefixed name: " + qname, line, column);
        }
        return resolver.get(s[0]) + s[1];
    }
    final static String bNodeLabelStart = "_:";

    protected Node createListNode() {
        return createBNode();
    }

    // Unlabelled bNode.
    protected Node createBNode() {
        return new BNode();
    }

    //  Labelled bNode.
    protected Node createBNode(String label, int line, int column) {
        return new BNode(label);
    }

    protected Node createVariable(String s, int line, int column) {
        throwParseException("Variables are not supported by lemon API", line, column);
        return null;
    }

    protected Node createNode(String iri) {
        // Is it a bNode label? i.e. <_:xyz>
        if (isBNodeIRI(iri)) {
            String s = iri.substring(bNodeLabelStart.length());
            Node n = new BNode();
            return n;
        }
        return new IRI(iri);
    }

    protected boolean isBNodeIRI(String iri) {
        return skolomizedBNodes && iri.startsWith(bNodeLabelStart);
    }

    protected void throwParseException(String s, int line, int column) {
        throw new TurtleParseException(exMsg(s, line, column));
    }

    protected String fixupPrefix(String prefix, int line, int column) {
        if (prefix.endsWith(":")) {
            prefix = prefix.substring(0, prefix.length() - 1);
        }
        return prefix;
    }

    // Utilities to remove escapes
    // Testing interface
    public static String unescapeStr(String s) {
        return unescape(s, '\\', false, 1, 1);
    }

    protected String unescapeStr(String s, int line, int column) {
        return unescape(s, '\\', false, line, column);
    }

    // Worker function
    private static String unescape(String s, char escape, boolean pointCodeOnly, int line, int column) {
        int i = s.indexOf(escape);

        if (i == -1) {
            return s;
        }

        // Dump the initial part straight into the string buffer
        StringBuffer sb = new StringBuffer(s.substring(0, i));
        int len = s.length();
        for (; i < len; i++) {
            char ch = s.charAt(i);
            // Keep line and column numbers.
            switch (ch) {
                case '\n':
                case '\r':
                    line++;
                    column = 1;
                    break;
                default:
                    column++;
                    break;
            }

            if (ch != escape) {
                sb.append(ch);
                continue;
            }

            // Escape
            if (i >= len - 1) {
                throw new TurtleParseException(exMsg("Illegal escape at end of string", line, column));
            }
            char ch2 = s.charAt(i + 1);
            column = column + 1;
            i = i + 1;

            // \\u and \\U
            if (ch2 == 'u') {
                // i points to the \ so i+6 is next character
                if (i + 4 >= len) {
                    throw new TurtleParseException(exMsg("\\u escape too short", line, column));
                }
                int x = hex(s, i + 1, 4, line, column);
                sb.append((char) x);
                // Jump 1 2 3 4 -- already skipped \ and u
                i = i + 4;
                column = column + 4;
                continue;
            }
            if (ch2 == 'U') {
                // i points to the \ so i+6 is next character
                if (i + 8 >= len) {
                    throw new TurtleParseException(exMsg("\\U escape too short", line, column));
                }
                int x = hex(s, i + 1, 8, line, column);
                sb.append((char) x);
                // Jump 1 2 3 4 5 6 7 8 -- already skipped \ and u
                i = i + 8;
                column = column + 8;
                continue;
            }

            // Are we doing just point code escapes?
            // If so, \X-anything else is legal as a literal "\" and "X" 

            if (pointCodeOnly) {
                sb.append('\\');
                sb.append(ch2);
                i = i + 1;
                continue;
            }

            // Not just codepoints.  Must be a legal escape.
            char ch3 = 0;
            switch (ch2) {
                case 'n':
                    ch3 = '\n';
                    break;
                case 't':
                    ch3 = '\t';
                    break;
                case 'r':
                    ch3 = '\r';
                    break;
                case 'b':
                    ch3 = '\b';
                    break;
                case 'f':
                    ch3 = '\f';
                    break;
                case '\'':
                    ch3 = '\'';
                    break;
                case '\"':
                    ch3 = '\"';
                    break;
                case '\\':
                    ch3 = '\\';
                    break;
                default:
                    throw new TurtleParseException(exMsg("Unknown escape: \\" + ch2, line, column));
            }
            sb.append(ch3);
        }
        return sb.toString();
    }

    // Line and column that started the escape
    static private int hex(String s, int i, int len, int line, int column) {
//        if ( i+len >= s.length() )
//        {
//            
//        }
        int x = 0;
        for (int j = i; j < i + len; j++) {
            char ch = s.charAt(j);
            column++;
            int k = 0;
            switch (ch) {
                case '0':
                    k = 0;
                    break;
                case '1':
                    k = 1;
                    break;
                case '2':
                    k = 2;
                    break;
                case '3':
                    k = 3;
                    break;
                case '4':
                    k = 4;
                    break;
                case '5':
                    k = 5;
                    break;
                case '6':
                    k = 6;
                    break;
                case '7':
                    k = 7;
                    break;
                case '8':
                    k = 8;
                    break;
                case '9':
                    k = 9;
                    break;
                case 'A':
                case 'a':
                    k = 10;
                    break;
                case 'B':
                case 'b':
                    k = 11;
                    break;
                case 'C':
                case 'c':
                    k = 12;
                    break;
                case 'D':
                case 'd':
                    k = 13;
                    break;
                case 'E':
                case 'e':
                    k = 14;
                    break;
                case 'F':
                case 'f':
                    k = 15;
                    break;
                default:
                    throw new TurtleParseException(exMsg("Illegal hex escape: " + ch, line, column));
            }
            x = (x << 4) + k;
        }
        return x;
    }

    protected static String exMsg(String msg, int line, int column) {
        return "Line " + line + ", column " + column + ": " + msg;
    }}
