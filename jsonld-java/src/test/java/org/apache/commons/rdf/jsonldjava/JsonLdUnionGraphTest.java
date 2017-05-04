package org.apache.commons.rdf.jsonldjava;

import static org.junit.Assert.*;

import org.apache.commons.rdf.simple.IRIImpl;
import org.junit.Test;

public class JsonLdUnionGraphTest {
	IRIImpl subject = new IRIImpl("Gomba");
	IRIImpl predicate = new IRIImpl("Szereti");
	IRIImpl object = new IRIImpl("Eso");
	
	@Test
	public void containsTriple() {
		
		JsonLdUnionGraphImpl graph = new JsonLdUnionGraphImpl("ex");
		
		graph.add(subject, predicate, object);
		
		assertTrue(graph.contains(subject, predicate, object));
		
		graph.remove(subject, predicate, object);
		
		assertFalse(graph.contains(subject, predicate, object));
		
		graph.add(subject, predicate, object);
		graph.clear();
		
		assertFalse(graph.contains(subject, predicate, object));
		
		graph.close();
	}
}
