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
import org.jnosql.artemis.Repository;
import org.jnosql.artemis.graph.GraphConverter;
import org.jnosql.artemis.graph.GraphTemplate;
import org.jnosql.artemis.reflection.ClassRepresentation;
import org.jnosql.artemis.reflection.ClassRepresentations;
import org.jnosql.artemis.reflection.Reflections;

import java.lang.reflect.ParameterizedType;

/**
 * Proxy handle to generate {@link Repository}
 *
 * @param <T>  the type
 * @param <ID> the ID type
 */
class GraphRepositoryProxy<T, ID> extends AbstractGraphRepositoryProxy<T, ID> {


    private final GraphRepository repository;

    private final Reflections reflections;

    private final ClassRepresentation classRepresentation;

    private final GraphQueryParser queryParser;

    private final Graph graph;

    private final GraphConverter converter;


    GraphRepositoryProxy(GraphTemplate template, ClassRepresentations classRepresentations,
                         Class<?> repositoryType, Reflections reflections,
                         Graph graph, GraphConverter converter) {

        Class<T> typeClass = Class.class.cast(ParameterizedType.class.cast(repositoryType.getGenericInterfaces()[0])
                .getActualTypeArguments()[0]);

        this.graph = graph;
        this.converter = converter;
        this.reflections = reflections;
        this.classRepresentation = classRepresentations.get(typeClass);
        this.queryParser = new GraphQueryParser();
        this.repository = new GraphRepository(template, classRepresentation);

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
    protected GraphQueryParser getQueryParser() {
        return queryParser;
    }

    @Override
    protected Graph getGraph() {
        return graph;
    }

    @Override
    protected GraphConverter getConverter() {
        return converter;
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

        @Override
        protected Reflections getReflections() {
            return reflections;
        }

    }
}
