package org.jnosql.artemis.column;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import org.jnosql.diana.api.ExecuteAsyncQueryException;
import org.jnosql.diana.api.TTL;
import org.jnosql.diana.api.column.ColumnFamilyEntity;
import org.jnosql.diana.api.column.ColumnFamilyManager;
import org.jnosql.diana.api.column.ColumnQuery;

@ApplicationScoped
@ColumnCRUDInterceptor
class DefaultColumnCrudOperation implements ColumnCrudOperation{

    private final ColumnEntityConverter converter;

    private final Instance<ColumnFamilyManager> manager;

    private final ColumnEventPersistManager columnEventPersistManager;

    @Inject
    DefaultColumnCrudOperation(ColumnEntityConverter converter, Instance<ColumnFamilyManager> manager, ColumnEventPersistManager columnEventPersistManager) {
        this.converter = converter;
        this.manager = manager;
        this.columnEventPersistManager = columnEventPersistManager;
    }

    @Override
    public <T> T save(T entity) throws NullPointerException {
        columnEventPersistManager.firePreEntity(entity);
        ColumnFamilyEntity document = converter.toColumn(Objects.requireNonNull(entity, "entity is required"));
        columnEventPersistManager.firePreDocument(document);
        ColumnFamilyEntity documentCollection = manager.get().save(document);
        columnEventPersistManager.firePostDocument(documentCollection);
        T entityUpdated = converter.toEntity((Class<T>) entity.getClass(), documentCollection);
        columnEventPersistManager.firePostEntity(entityUpdated);
        return entityUpdated;
    }

    @Override
    public <T> void saveAsync(T entity) throws ExecuteAsyncQueryException, UnsupportedOperationException {
        manager.get().saveAsync(converter.toColumn(Objects.requireNonNull(entity, "entity is required")));
    }

    @Override
    public <T> T save(T entity, TTL ttl) {
        columnEventPersistManager.firePreEntity(entity);
        ColumnFamilyEntity document = converter.toColumn(Objects.requireNonNull(entity, "entity is required"));
        columnEventPersistManager.firePreDocument(document);
        ColumnFamilyEntity documentCollection = manager.get().save(document, ttl);
        columnEventPersistManager.firePostDocument(documentCollection);
        T entityUpdated = converter.toEntity((Class<T>) entity.getClass(), documentCollection);
        columnEventPersistManager.firePostEntity(entityUpdated);
        return entityUpdated;
    }

    @Override
    public <T> void saveAsync(T entity, TTL ttl) throws ExecuteAsyncQueryException, UnsupportedOperationException {
        Objects.requireNonNull(entity, "entity is required");
        Objects.requireNonNull(ttl, "ttl is required");
        manager.get().saveAsync(converter.toColumn(entity), ttl);
    }

    @Override
    public <T> void saveAsync(T entity, Consumer<T> callBack) throws ExecuteAsyncQueryException, UnsupportedOperationException {
        Objects.requireNonNull(entity, "entity is required");
        manager.get().saveAsync(converter.toColumn(entity),
                d -> {
                    T value = converter.toEntity((Class<T>) entity.getClass(), d);
                    callBack.accept(value);
                });
    }

    @Override
    public <T> void saveAsync(T entity, TTL ttl, Consumer<T> callBack) throws ExecuteAsyncQueryException, UnsupportedOperationException {
        Objects.requireNonNull(entity, "entity is required");
        Objects.requireNonNull(ttl, "ttl is required");
        manager.get().saveAsync(converter.toColumn(entity), ttl,
                d -> {
                    T value = converter.toEntity((Class<T>) entity.getClass(), d);
                    callBack.accept(value);
                });
    }

    @Override
    public <T> T update(T entity) {
        columnEventPersistManager.firePreEntity(entity);
        ColumnFamilyEntity document = converter.toColumn(Objects.requireNonNull(entity, "entity is required"));
        columnEventPersistManager.firePreDocument(document);
        ColumnFamilyEntity documentCollection = manager.get().update(document);
        columnEventPersistManager.firePostDocument(documentCollection);
        T entityUpdated = converter.toEntity((Class<T>) entity.getClass(), documentCollection);
        columnEventPersistManager.firePostEntity(entityUpdated);
        return entityUpdated;
    }

    @Override
    public <T> void updateAsync(T entity) throws ExecuteAsyncQueryException, UnsupportedOperationException {
        manager.get().updateAsync(converter.toColumn(Objects.requireNonNull(entity, "entity is required")));
    }

    @Override
    public <T> void updateAsync(T entity, Consumer<T> callBack) throws ExecuteAsyncQueryException, UnsupportedOperationException {
        Objects.requireNonNull(entity, "entity is required");
        manager.get().updateAsync(converter.toColumn(entity),
                d -> {
                    T value = converter.toEntity((Class<T>) entity.getClass(), d);
                    callBack.accept(value);
                });
    }

    @Override
    public void delete(ColumnQuery query) {
        manager.get().delete(query);
    }

    @Override
    public void deleteAsync(ColumnQuery query) throws ExecuteAsyncQueryException, UnsupportedOperationException {
        manager.get().deleteAsync(query);
    }

    @Override
    public void deleteAsync(ColumnQuery query, Consumer<Void> callBack) throws ExecuteAsyncQueryException, UnsupportedOperationException {
        manager.get().deleteAsync(query, callBack);
    }

    @Override
    public <T> List<T> find(ColumnQuery query) throws NullPointerException {
        List<ColumnFamilyEntity> entities = manager.get().find(query);
        Function<ColumnFamilyEntity, T> function = e -> converter.toEntity(e);
        return entities.stream().map(function).collect(Collectors.toList());
    }

    @Override
    public <T> void findAsync(ColumnQuery query, Consumer<List<T>> callBack) throws ExecuteAsyncQueryException, UnsupportedOperationException {
        Function<ColumnFamilyEntity, T> function = e -> converter.toEntity(e);
        manager.get().findAsync(query, es -> {
            callBack.accept(es.stream().map(function).collect(Collectors.toList()));
        });
    }

    @Override
    public <T> List<T> nativeQuery(String query) throws UnsupportedOperationException {
        List<ColumnFamilyEntity> entities = manager.get().nativeQuery(query);
        Function<ColumnFamilyEntity, T> function = e -> converter.toEntity(e);
        return entities.stream().map(function).collect(Collectors.toList());
    }

    @Override
    public <T> void nativeQueryAsync(String query, Consumer<List<T>> callBack) throws ExecuteAsyncQueryException, UnsupportedOperationException {
        manager.get().nativeQueryAsync(query, es -> {
            Function<ColumnFamilyEntity, T> function = e -> converter.toEntity(e);
            callBack.accept(es.stream().map(function).collect(Collectors.toList()));
        });
    }
}
