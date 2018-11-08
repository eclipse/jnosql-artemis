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
package org.jnosql.artemis.graph.query;

import org.apache.tinkerpop.gremlin.structure.Graph;
import org.jnosql.artemis.Converters;
import org.jnosql.artemis.Repository;
import org.jnosql.artemis.graph.GraphConverter;
import org.jnosql.artemis.graph.GraphTemplate;
import org.jnosql.artemis.reflection.ClassRepresentation;
import org.jnosql.artemis.reflection.ClassRepresentations;

import java.lang.reflect.ParameterizedType;

/**
 * Proxy handle to generate {@link Repository}
 *
 * @param <T>  the type
 * @param <ID> the ID type
 */
class GraphRepositoryProxy<T, ID> extends AbstractGraphRepositoryProxy<T, ID> {


    private final GraphRepository repository;

    private final ClassRepresentation classRepresentation;

    private final Graph graph;

    private final GraphConverter converter;

    private final GraphTemplate template;

    private final Converters converters;


    GraphRepositoryProxy(GraphTemplate template, ClassRepresentations classRepresentations,
                         Class<?> repositoryType,
                         Graph graph, GraphConverter converter,
                         Converters converters) {

        Class<T> typeClass = Class.class.cast(ParameterizedType.class.cast(repositoryType.getGenericInterfaces()[0])
                .getActualTypeArguments()[0]);

        this.graph = graph;
        this.converter = converter;
        this.classRepresentation = classRepresentations.get(typeClass);
        this.repository = new GraphRepository(template, classRepresentation);
        this.template = template;
        this.converters = converters;

    }

    @Override
    protected ClassRepresentation getClassRepresentation() {
        return classRepresentation;
    }

    @Override
    protected Repository getRepository() {
        return repository;
    }

    @Override
    protected Graph getGraph() {
        return graph;
    }

    @Override
    protected GraphConverter getConverter() {
        return converter;
    }

    @Override
    protected GraphTemplate getTemplate() {
        return template;
    }

    @Override
    protected Converters getConverters() {
        return converters;
    }


    class GraphRepository extends AbstractGraphRepository implements Repository {

        private final GraphTemplate template;

        private final ClassRepresentation classRepresentation;

        GraphRepository(GraphTemplate template, ClassRepresentation classRepresentation) {
            this.template = template;
            this.classRepresentation = classRepresentation;
        }

        @Override
        protected GraphTemplate getTemplate() {
            return template;
        }

        @Override
        protected ClassRepresentation getClassRepresentation() {
            return classRepresentation;
        }

    }
}
