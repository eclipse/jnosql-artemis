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
import org.jnosql.artemis.reflection.ClassRepresentations;
import org.jnosql.diana.api.column.ColumnDeleteQuery;
import org.jnosql.diana.api.column.ColumnQuery;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.function.Consumer;

/**
 * Proxy handle to generate {@link RepositoryAsync}
 *
 * @param <T> the type
 */
class ColumnRepositoryAsyncProxy<T> implements InvocationHandler {

    private final Class<T> typeClass;

    private final ColumnTemplateAsync template;

    private final ColumnRepositoryAsync crudRepository;

    private final ClassRepresentation classRepresentation;

    private final ColumnQueryParser queryParser;

    private final ColumnQueryDeleteParser queryDeleteParser;


    ColumnRepositoryAsyncProxy(ColumnTemplateAsync template, ClassRepresentations classRepresentations, Class<?> repositoryType) {
        this.template = template;
        this.crudRepository = new ColumnRepositoryAsync(template);
        this.typeClass = Class.class.cast(ParameterizedType.class.cast(repositoryType.getGenericInterfaces()[0])
                .getActualTypeArguments()[0]);
        this.classRepresentation = classRepresentations.get(typeClass);
        this.queryParser = new ColumnQueryParser();
        this.queryDeleteParser = new ColumnQueryDeleteParser();
    }


    @Override
    public Object invoke(Object instance, Method method, Object[] args) throws Throwable {

        String methodName = method.getName();
        ColumnRepositoryType type = ColumnRepositoryType.of(method, args);

        switch (type) {
            case DEFAULT:
                return method.invoke(crudRepository, args);
            case FIND_BY:
                ColumnQuery query = queryParser.parse(methodName, args, classRepresentation);
                return executeQuery(getCallBack(args), query);
            case DELETE_BY:
                ColumnDeleteQuery deleteQuery = queryDeleteParser.parse(methodName, args, classRepresentation);
                return executeDelete(getCallBack(args), deleteQuery);
            case QUERY:
                ColumnQuery columnQuery = ColumnRepositoryType.getQuery(args).get();
                return executeQuery(getCallBack(args), columnQuery);
            case QUERY_DELETE:
                return executeDelete(args, ColumnRepositoryType.getDeleteQuery(args).get());
                default:
                    return null;
        }
    }

    private Object executeDelete(Object arg, ColumnDeleteQuery deleteQuery) {
        Object callBack = arg;
        if (Consumer.class.isInstance(callBack)) {
            template.delete(deleteQuery, Consumer.class.cast(callBack));
            return Void.class;
        }
        template.delete(deleteQuery);
        return Void.class;
    }

    private Object getCallBack(Object[] args) {
        return args[args.length - 1];
    }

    private Object executeQuery(Object arg, ColumnQuery query) {
        Object callBack = arg;
        if (Consumer.class.isInstance(callBack)) {
            template.find(query, Consumer.class.cast(callBack));
            return null;
        }

        throw new DynamicQueryException("On find async method you must put a java.util.function.Consumer" +
                " as end parameter as callback");
    }


    class ColumnRepositoryAsync extends AbstractColumnRepositoryAsync implements RepositoryAsync {

        private final ColumnTemplateAsync template;

        ColumnRepositoryAsync(ColumnTemplateAsync repository) {
            this.template = repository;
        }

        @Override
        protected ColumnTemplateAsync getTemplate() {
            return template;
        }
    }
}
