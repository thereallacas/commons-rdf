/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.rdf.jena;

import static org.junit.Assert.*;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.datatypes.xsd.XSDDatatype;


import org.apache.commons.rdf.api.AbstractRDFTest;
import org.apache.commons.rdf.api.RDF;
import org.apache.commons.rdf.api.IRI;
import org.apache.commons.rdf.api.Literal;
import org.apache.commons.rdf.api.BlankNode;
import org.apache.commons.rdf.api.Triple;

import org.apache.commons.rdf.simple.SimpleRDF;

import org.junit.Test;
import org.junit.Before;

public class JenaRDFTest extends AbstractRDFTest {

    JenaRDF jena;
    SimpleRDF rdf;

    @Override
    public RDF createFactory() {
        return new JenaRDF();
    }

    @Before
    public void setup()
    {
        jena = new JenaRDF();
        rdf = new SimpleRDF();
    }

    @Test
    public void asRDFTerm_nullNode() {
        assertNull(JenaRDF.asRDFTerm(jena, null));
    }

    @Test
    public void asRDFTerm_uri() {
        Node uri = NodeFactory.createURI("http://example.com/ex");
        IRI expectedIRI = jena.createIRI(uri.getURI());
        assertEquals(expectedIRI, JenaRDF.asRDFTerm(rdf, uri));
        assertEquals(expectedIRI, JenaRDF.asRDFTerm(jena, uri));
    }

    @Test
    public void asRDFTerm_blank() {
        Node blank = NodeFactory.createBlankNode();
        assertTrue(JenaRDF.asRDFTerm(rdf, blank) instanceof BlankNode);
        assertTrue(JenaRDF.asRDFTerm(jena, blank) instanceof BlankNode);
    }

    @Test
    public void asRDFTerm_literal() {
        Node literal = NodeFactory.createLiteral("Hello", "en");
        Literal expectedLiteral = jena.createLiteral(literal.getLiteralLexicalForm(), "en");
        assertEquals(expectedLiteral, JenaRDF.asRDFTerm(rdf, literal));
        assertEquals(expectedLiteral, JenaRDF.asRDFTerm(jena, literal));
    }

    @Test
    public void asRDFTerm_literal_emptyLang() {
        Node literal = NodeFactory.createLiteral("Hello", "");
        Literal expectedLiteral = jena.createLiteral(literal.getLiteralLexicalForm(), "");
        assertEquals(expectedLiteral, JenaRDF.asRDFTerm(rdf, literal));
        assertEquals(expectedLiteral, JenaRDF.asRDFTerm(jena, literal));
    }

    @Test
    public void asRDFTerm_literal_int() {
        Node literal = NodeFactory.createLiteral("42", XSDDatatype.XSDint);
        Literal actualLiteralSimple = (Literal) JenaRDF.asRDFTerm(rdf, literal);
        Literal actualLiteralJena = (Literal) JenaRDF.asRDFTerm(jena, literal);
        Literal expectedLiteral = jena.createLiteral(literal.getLiteralLexicalForm(), XSDDatatype.XSDint.getURI());
        assertEquals(expectedLiteral.getLexicalForm(), actualLiteralSimple.getLexicalForm());
        assertEquals(expectedLiteral.getLexicalForm(), actualLiteralJena.getLexicalForm());
    }

    @Test(expected=ConversionException.class)
    public void asRDFTerm_variable() {
        Node variable = NodeFactory.createVariable("foo");
        JenaRDF.asRDFTerm(rdf, variable);
    }

    @Test
    public void asTriple() {
        Node s = NodeFactory.createURI("http://example.com/subj");
        Node p = NodeFactory.createURI("http://example.com/pred");
        Node o = NodeFactory.createURI("http://example.com/obj");
        org.apache.jena.graph.Triple jt = org.apache.jena.graph.Triple.create(s, p, o);

        Triple t = JenaRDF.asTriple(jena, jt);
        Triple st = JenaRDF.asTriple(rdf, jt);

        assertEquals(jena.createIRI("http://example.com/subj"), t.getSubject());
        assertEquals(jena.createIRI("http://example.com/pred"), t.getPredicate());
        assertEquals(jena.createIRI("http://example.com/obj"), t.getObject());

        assertEquals(jena.createIRI("http://example.com/subj"), st.getSubject());
        assertEquals(jena.createIRI("http://example.com/pred"), st.getPredicate());
        assertEquals(jena.createIRI("http://example.com/obj"), st.getObject());
    }

    @Test(expected=ConversionException.class)
    public void asTriple_badSubject() {
        Node s = NodeFactory.createLiteral("hello");
        Node p = NodeFactory.createURI("http://example.com/pred");
        Node o = NodeFactory.createURI("http://example.com/obj");
        org.apache.jena.graph.Triple jt = org.apache.jena.graph.Triple.create(s, p, o);

        JenaRDF.asTriple(rdf, jt);
    }
}
