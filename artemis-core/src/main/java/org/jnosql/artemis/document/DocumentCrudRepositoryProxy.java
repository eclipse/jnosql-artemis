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
package org.jnosql.artemis.document;


import org.jnosql.artemis.CrudRepository;
import org.jnosql.artemis.reflection.ClassRepresentation;
import org.jnosql.artemis.reflection.ClassRepresentations;
import org.jnosql.diana.api.document.DocumentDeleteQuery;
import org.jnosql.diana.api.document.DocumentQuery;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.time.Duration;

class DocumentCrudRepositoryProxy<T> implements InvocationHandler {

    private final Class<T> typeClass;

    private final DocumentRepository repository;


    private final DocumentCrudRepository crudRepository;

    private final ClassRepresentation classRepresentation;

    private final DocumentQueryParser documentQueryParser;

    private final DocumentQueryDeleteParser documentQueryDeleteParser;


    DocumentCrudRepositoryProxy(DocumentRepository repository, ClassRepresentations classRepresentations, Class<?> repositoryType) {
        this.repository = repository;
        this.crudRepository = new DocumentCrudRepository(repository);
        this.typeClass = Class.class.cast(ParameterizedType.class.cast(repositoryType.getGenericInterfaces()[0])
                .getActualTypeArguments()[0]);
        this.classRepresentation = classRepresentations.get(typeClass);
        this.documentQueryParser = new DocumentQueryParser();
        this.documentQueryDeleteParser = new DocumentQueryDeleteParser();
    }


    @Override
    public Object invoke(Object o, Method method, Object[] args) throws Throwable {

        String methodName = method.getName();
        switch (methodName) {
            case "save":
            case "update":
                return method.invoke(crudRepository, args);
            default:

        }
        if (methodName.startsWith("findBy")) {
            DocumentQuery query = documentQueryParser.parse(methodName, args, classRepresentation);
            return ReturnTypeConverterUtil.returnObject(query, repository, typeClass, method);
        } else if (methodName.startsWith("deleteBy")) {
            DocumentDeleteQuery query = documentQueryDeleteParser.parse(methodName, args, classRepresentation);
            repository.delete(query);
        }
        return null;
    }


    class DocumentCrudRepository implements CrudRepository {

        private final DocumentRepository repository;

        DocumentCrudRepository(DocumentRepository repository) {
            this.repository = repository;
        }

        @Override
        public Object save(Object entity) throws NullPointerException {
            return repository.save(entity);
        }

        @Override
        public Object save(Object entity, Duration ttl) {
            return repository.save(entity, ttl);
        }

        @Override
        public Iterable save(Iterable entities) throws NullPointerException {
            return repository.save(entities);
        }

        @Override
        public Iterable save(Iterable entities, Duration ttl) throws NullPointerException {
            return repository.save(entities, ttl);
        }

        @Override
        public Object update(Object entity) {
            return repository.update(entity);
        }

        @Override
        public Iterable update(Iterable entities) throws NullPointerException {
            return repository.update(entities);
        }
    }
}
