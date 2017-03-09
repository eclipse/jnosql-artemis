package org.jnosql.artemis.column.query;

import org.jnosql.artemis.CrudRepository;
import org.jnosql.artemis.column.ColumnRepository;

import java.time.Duration;

/**
 * The {@link CrudRepository} template method
 */
public abstract class AbstractColumnCrudRepository implements CrudRepository {

    protected abstract ColumnRepository getColumnRepository();

    @Override
    public Object save(Object entity) throws NullPointerException {
        return getColumnRepository().save(entity);
    }

    @Override
    public Object save(Object entity, Duration ttl) {
        return getColumnRepository().save(entity, ttl);
    }

    @Override
    public Iterable save(Iterable entities) throws NullPointerException {
        return getColumnRepository().save(entities);
    }

    @Override
    public Iterable save(Iterable entities, Duration ttl) throws NullPointerException {
        return getColumnRepository().save(entities, ttl);
    }

    @Override
    public Object update(Object entity) {
        return getColumnRepository().update(entity);
    }

    @Override
    public Iterable update(Iterable entities) throws NullPointerException {
        return getColumnRepository().update(entities);
    }
}
