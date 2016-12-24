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
package org.jnosql.artemis.document;

import org.jnosql.diana.api.ExecuteAsyncQueryException;
import org.jnosql.diana.api.document.DocumentCollectionManager;
import org.jnosql.diana.api.document.DocumentEntity;
import org.jnosql.diana.api.document.DocumentQuery;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

/**
 * The default implementation of {@link DocumentCrudOperation}
 */
@SuppressWarnings("unchecked")
@ApplicationScoped
@DocumentCRUDInterceptor
class DefaultDocumentCrudOperation implements DocumentCrudOperation {


    private DocumentEntityConverter converter;

    private Instance<DocumentCollectionManager> manager;

    private DocumentWorkflow workflow;


    @Inject
    DefaultDocumentCrudOperation(DocumentEntityConverter converter, Instance<DocumentCollectionManager> manager, DocumentWorkflow workflow) {
        this.converter = converter;
        this.manager = manager;
        this.workflow = workflow;
    }

    DefaultDocumentCrudOperation() {
    }

    @Override
    public <T> T save(T entity) throws NullPointerException {

        UnaryOperator<DocumentEntity> saveAction = e -> manager.get().save(e);
        return workflow.flow(entity, saveAction);
    }

    @Override
    public <T> void saveAsync(T entity) throws ExecuteAsyncQueryException, UnsupportedOperationException {
        manager.get().saveAsync(converter.toDocument(Objects.requireNonNull(entity, "entity is required")));
    }

    @Override
    public <T> T save(T entity, Duration ttl) {
        UnaryOperator<DocumentEntity> saveAction = e -> manager.get().save(e, ttl);
        return workflow.flow(entity, saveAction);
    }

    @Override
    public <T> void saveAsync(T entity, Duration ttl) throws ExecuteAsyncQueryException, UnsupportedOperationException {
        Objects.requireNonNull(entity, "entity is required");
        Objects.requireNonNull(ttl, "ttl is required");
        manager.get().saveAsync(converter.toDocument(entity), ttl);
    }

    @Override
    public <T> void saveAsync(T entity, Consumer<T> callBack) throws ExecuteAsyncQueryException, UnsupportedOperationException {
        Objects.requireNonNull(entity, "entity is required");
        manager.get().saveAsync(converter.toDocument(entity),
                d -> {
                    T value = converter.toEntity((Class<T>) entity.getClass(), d);
                    callBack.accept(value);
                });
    }

    @Override
    public <T> void saveAsync(T entity, Duration ttl, Consumer<T> callBack) throws ExecuteAsyncQueryException, UnsupportedOperationException {
        Objects.requireNonNull(entity, "entity is required");
        Objects.requireNonNull(ttl, "ttl is required");
        manager.get().saveAsync(converter.toDocument(entity), ttl,
                d -> {
                    T value = converter.toEntity((Class<T>) entity.getClass(), d);
                    callBack.accept(value);
                });
    }

    @Override
    public <T> T update(T entity) {

        UnaryOperator<DocumentEntity> saveAction = e -> manager.get().update(e);
        return workflow.flow(entity, saveAction);
    }

    @Override
    public <T> void updateAsync(T entity) throws ExecuteAsyncQueryException, UnsupportedOperationException {
        manager.get().updateAsync(converter.toDocument(Objects.requireNonNull(entity, "entity is required")));
    }

    @Override
    public <T> void updateAsync(T entity, Consumer<T> callBack) throws ExecuteAsyncQueryException, UnsupportedOperationException {
        Objects.requireNonNull(entity, "entity is required");
        manager.get().updateAsync(converter.toDocument(entity),
                d -> {
                    T value = converter.toEntity((Class<T>) entity.getClass(), d);
                    callBack.accept(value);
                });
    }

    @Override
    public void delete(DocumentQuery query) {
        manager.get().delete(query);
    }

    @Override
    public void deleteAsync(DocumentQuery query) throws ExecuteAsyncQueryException, UnsupportedOperationException {
        manager.get().deleteAsync(query);
    }

    @Override
    public void deleteAsync(DocumentQuery query, Consumer<Void> callBack) throws ExecuteAsyncQueryException, UnsupportedOperationException {
        manager.get().deleteAsync(query, callBack);
    }

    @Override
    public <T> List<T> find(DocumentQuery query) throws NullPointerException {
        List<DocumentEntity> entities = manager.get().find(query);
        Function<DocumentEntity, T> function = e -> converter.toEntity(e);
        return entities.stream().map(function).collect(Collectors.toList());
    }

    @Override
    public <T> void findAsync(DocumentQuery query, Consumer<List<T>> callBack) throws ExecuteAsyncQueryException, UnsupportedOperationException {
        Function<DocumentEntity, T> function = e -> converter.toEntity(e);
        manager.get().findAsync(query, es -> callBack.accept(es.stream().map(function).collect(Collectors.toList())));
    }

}
