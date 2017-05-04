package org.apache.commons.rdf.jsonldjava;

import static org.junit.Assert.*;

import org.apache.commons.rdf.simple.IRIImpl;
import org.junit.Before;
import org.junit.Test;

public class JsonLdUnionGraphTest {
	IRIImpl subject;
	IRIImpl predicate;
	IRIImpl object;
	JsonLdUnionGraphImpl graph;
	
	@Before
	public void setup() {
		graph = new JsonLdUnionGraphImpl("ex");
		subject = new IRIImpl("Gomba");
		predicate = new IRIImpl("Szereti");
		object = new IRIImpl("Eső");
	}
	
	@Test
	public void add() {
		graph.add(subject, predicate, object);
		
		assertTrue(graph.contains(subject, predicate, object));
	}
	
	@Test
	public void remove() {
		graph.add(subject, predicate, object);
		graph.remove(subject, predicate, object);
		
		assertFalse(graph.contains(subject, predicate, object));
	}
	
	@Test
	public void clear() {
		for (int i = 1; i <= 10; i++) {
			graph.add(subject, predicate, object);
		}
		graph.clear();
		
		assertFalse(graph.contains(subject, predicate, object));
	}
}
