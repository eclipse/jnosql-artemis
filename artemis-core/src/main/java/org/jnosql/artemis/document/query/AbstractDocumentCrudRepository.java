package org.jnosql.artemis.document.query;

import org.jnosql.artemis.CrudRepository;
import org.jnosql.artemis.document.DocumentRepository;

import java.time.Duration;

/**
 * The {@link CrudRepository} template method
 */
public abstract class AbstractDocumentCrudRepository implements CrudRepository {

    protected abstract DocumentRepository getDocumentRepository();

    @Override
    public Object save(Object entity) throws NullPointerException {
        return getDocumentRepository().save(entity);
    }

    @Override
    public Object save(Object entity, Duration ttl) {
        return getDocumentRepository().save(entity, ttl);
    }

    @Override
    public Iterable save(Iterable entities) throws NullPointerException {
        return getDocumentRepository().save(entities);
    }

    @Override
    public Iterable save(Iterable entities, Duration ttl) throws NullPointerException {
        return getDocumentRepository().save(entities, ttl);
    }

    @Override
    public Object update(Object entity) {
        return getDocumentRepository().update(entity);
    }

    @Override
    public Iterable update(Iterable entities) throws NullPointerException {
        return getDocumentRepository().update(entities);
    }
}
