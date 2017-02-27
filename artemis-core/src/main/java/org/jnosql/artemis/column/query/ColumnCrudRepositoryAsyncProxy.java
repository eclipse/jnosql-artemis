/*
 * Copyright 2017 Otavio Santana and others
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jnosql.artemis.column.query;


import org.jnosql.artemis.CrudRepositoryAsync;
import org.jnosql.artemis.DynamicQueryException;
import org.jnosql.artemis.column.ColumnRepositoryAsync;
import org.jnosql.artemis.reflection.ClassRepresentation;
import org.jnosql.artemis.reflection.ClassRepresentations;
import org.jnosql.diana.api.ExecuteAsyncQueryException;
import org.jnosql.diana.api.column.ColumnDeleteQuery;
import org.jnosql.diana.api.column.ColumnQuery;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.time.Duration;
import java.util.function.Consumer;

/**
 * Proxy handle to generate {@link CrudRepositoryAsync}
 *
 * @param <T> the type
 */
class ColumnCrudRepositoryAsyncProxy<T> implements InvocationHandler {

    public static final String SAVE = "save";
    public static final String UPDATE = "update";
    public static final String FIND_BY = "findBy";
    public static final String DELETE_BY = "deleteBy";

    private final Class<T> typeClass;

    private final ColumnRepositoryAsync repository;

    private final ColumnCrudRepositoryAsync crudRepository;

    private final ClassRepresentation classRepresentation;

    private final ColumnQueryParser queryParser;

    private final ColumnQueryDeleteParser queryDeleteParser;


    ColumnCrudRepositoryAsyncProxy(ColumnRepositoryAsync repository, ClassRepresentations classRepresentations, Class<?> repositoryType) {
        this.repository = repository;
        this.crudRepository = new ColumnCrudRepositoryAsync(repository);
        this.typeClass = Class.class.cast(ParameterizedType.class.cast(repositoryType.getGenericInterfaces()[0])
                .getActualTypeArguments()[0]);
        this.classRepresentation = classRepresentations.get(typeClass);
        this.queryParser = new ColumnQueryParser();
        this.queryDeleteParser = new ColumnQueryDeleteParser();
    }


    @Override
    public Object invoke(Object instance, Method method, Object[] args) throws Throwable {

        String methodName = method.getName();
        switch (methodName) {
            case SAVE:
            case UPDATE:
                return method.invoke(crudRepository, args);
            default:
        }


        if (methodName.startsWith(FIND_BY)) {
            ColumnQuery query = queryParser.parse(methodName, args, classRepresentation);
            Object callBack = args[args.length - 1];
            if (Consumer.class.isInstance(callBack)) {
                repository.find(query, Consumer.class.cast(callBack));
                return null;
            }

            throw new DynamicQueryException("On find async method you must put a java.util.function.Consumer" +
                    " as end parameter as callback");
        }

        if (methodName.startsWith(DELETE_BY)) {
            Object callBack = args[args.length - 1];
            ColumnDeleteQuery query = queryDeleteParser.parse(methodName, args, classRepresentation);
            if (Consumer.class.isInstance(callBack)) {
                repository.delete(query, Consumer.class.cast(callBack));
                return null;
            }

            repository.delete(query);
            return null;
        }

        return null;
    }


    class ColumnCrudRepositoryAsync implements CrudRepositoryAsync {

        private final ColumnRepositoryAsync repository;

        ColumnCrudRepositoryAsync(ColumnRepositoryAsync repository) {
            this.repository = repository;
        }

        @Override
        public void save(Object entity) throws ExecuteAsyncQueryException, UnsupportedOperationException, NullPointerException {
            repository.save(entity);
        }

        @Override
        public void save(Object entity, Duration ttl) throws ExecuteAsyncQueryException, UnsupportedOperationException, NullPointerException {
            repository.save(entity, ttl);
        }

        @Override
        public void save(Iterable entities) throws ExecuteAsyncQueryException, UnsupportedOperationException, NullPointerException {
            repository.save(entities);
        }

        @Override
        public void save(Iterable entities, Duration ttl) throws NullPointerException {
            repository.save(entities, ttl);
        }

        @Override
        public void update(Iterable entities) throws ExecuteAsyncQueryException, UnsupportedOperationException, NullPointerException {
            repository.update(entities);
        }

        @Override
        public void update(Object entity) throws ExecuteAsyncQueryException, UnsupportedOperationException, NullPointerException {
            repository.save(entity);
        }

        @Override
        public void update(Object entity, Consumer callBack) throws ExecuteAsyncQueryException, UnsupportedOperationException, NullPointerException {
            repository.update(entity, callBack);
        }

        @Override
        public void save(Object entity, Duration ttl, Consumer callBack) throws ExecuteAsyncQueryException, UnsupportedOperationException, NullPointerException {
            repository.save(entity, ttl, callBack);
        }

        @Override
        public void save(Object entity, Consumer callBack) throws ExecuteAsyncQueryException, UnsupportedOperationException, NullPointerException {
            repository.save(entity, callBack);
        }
    }
}
