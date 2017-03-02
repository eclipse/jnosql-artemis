package org.jnosql.artemis.key.query;


import org.jnosql.artemis.key.KeyValueRepository;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.time.Duration;
import java.util.Optional;

class KeyValueCrudRepositoryProxy<T> implements InvocationHandler {

    private final Class<T> typeClass;

    private final KeyValueRepository repository;

    private final DefaultKeyValueCrudRepository crudRepository;

    KeyValueCrudRepositoryProxy(Class<T> typeClass, KeyValueRepository repository) {
        this.typeClass = typeClass;
        this.repository = repository;
        this.crudRepository = new DefaultKeyValueCrudRepository(typeClass, repository);
    }

    @Override
    public Object invoke(Object o, Method method, Object[] args) throws Throwable {
        return method.invoke(crudRepository, args);
    }

    class DefaultKeyValueCrudRepository implements KeyValueCrudRepository {

        private final Class<T> typeClass;

        private final KeyValueRepository repository;

        public DefaultKeyValueCrudRepository(Class<T> typeClass, KeyValueRepository repository) {
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
