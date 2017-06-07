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
package org.jnosql.artemis.key.query;


import org.jnosql.artemis.key.KeyValueTemplate;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.time.Duration;
import java.util.Optional;

class KeyValueCrudRepositoryProxy<T> implements InvocationHandler {

    private final DefaultKeyValueRepository crudRepository;

    KeyValueCrudRepositoryProxy(Class<?> repositoryType, KeyValueTemplate repository) {
        Class<T> typeClass = Class.class.cast(ParameterizedType.class.cast(repositoryType.getGenericInterfaces()[0])
                .getActualTypeArguments()[0]);
        this.crudRepository = new DefaultKeyValueRepository(typeClass, repository);
    }

    @Override
    public Object invoke(Object o, Method method, Object[] args) throws Throwable {
        return method.invoke(crudRepository, args);
    }

    class DefaultKeyValueRepository implements KeyValueRepository {

        private final Class<T> typeClass;

        private final KeyValueTemplate repository;

        public DefaultKeyValueRepository(Class<T> typeClass, KeyValueTemplate repository) {
            this.typeClass = typeClass;
            this.repository = repository;
        }

        @Override
        public Object put(Object entity) throws NullPointerException {
            return repository.put(entity);
        }

        @Override
        public Object put(Object entity, Duration ttl) throws NullPointerException, UnsupportedOperationException {
            return repository.put(entity, ttl);
        }

        @Override
        public Iterable put(Iterable entities) throws NullPointerException {
            return repository.put(entities);
        }

        @Override
        public Iterable put(Iterable entities, Duration ttl) throws NullPointerException, UnsupportedOperationException {
            return repository.put(entities, ttl);
        }

        @Override
        public void remove(Iterable keys) throws NullPointerException {
            repository.remove(keys);
        }

        @Override
        public void remove(Object key) throws NullPointerException {
            repository.remove(key);
        }

        @Override
        public Iterable get(Iterable keys) throws NullPointerException {
            return repository.get(keys, typeClass);
        }

        @Override
        public Optional get(Object key) throws NullPointerException {
            return repository.get(key, typeClass);
        }
    }
}
