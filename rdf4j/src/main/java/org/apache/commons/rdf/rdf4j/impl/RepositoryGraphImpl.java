/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
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
package org.apache.commons.rdf.rdf4j.impl;

import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

import org.apache.commons.rdf.api.BlankNodeOrIRI;
import org.apache.commons.rdf.api.Graph;
import org.apache.commons.rdf.api.IRI;
import org.apache.commons.rdf.api.RDFTerm;
import org.apache.commons.rdf.api.Triple;
import org.apache.commons.rdf.rdf4j.ClosableIterable;
import org.apache.commons.rdf.rdf4j.RDF4JBlankNodeOrIRI;
import org.apache.commons.rdf.rdf4j.RDF4JGraph;
import org.apache.commons.rdf.rdf4j.RDF4JTriple;
import org.eclipse.rdf4j.common.iteration.Iterations;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryResult;

class RepositoryGraphImpl extends AbstractRepositoryGraphLike<Triple> implements Graph, RDF4JGraph {

    private final Resource[] contextMask;

    RepositoryGraphImpl(Repository repository, UUID salt, boolean handleInitAndShutdown, boolean includeInferred,
            Resource... contextMask) {
        super(repository, salt, handleInitAndShutdown, includeInferred);
        this.contextMask = Objects.requireNonNull(contextMask);
    }

    @Override
    public void add(Triple tripleLike) {
        Statement statement = rdf4jTermFactory.asStatement(tripleLike);
        try (RepositoryConnection conn = getRepositoryConnection()) {
            conn.add(statement, contextMask);
            conn.commit();
        }
    }

    @Override
    public boolean contains(Triple tripleLike) {
        Statement statement = rdf4jTermFactory.asStatement(tripleLike);
        try (RepositoryConnection conn = getRepositoryConnection()) {
            return conn.hasStatement(statement, includeInferred, contextMask);
        }
    }

    @Override
    public void remove(Triple tripleLike) {
        Statement statement = rdf4jTermFactory.asStatement(tripleLike);
        try (RepositoryConnection conn = getRepositoryConnection()) {
            conn.remove(statement, contextMask);
            conn.commit();
        }
    }

    @Override
    public void clear() {
        try (RepositoryConnection conn = getRepositoryConnection()) {
            conn.clear(contextMask);
            conn.commit();
        }
    }

    @Override
    public long size() {
        if (!includeInferred && contextMask.length == 0) {
            try (RepositoryConnection conn = getRepositoryConnection()) {
                return conn.size();
            }
        } else {
            try (Stream<RDF4JTriple> stream = stream()) {
                return stream.count();
            }
        }
    }

    @Override
    public void add(BlankNodeOrIRI subject, IRI predicate, RDFTerm object) {
        Resource subj = (Resource) rdf4jTermFactory.asValue(subject);
        org.eclipse.rdf4j.model.IRI pred = (org.eclipse.rdf4j.model.IRI) rdf4jTermFactory.asValue(predicate);
        Value obj = rdf4jTermFactory.asValue(object);
        try (RepositoryConnection conn = getRepositoryConnection()) {
            conn.add(subj, pred, obj, contextMask);
            conn.commit();
        }
    }

    @Override
    public boolean contains(BlankNodeOrIRI subject, IRI predicate, RDFTerm object) {
        Resource subj = (Resource) rdf4jTermFactory.asValue(subject);
        org.eclipse.rdf4j.model.IRI pred = (org.eclipse.rdf4j.model.IRI) rdf4jTermFactory.asValue(predicate);
        Value obj = rdf4jTermFactory.asValue(object);
        try (RepositoryConnection conn = getRepositoryConnection()) {
            return conn.hasStatement(subj, pred, obj, includeInferred, contextMask);
        }
    }

    @Override
    public void remove(BlankNodeOrIRI subject, IRI predicate, RDFTerm object) {
        Resource subj = (Resource) rdf4jTermFactory.asValue(subject);
        org.eclipse.rdf4j.model.IRI pred = (org.eclipse.rdf4j.model.IRI) rdf4jTermFactory.asValue(predicate);
        Value obj = rdf4jTermFactory.asValue(object);
        try (RepositoryConnection conn = getRepositoryConnection()) {
            conn.remove(subj, pred, obj, contextMask);
            conn.commit();
        }
    }

    @Override
    public ClosableIterable<Triple> iterate() throws ConcurrentModificationException, IllegalStateException {
        return iterate(null, null, null);
    }

    @Override
    public ClosableIterable<Triple> iterate(BlankNodeOrIRI subject, IRI predicate, RDFTerm object)
            throws ConcurrentModificationException, IllegalStateException {
        Resource subj = (Resource) rdf4jTermFactory.asValue(subject);
        org.eclipse.rdf4j.model.IRI pred = (org.eclipse.rdf4j.model.IRI) rdf4jTermFactory.asValue(predicate);
        Value obj = rdf4jTermFactory.asValue(object);
        return new ConvertedStatements<>(this::getRepositoryConnection, rdf4jTermFactory::asTriple, subj, pred,
                obj, contextMask);
    }

    @Override
    public Stream<RDF4JTriple> stream() {
        return stream(null, null, null);
    }

    @Override
    public Stream<RDF4JTriple> stream(BlankNodeOrIRI subject, IRI predicate, RDFTerm object) {
        Resource subj = (Resource) rdf4jTermFactory.asValue(subject);
        org.eclipse.rdf4j.model.IRI pred = (org.eclipse.rdf4j.model.IRI) rdf4jTermFactory.asValue(predicate);
        Value obj = rdf4jTermFactory.asValue(object);

        // NOTE: We can't do the usual try..with closing of the
        // RepositoryConnection here as it will have to be closed outside
        // by the user of the returned stream
        RepositoryConnection conn = getRepositoryConnection();
        Stream<RDF4JTriple> stream = null;
        try {
            RepositoryResult<Statement> statements = conn.getStatements(subj, pred, obj, includeInferred, contextMask);
            // NOTE: Iterations.stream should close RepositoryResult as long as
            // our caller closes the stream
            stream = Iterations.stream(statements).map(this::asTripleLike);
        } finally {
            if (stream == null) {
                // Some exception before we made the stream, close connection
                // here
                conn.close();
            }
        }
        // Make sure the RepositoryConnection is closed
        return stream.onClose(conn::close);
    }

    @Override
    protected RDF4JTriple asTripleLike(Statement statement) {
        return rdf4jTermFactory.asTriple(statement);
    }

    @Override
    public Set<RDF4JBlankNodeOrIRI> getContextMask() {
        Set<RDF4JBlankNodeOrIRI> mask = new HashSet<>();
        for (Resource s : contextMask) {
            mask.add((RDF4JBlankNodeOrIRI) rdf4jTermFactory.asRDFTerm(s));
        }
        return Collections.unmodifiableSet(mask);
    }

}
