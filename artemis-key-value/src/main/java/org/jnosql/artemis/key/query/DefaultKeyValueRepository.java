package org.jnosql.artemis.key.query;

import org.jnosql.artemis.Repository;
import org.jnosql.artemis.key.KeyValueTemplate;

import java.util.Optional;

class DefaultKeyValueRepository<T>  implements Repository {

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