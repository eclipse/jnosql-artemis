/*
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
package org.jnosql.artemis.column;

import org.jnosql.diana.api.ExecuteAsyncQueryException;
import org.jnosql.diana.api.column.ColumnEntity;
import org.jnosql.diana.api.column.ColumnFamilyManager;
import org.jnosql.diana.api.column.ColumnQuery;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * The default implementation of {@link ColumnCrudOperation}
 */
@SuppressWarnings("unchecked")
@ApplicationScoped
@ColumnCRUDInterceptor
class DefaultColumnCrudOperation implements ColumnCrudOperation {

    private ColumnEntityConverter converter;

    private Instance<ColumnFamilyManager> manager;

    private ColumnEventPersistManager columnEventPersistManager;

    @Inject
    DefaultColumnCrudOperation(ColumnEntityConverter converter, Instance<ColumnFamilyManager> manager, ColumnEventPersistManager columnEventPersistManager) {
        this.converter = converter;
        this.manager = manager;
        this.columnEventPersistManager = columnEventPersistManager;
    }

    DefaultColumnCrudOperation() {
    }

    @Override
    public <T> T save(T entity) throws NullPointerException {
        columnEventPersistManager.firePreEntity(entity);
        ColumnEntity document = converter.toColumn(Objects.requireNonNull(entity, "entity is required"));
        columnEventPersistManager.firePreDocument(document);
        ColumnEntity documentCollection = manager.get().save(document);
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
    public <T> T save(T entity, Duration ttl) {
        columnEventPersistManager.firePreEntity(entity);
        ColumnEntity document = converter.toColumn(Objects.requireNonNull(entity, "entity is required"));
        columnEventPersistManager.firePreDocument(document);
        ColumnEntity documentCollection = manager.get().save(document, ttl);
        columnEventPersistManager.firePostDocument(documentCollection);
        T entityUpdated = converter.toEntity((Class<T>) entity.getClass(), documentCollection);
        columnEventPersistManager.firePostEntity(entityUpdated);
        return entityUpdated;
    }

    @Override
    public <T> void saveAsync(T entity, Duration ttl) throws ExecuteAsyncQueryException, UnsupportedOperationException {
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
    public <T> void saveAsync(T entity, Duration ttl, Consumer<T> callBack) throws ExecuteAsyncQueryException, UnsupportedOperationException {
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
        ColumnEntity document = converter.toColumn(Objects.requireNonNull(entity, "entity is required"));
        columnEventPersistManager.firePreDocument(document);
        ColumnEntity documentCollection = manager.get().update(document);
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
        List<ColumnEntity> entities = manager.get().find(query);
        Function<ColumnEntity, T> function = e -> converter.toEntity(e);
        return entities.stream().map(function).collect(Collectors.toList());
    }

    @Override
    public <T> void findAsync(ColumnQuery query, Consumer<List<T>> callBack) throws ExecuteAsyncQueryException, UnsupportedOperationException {
        Function<ColumnEntity, T> function = e -> converter.toEntity(e);
        manager.get().findAsync(query, es -> callBack.accept(es.stream().map(function).collect(Collectors.toList())));
    }

}
