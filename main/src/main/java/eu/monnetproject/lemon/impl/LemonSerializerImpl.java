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

import eu.monnetproject.lemon.*;
import eu.monnetproject.lemon.impl.io.ReaderVisitor;
import eu.monnetproject.lemon.impl.io.turtle.TurtleParser;
import eu.monnetproject.lemon.impl.io.turtle.TurtleWriter;
import eu.monnetproject.lemon.impl.io.xml.RDFXMLReader;
import eu.monnetproject.lemon.impl.io.xml.RDFXMLWriter;
import eu.monnetproject.lemon.model.LexicalEntry;
import eu.monnetproject.lemon.model.Lexicon;
import java.io.*;
import java.net.URI;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import net.lexinfo.LexInfo;

/**
 * A serializer for in-memory lemon models
 *
 * @author John McCrae
 */
public class LemonSerializerImpl extends LemonSerializer {

    private final LinguisticOntology lingOnto;
    private boolean isClosed = false;
    private boolean ignoreErrors = false;

    /**
     * DO NOT USE!. Obtain a serializer by {@code LemonSerializer.newInstance()}
     *
     * @param lingOnto
     */
    public LemonSerializerImpl(LinguisticOntology lingOnto) {
        this.lingOnto = lingOnto == null ? new LexInfo() : lingOnto;
    }

    @Override
    public LemonModel read(Reader source) {
        if (isClosed) {
            throw new LemonRepositoryAlreadyClosedException();
        }
        final LemonModelImpl model = new LemonModelImpl(remoteUpdateFactory);
        read(model, source);
        return model;
    }

    @Override
    public void write(LemonModel model, Writer target) {
        if (isClosed) {
            throw new LemonRepositoryAlreadyClosedException();
        }
        try {
            final RDFXMLWriter xmlWriter = new RDFXMLWriter(lingOnto, Lexicon.class);
            for (Lexicon lexicon : model.getLexica()) {
                ((LexiconImpl) lexicon).accept(xmlWriter);
            }
            target.write(xmlWriter.getDocument());
            target.flush();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public LemonModel create() {
        if (isClosed) {
            throw new LemonRepositoryAlreadyClosedException();
        }
        return new LemonModelImpl(remoteUpdateFactory);
    }

    @Override
    @Deprecated
    public LemonModel create(URI context) {
        if (isClosed) {
            throw new LemonRepositoryAlreadyClosedException();
        }
        return new LemonModelImpl(context != null ? context.toString() : "unknown:lexicon", remoteUpdateFactory);
    }

    @Override
    public void writeEntry(LemonModel model, LexicalEntry entry, LinguisticOntology lingOnto,
            Writer target) {
        if (isClosed) {
            throw new LemonRepositoryAlreadyClosedException();
        }
        try {
            final RDFXMLWriter visitor = new RDFXMLWriter(lingOnto, LexicalEntry.class);
            if (entry instanceof LexicalEntryImpl) {
                final LexicalEntryImpl entryImpl = (LexicalEntryImpl) entry;
                entryImpl.getCanonicalForm(); // Force remote resolve
                entryImpl.accept(visitor);
            } else {
                throw new IllegalArgumentException("Cannot write model I didn't create");
            }
            target.append(visitor.getDocument());
            target.flush();
        } catch (IOException x) {
            throw new RuntimeException(x);
        } catch (ParserConfigurationException x) {
            throw new RuntimeException(x);
        } catch (TransformerException x) {
            throw new RuntimeException(x);
        }
    }

    @Override
    public void writeLexicon(LemonModel model, Lexicon lexicon, LinguisticOntology lingOnto,
            Writer target) {
        if (isClosed) {
            throw new LemonRepositoryAlreadyClosedException();
        }

        try {
            final RDFXMLWriter visitor = new RDFXMLWriter(lingOnto, Lexicon.class);
            System.err.println(">>>>>>>Writing lexicon");
            for (LexicalEntry entry : lexicon.getEntrys()) {
                System.err.println(entry.getURI().toString());
                entry.getCanonicalForm(); // force remote resolve
            }
            if (lexicon instanceof LexiconImpl) {
                ((LexiconImpl) lexicon).accept(visitor);
            } else {
                throw new IllegalArgumentException("Cannot write model I didn't create");
            }
            target.append(visitor.getDocument());
            target.flush();
        } catch (IOException x) {
            throw new RuntimeException(x);
        } catch (ParserConfigurationException x) {
            throw new RuntimeException(x);
        } catch (TransformerException x) {
            throw new RuntimeException(x);
        }
    }
    

    @Override
    public void moveLexicon(Lexicon lexicon, LemonModel from, LemonModel to) {
        if (isClosed) {
            throw new LemonRepositoryAlreadyClosedException();
        }
        if (lexicon instanceof LexiconImpl) {
            final CopyVisitor copyVisitor = new CopyVisitor(lingOnto, (LemonModelImpl) to);
            ((LexiconImpl) lexicon).accept(copyVisitor);
        } else {
            throw new IllegalArgumentException("moveLexicon has to be called by the serializer that created the from lexicon");
        }
    }

    @Override
    public void read(LemonModel lm, Reader source) {
        if (isClosed) {
            throw new LemonRepositoryAlreadyClosedException();
        }
        StringBuilder sb = new StringBuilder();
        try {
            char[] buf = new char[1024];
            int s;
            while ((s = source.read(buf)) != -1) {
                sb.append(buf, 0, s);
            }
        } catch (IOException x) {
            throw new RuntimeException(x);
        }
        source = new BufferedReader(new StringReader(sb.toString()));
        if (!(lm instanceof LemonModelImpl)) {
            throw new IllegalArgumentException("Lemon Model not created by this serializer");
        }
        final LemonModelImpl model = (LemonModelImpl) lm;
        final RDFXMLReader rdfXMLReader = new RDFXMLReader(model,ignoreErrors);
        try {
            rdfXMLReader.parse(source);
        } catch (Exception ex) {
            try {
                source = new BufferedReader(new StringReader(sb.toString()));
                final TurtleParser parser = new TurtleParser(source, model, ignoreErrors);
                parser.parse();
            } catch (Exception ex2) {
                ex.printStackTrace();
                throw new RuntimeException(ex2);
            }
        }
    }

    @Override
    public LexicalEntry readEntry(Reader source) {
        if (isClosed) {
            throw new LemonRepositoryAlreadyClosedException();
        }
        final LemonModelImpl lm = new LemonModelImpl(remoteUpdateFactory);
        StringBuilder sb = new StringBuilder();
        try {
            char[] buf = new char[1024];
            int s;
            while ((s = source.read(buf)) != -1) {
                sb.append(buf, 0, s);
            }
        } catch (IOException x) {
            throw new RuntimeException(x);
        }
        source = new BufferedReader(new StringReader(sb.toString()));
        if (!(lm instanceof LemonModelImpl)) {
            throw new IllegalArgumentException("Lemon Model not created by this serializer");
        }
        final ReaderVisitor model = new ReaderVisitor(lm, ignoreErrors);
        final RDFXMLReader rdfXMLReader = new RDFXMLReader(model);
        try {
            rdfXMLReader.parse(source);
        } catch (Exception ex) {
            try {
                source = new BufferedReader(new StringReader(sb.toString()));
                final TurtleParser parser = new TurtleParser(source, model);
                parser.parse();
            } catch (Exception ex2) {
                ex.printStackTrace();
                throw new RuntimeException(ex2);
            }
        }
        return model.getEntry();
    }

    @Override
    public void write(LemonModel lm, Writer dt, boolean xml) {
        if (isClosed) {
            throw new LemonRepositoryAlreadyClosedException();
        }
        if (xml) {
            write(lm, dt);
        } else {

            try {
                final TurtleWriter turtleWriter = new TurtleWriter(lingOnto);
                for (Lexicon lexicon : lm.getLexica()) {
                    ((LexiconImpl) lexicon).accept(turtleWriter);
                }
                dt.write(turtleWriter.getDocument());
            } catch (Exception ex) {
                if (ex instanceof RuntimeException) {
                    throw (RuntimeException) ex;
                }
                throw new RuntimeException(ex);
            }
        }
    }

    @Override
    public void writeEntry(LemonModel lm, LexicalEntry le, LinguisticOntology lo, Writer dt, boolean xml) {
        if (isClosed) {
            throw new LemonRepositoryAlreadyClosedException();
        }
        if (xml) {
            writeEntry(lm, le, lo, dt);
        } else {
            try {
                final TurtleWriter turtleWriter = new TurtleWriter(lingOnto);
                le.getForms(); // resolve remote
                ((LexicalEntryImpl) le).accept(turtleWriter);
                dt.write(turtleWriter.getDocument());
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    @Override
    public void writeLexicon(LemonModel lm, Lexicon lxcn, LinguisticOntology lo, Writer dt, boolean xml) {
        if (isClosed) {
            throw new LemonRepositoryAlreadyClosedException();
        }
        if (xml) {
            writeLexicon(lm, lxcn, lo, dt);
        } else {

            try {
                for (LexicalEntry entry : lxcn.getEntrys()) {
                    entry.getCanonicalForm(); // force remote resolve
                }
                final TurtleWriter turtleWriter = new TurtleWriter(lingOnto);
                ((LexiconImpl) lxcn).accept(turtleWriter);
                dt.write(turtleWriter.getDocument());
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    @Override
    public void close() {
        isClosed = true;
    }

    @Override
    public void setIgnoreModelErrors(boolean ignoreErrors) {
        this.ignoreErrors = ignoreErrors;
    }

    @Override
    public boolean ignoreModelErrors() {
        return ignoreErrors;
    }
    
    
}
