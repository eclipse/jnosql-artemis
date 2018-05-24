/*
 *  Copyright (c) 2017 Otávio Santana and others
 *   All rights reserved. This program and the accompanying materials
 *   are made available under the terms of the Eclipse Public License v1.0
 *   and Apache License v2.0 which accompanies this distribution.
 *   The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 *   and the Apache License v2.0 is available at http://www.opensource.org/licenses/apache2.0.php.
 *
 *   You may elect to redistribute this code under either of these licenses.
 *
 *   Contributors:
 *
 *   Otavio Santana
 */
package org.jnosql.artemis.graph;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Transaction;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.jnosql.artemis.reflection.ClassRepresentations;
import org.jnosql.artemis.reflection.Reflections;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.util.Iterator;

/**
 * The default {@link GraphTemplate} to GraphTraversalSourceOperation
 */
@GraphTraversalSourceOperation
class DefaultGraphTraversalSourceTemplate extends AbstractGraphTemplate {


    private Instance<GraphTraversalSourceSupplier> supplierInstance;

    private ClassRepresentations classRepresentations;

    private GraphConverter converter;

    private GraphWorkflow workflow;

    private Reflections reflections;

    @Inject
    DefaultGraphTraversalSourceTemplate(Instance<GraphTraversalSourceSupplier> supplierInstance,
                                        ClassRepresentations classRepresentations,
                                        @GraphTraversalSourceOperation GraphConverter converter,
                                        GraphWorkflow workflow, Reflections reflections) {
        this.supplierInstance = supplierInstance;
        this.classRepresentations = classRepresentations;
        this.converter = converter;
        this.workflow = workflow;
        this.reflections = reflections;
    }

    DefaultGraphTraversalSourceTemplate() {
    }

    @Override
    protected Graph getGraph() {
        throw new UnsupportedOperationException("The GraphTraversalSourceOperation implementation does not support Graph");
    }

    @Override
    public Transaction getTransaction() {
        return getTraversal().tx();
    }

    @Override
    protected GraphTraversalSource getTraversal() {
        return supplierInstance.get().get();
    }
    @Override
    protected ClassRepresentations getClassRepresentations() {
        return classRepresentations;
    }

    @Override
    protected GraphConverter getConverter() {
        return converter;
    }

    @Override
    protected GraphWorkflow getFlow() {
        return workflow;
    }

    @Override
    protected Reflections getReflections() {
        return reflections;
    }


    @Override
    protected Iterator<Vertex> getVertices(Object id) {
        return getTraversal().V(id).toList().iterator();
    }

}
