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

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.jnosql.artemis.Converters;
import org.jnosql.artemis.DynamicQueryException;
import org.jnosql.artemis.reflection.ClassMapping;
import org.jnosql.artemis.util.ConverterUtil;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static java.util.Collections.singletonList;

final class GraphQueryMethod {

    private final ClassMapping representation;
    private final GraphTraversal<Vertex, Vertex> traversal;
    private final Object[] args;
    private final Converters converters;
    private final Method method;
    private int counter = 0;

    GraphQueryMethod(ClassMapping representation,
                     GraphTraversal<Vertex, Vertex> traversal,
                     Converters converters, Method method, Object[] args) {
        this.representation = representation;
        this.traversal = traversal;
        this.args = args;
        this.converters = converters;
        this.method = method;
    }

    public Method getMethod() {
        return method;
    }

    public String getEntityName() {
        return representation.getName();
    }

    public ClassMapping getRepresentation() {
        return representation;
    }

    public GraphTraversal<Vertex, Vertex> getTraversal() {
        return traversal;
    }

    public Object getValue(String name) {
        Object value = getValue();
        return ConverterUtil.getValue(value, representation, name, converters);
    }

    public  Collection<?> getInValue(String name) {
        Object value = getValue();
        if(value instanceof Iterable<?>) {
            return (Collection<?>) StreamSupport.stream(Iterable.class.cast(value).spliterator(), false)
                    .map(v -> ConverterUtil.getValue(v, representation, name, converters))
                    .collect(Collectors.toList());
        }
        return singletonList(ConverterUtil.getValue(value, representation, name, converters));
    }

    private Object getValue() {
        if ((counter + 1) > args.length) {
            throw new DynamicQueryException(String.format("There is a missed argument in the method %s",
                    method));
        }
        Object value = args[counter];
        counter++;
        return value;
    }


}
