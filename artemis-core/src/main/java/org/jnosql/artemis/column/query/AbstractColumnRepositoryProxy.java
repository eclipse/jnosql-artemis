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


import org.jnosql.artemis.Repository;
import org.jnosql.artemis.column.ColumnTemplate;
import org.jnosql.artemis.reflection.ClassRepresentation;
import org.jnosql.diana.api.column.ColumnDeleteQuery;
import org.jnosql.diana.api.column.ColumnQuery;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import static org.jnosql.artemis.column.query.ColumnRepositoryType.getDeleteQuery;
import static org.jnosql.artemis.column.query.ColumnRepositoryType.getQuery;
import static org.jnosql.artemis.column.query.ReturnTypeConverterUtil.returnObject;

/**
 * Template method to {@link Repository} proxy on column
 * @param <T>
 */
public abstract class AbstractColumnRepositoryProxy<T> implements InvocationHandler {

    protected abstract Repository getRepository();

    protected abstract  ClassRepresentation getClassRepresentation();

    protected abstract ColumnQueryParser getQueryParser();

    protected abstract ColumnQueryDeleteParser getDeleteParser();

    protected abstract ColumnTemplate getTemplate();

    @Override
    public Object invoke(Object o, Method method, Object[] args) throws Throwable {
        String methodName = method.getName();
        ColumnRepositoryType type = ColumnRepositoryType.of(method, args);
        Class<?> typeClass = getClassRepresentation().getClassInstance();

        switch (type) {
            case DEFAULT:
                return method.invoke(getRepository(), args);
            case FIND_BY:
                ColumnQuery query = getQueryParser().parse(methodName, args, getClassRepresentation());
                return returnObject(query, getTemplate(), typeClass, method);
            case DELETE_BY:
                ColumnDeleteQuery deleteQuery = getDeleteParser().parse(methodName, args, getClassRepresentation());
                getTemplate().delete(deleteQuery);
                return Void.class;
            case QUERY:
                ColumnQuery columnQuery = getQuery(args).get();
                return returnObject(columnQuery, getTemplate(), typeClass, method);
            case QUERY_DELETE:
                ColumnDeleteQuery columnDeleteQuery = getDeleteQuery(args).get();
                getTemplate().delete(columnDeleteQuery);
                return Void.class;
            default:
                return Void.class;

        }
    }

}
