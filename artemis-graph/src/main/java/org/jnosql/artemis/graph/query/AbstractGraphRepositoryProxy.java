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
import org.apache.tinkerpop.gremlin.structure.Element;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.jnosql.artemis.Repository;
import org.jnosql.artemis.graph.GraphConverter;
import org.jnosql.artemis.reflection.ClassRepresentation;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Stream;

import static org.jnosql.artemis.graph.query.ReturnTypeConverterUtil.returnObject;

/**
 * Template method to {@link Repository} proxy on Graph
 *
 * @param <T>  the entity type
 * @param <ID> the ID entity
 */
abstract class AbstractGraphRepositoryProxy<T, ID> implements InvocationHandler {


    protected abstract ClassRepresentation getClassRepresentation();

    protected abstract Repository getRepository();

    protected abstract GraphQueryParser getQueryParser();

    protected abstract Graph getGraph();

    protected abstract GraphConverter getConverter();


    @Override
    public Object invoke(Object instance, Method method, Object[] args) throws Throwable {
        String methodName = method.getName();
        GraphRepositoryType type = GraphRepositoryType.of(method, args);

        switch (type) {
            case DEFAULT:
                return method.invoke(getRepository(), args);
            case FIND_BY:
                return executeFindByMethod(method, args, methodName);
            case DELETE_BY:
                return executeDeleteMethod(args, methodName);
            case FIND_ALL:
                return executeFindAll(method, args);
            case OBJECT_METHOD:
                return method.invoke(this, args);
            case UNKNOWN:
            default:
                return Void.class;

        }
    }

    private Object executeDeleteMethod(Object[] args, String methodName) {
        GraphTraversal<Vertex, Vertex> traversal = getGraph().traversal().V();
        getQueryParser().deleteByParse(methodName, args, getClassRepresentation(), traversal);

        List<?> vertices = traversal.toList();

        for (Object element : vertices) {
            if (Element.class.isInstance(element)) {
                Element.class.cast(element).remove();
            }
        }
        return Void.class;
    }

    private Object executeFindByMethod(Method method, Object[] args, String methodName) {
        Class<?> classInstance = getClassRepresentation().getClassInstance();
        GraphTraversal<Vertex, Vertex> traversal = getGraph().traversal().V();
        getQueryParser().findByParse(methodName, args, getClassRepresentation(), traversal);

        List<Vertex> vertices = traversal.hasLabel(getClassRepresentation().getName()).toList();
        Stream<T> stream = vertices.stream().map(getConverter()::toEntity);

        return returnObject(stream, classInstance, method);
    }

    private Object executeFindAll(Method method, Object[] args) {
        Class<?> classInstance = getClassRepresentation().getClassInstance();
        GraphTraversal<Vertex, Vertex> traversal = getGraph().traversal().V();
        List<Vertex> vertices = traversal.hasLabel(getClassRepresentation().getName()).toList();
        Stream<T> stream = vertices.stream().map(getConverter()::toEntity);
        return returnObject(stream, classInstance, method);
    }

}
