/*
 * Copyright 2017 Otavio Santana and others
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
        Object callBack = arg;
        if (Consumer.class.isInstance(callBack)) {
            getTemplate().delete(deleteQuery, Consumer.class.cast(callBack));
            return Void.class;
        }
        getTemplate().delete(deleteQuery);
        return Void.class;
    }

    private Object getCallBack(Object[] args) {
        return args[args.length - 1];
    }

    private Object executeQuery(Object arg, ColumnQuery query) {
        Object callBack = arg;
        if (Consumer.class.isInstance(callBack)) {
            getTemplate().select(query, Consumer.class.cast(callBack));
            return null;
        }

        throw new DynamicQueryException("On select async method you must put a java.util.function.Consumer" +
                " as end parameter as callback");
    }

}
