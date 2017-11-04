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
package org.jnosql.artemis.column.query;


import org.jnosql.artemis.DynamicQueryException;
import org.jnosql.artemis.RepositoryAsync;
import org.jnosql.artemis.column.ColumnTemplateAsync;
import org.jnosql.artemis.reflection.ClassRepresentation;
import org.jnosql.diana.api.column.ColumnDeleteQuery;
import org.jnosql.diana.api.column.ColumnQuery;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.function.Consumer;

import static org.jnosql.diana.api.column.query.ColumnQueryBuilder.select;

/**
 * Template method to {@link RepositoryAsync} proxy on column
 *
 * @param <T> the type
 */
public abstract class AbstractColumnRepositoryAsyncProxy<T> implements InvocationHandler {

    protected abstract RepositoryAsync getRepository();

    protected abstract ClassRepresentation getClassRepresentation();

    protected abstract ColumnQueryParser getQueryParser();

    protected abstract ColumnQueryDeleteParser getDeleteParser();

    protected abstract ColumnTemplateAsync getTemplate();

    @Override
    public Object invoke(Object instance, Method method, Object[] args) throws Throwable {

        String methodName = method.getName();
        ColumnRepositoryType type = ColumnRepositoryType.of(method, args);

        switch (type) {
            case DEFAULT:
                return method.invoke(getRepository(), args);
            case FIND_BY:
                ColumnQuery query = getQueryParser().parse(methodName, args, getClassRepresentation());
                return executeQuery(getCallBack(args), query);
            case FIND_ALL:
                return executeQuery(getCallBack(args), select().from(getClassRepresentation().getName()).build());
            case DELETE_BY:
                ColumnDeleteQuery deleteQuery = getDeleteParser().parse(methodName, args, getClassRepresentation());
                return executeDelete(getCallBack(args), deleteQuery);
            case QUERY:
                ColumnQuery columnQuery = ColumnRepositoryType.getQuery(args).get();
                return executeQuery(getCallBack(args), columnQuery);
            case QUERY_DELETE:
                return executeDelete(args, ColumnRepositoryType.getDeleteQuery(args).get());
            default:
                return Void.class;
        }
    }

    private Object executeDelete(Object arg, ColumnDeleteQuery deleteQuery) {
        if (Consumer.class.isInstance(arg)) {
            getTemplate().delete(deleteQuery, Consumer.class.cast(arg));
            return Void.class;
        }
        getTemplate().delete(deleteQuery);
        return Void.class;
    }

    private Object getCallBack(Object[] args) {
        return args[args.length - 1];
    }

    private Object executeQuery(Object arg, ColumnQuery query) {
        if (Consumer.class.isInstance(arg)) {
            getTemplate().select(query, Consumer.class.cast(arg));
            return null;
        }

        throw new DynamicQueryException("On select async method you must put a java.util.function.Consumer" +
                " as end parameter as callback");
    }

}
