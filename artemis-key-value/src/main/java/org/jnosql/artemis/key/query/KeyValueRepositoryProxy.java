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


import org.jnosql.artemis.DynamicQueryException;
import org.jnosql.artemis.Repository;
import org.jnosql.artemis.key.KeyValueTemplate;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

class KeyValueRepositoryProxy<T> implements InvocationHandler {

    private final DefaultKeyValueRepository crudRepository;

    private static final List<Method> METHODS;

   static{
       METHODS = new ArrayList<>();
       METHODS.addAll(Arrays.asList(Object.class.getMethods()));
       METHODS.addAll(Arrays.asList(Repository.class.getMethods()));
   }

    KeyValueRepositoryProxy(Class<?> repositoryType, KeyValueTemplate repository) {
        Class<T> typeClass = Class.class.cast(ParameterizedType.class.cast(repositoryType.getGenericInterfaces()[0])
                .getActualTypeArguments()[0]);
        this.crudRepository = new DefaultKeyValueRepository(typeClass, repository);
    }

    @Override
    public Object invoke(Object o, Method method, Object[] args) throws Throwable {

       if(METHODS.stream().anyMatch(method::equals)) {
           return method.invoke(crudRepository, args);
       } else {
           throw new DynamicQueryException("Key Value repository does not support query method");
       }
    }

    class DefaultKeyValueRepository implements Repository {

        private final Class<T> typeClass;

        private final KeyValueTemplate repository;

        public DefaultKeyValueRepository(Class<T> typeClass, KeyValueTemplate repository) {
            this.typeClass = typeClass;
            this.repository = repository;
        }


        @Override
        public Object save(Object entity) throws NullPointerException {
            return repository.put(entity);
        }

        @Override
        public Iterable save(Iterable entities) throws NullPointerException {
            return repository.put(entities);
        }

        @Override
        public void deleteById(Object key) throws NullPointerException {
            repository.remove(key);
        }

        @Override
        public void deleteById(Iterable ids) throws NullPointerException {
            repository.remove(ids);
        }

        @Override
        public Optional findById(Object key) throws NullPointerException {
            return repository.get(key, typeClass);
        }

        @Override
        public Iterable findById(Iterable keys) throws NullPointerException {
            return repository.get(keys, typeClass);
        }

        @Override
        public boolean existsById(Object key) throws NullPointerException {
            return repository.get(key, typeClass).isPresent();
        }
    }
}
