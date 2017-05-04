package org.apache.commons.rdf.rdf4j;

import static org.junit.Assert.*;

import java.io.IOException;

import org.apache.commons.rdf.api.RDFSyntax;
import org.apache.commons.rdf.rdf4j.experimental.RDF4JParser;
import org.apache.commons.rdf.simple.IRIImpl;
import org.junit.Test;

public class RDF4JParserTest {

	@Test
	public void parseException() throws IllegalStateException, IOException {
		RDF4JParser parser = new RDF4JParser();
		parser.contentType(RDFSyntax.TURTLE);
		try {
			parser.parse();
		}

		catch (IllegalStateException exception) {
			assert (true);
			return;
		}
		assert (false);
	}

	@Test
	public void equality() {
		RDF4JParser lhs = new RDF4JParser();
		lhs.source(System.in);
		lhs.contentType(RDFSyntax.TURTLE);
		lhs.rdfTermFactory(new RDF4J());

		RDF4JParser rhs = lhs.clone();

		assertEquals(lhs.getBase(), rhs.getBase());
		assertEquals(lhs.getContentType(), rhs.getContentType());
		assertEquals(lhs.getParserConfig(), rhs.getParserConfig());
		assertEquals(lhs.getRdfTermFactory(), rhs.getRdfTermFactory());
		assertEquals(lhs.getSourceIri(), rhs.getSourceIri());
	}

}
