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
package org.jnosql.artemis.document;


import org.jnosql.artemis.Converters;
import org.jnosql.artemis.IdNotFoundException;
import org.jnosql.artemis.document.util.ConverterUtil;
import org.jnosql.artemis.reflection.ClassRepresentation;
import org.jnosql.artemis.reflection.ClassRepresentations;
import org.jnosql.artemis.reflection.FieldRepresentation;
import org.jnosql.diana.api.ExecuteAsyncQueryException;
import org.jnosql.diana.api.document.DocumentCollectionManagerAsync;
import org.jnosql.diana.api.document.DocumentDeleteQuery;
import org.jnosql.diana.api.document.DocumentEntity;
import org.jnosql.diana.api.document.DocumentQuery;
import org.jnosql.diana.api.document.query.DocumentQueryBuilder;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;

/**
 * This class provides a skeletal implementation of the {@link DocumentTemplateAsync} interface,
 * to minimize the effort required to implement this interface.
 */
public abstract class AbstractDocumentTemplateAsync implements DocumentTemplateAsync {


    private static final Consumer EMPTY = t -> {
    };

    protected abstract DocumentEntityConverter getConverter();

    protected abstract DocumentCollectionManagerAsync getManager();

    protected abstract ClassRepresentations getClassRepresentations();

    protected abstract Converters getConverters();

    @Override
    public <T> void insert(T entity) throws ExecuteAsyncQueryException, UnsupportedOperationException, NullPointerException {
        insert(entity, EMPTY);
    }

    @Override
    public <T> void insert(T entity, Duration ttl) throws ExecuteAsyncQueryException, UnsupportedOperationException, NullPointerException {
        insert(entity, ttl, EMPTY);
    }

    @Override
    public <T> void insert(T entity, Consumer<T> callBack) throws ExecuteAsyncQueryException, UnsupportedOperationException, NullPointerException {
        requireNonNull(entity, "entity is required");
        requireNonNull(callBack, "callBack is required");
        Consumer<DocumentEntity> dianaCallBack = c -> callBack.accept((T) getConverter().toEntity(entity.getClass(), c));
        getManager().insert(getConverter().toDocument(entity), dianaCallBack);
    }

    @Override
    public <T> void insert(T entity, Duration ttl, Consumer<T> callBack) throws ExecuteAsyncQueryException, UnsupportedOperationException, NullPointerException {
        requireNonNull(entity, "entity is required");
        requireNonNull(ttl, "ttl is required");
        requireNonNull(callBack, "callBack is required");
        Consumer<DocumentEntity> dianaCallBack = c -> callBack.accept((T) getConverter().toEntity(entity.getClass(), c));
        getManager().insert(getConverter().toDocument(entity), ttl, dianaCallBack);
    }

    @Override
    public <T> void update(T entity) throws ExecuteAsyncQueryException, UnsupportedOperationException, NullPointerException {
        requireNonNull(entity, "entity is required");
        update(entity, EMPTY);
    }

    @Override
    public <T> void update(T entity, Consumer<T> callBack) throws ExecuteAsyncQueryException, UnsupportedOperationException, NullPointerException {
        requireNonNull(entity, "entity is required");
        requireNonNull(callBack, "callBack is required");
        Consumer<DocumentEntity> dianaCallBack = c -> callBack.accept((T) getConverter().toEntity(entity.getClass(), c));
        getManager().update(getConverter().toDocument(entity), dianaCallBack);
    }

    @Override
    public void delete(DocumentDeleteQuery query) throws ExecuteAsyncQueryException, UnsupportedOperationException, NullPointerException {
        requireNonNull(query, "query is required");
        getManager().delete(query);
    }

    @Override
    public void delete(DocumentDeleteQuery query, Consumer<Void> callBack) throws ExecuteAsyncQueryException, UnsupportedOperationException, NullPointerException {
        requireNonNull(query, "query is required");
        requireNonNull(callBack, "callBack is required");
        getManager().delete(query);
    }

    @Override
    public <T> void select(DocumentQuery query, Consumer<List<T>> callBack) throws ExecuteAsyncQueryException, UnsupportedOperationException, NullPointerException {
        requireNonNull(query, "query is required");
        requireNonNull(callBack, "callBack is required");

        Consumer<List<DocumentEntity>> dianaCallBack = d -> callBack.accept(
                d.stream()
                        .map(getConverter()::toEntity)
                        .map(o -> (T) o)
                        .collect(toList()));
        getManager().select(query, dianaCallBack);
    }


    @Override
    public <T, ID> void find(Class<T> entityClass, ID id, Consumer<Optional<T>> callBack) throws
            NullPointerException, IdNotFoundException {

        requireNonNull(entityClass, "entityClass is required");
        requireNonNull(id, "id is required");
        requireNonNull(callBack, "callBack is required");

        ClassRepresentation classRepresentation = getClassRepresentations().get(entityClass);
        FieldRepresentation idField = classRepresentation.getId()
                .orElseThrow(() -> IdNotFoundException.newInstance(entityClass));

        Object value = ConverterUtil.getValue(id, classRepresentation, idField.getFieldName(), getConverters());
        DocumentQuery query = DocumentQueryBuilder.select().from(classRepresentation.getName())
                .where(idField.getName()).eq(value).build();

        singleResult(query, callBack);
    }

    @Override
    public <T, ID> void delete(Class<T> entityClass, ID id, Consumer<Void> callBack) throws
            NullPointerException, IdNotFoundException {

        requireNonNull(entityClass, "entityClass is required");
        requireNonNull(id, "id is required");
        requireNonNull(callBack, "callBack is required");

        DocumentDeleteQuery query = getDeleteQuery(entityClass, id);
        delete(query, callBack);
    }

    @Override
    public <T, ID> void delete(Class<T> entityClass, ID id) throws
            NullPointerException, IdNotFoundException {

        requireNonNull(entityClass, "entityClass is required");
        requireNonNull(id, "id is required");

        DocumentDeleteQuery query = getDeleteQuery(entityClass, id);

        delete(query);
    }

    private <T, ID> DocumentDeleteQuery getDeleteQuery(Class<T> entityClass, ID id) {
        ClassRepresentation classRepresentation = getClassRepresentations().get(entityClass);
        FieldRepresentation idField = classRepresentation.getId()
                .orElseThrow(() -> IdNotFoundException.newInstance(entityClass));

        Object value = ConverterUtil.getValue(id, classRepresentation, idField.getFieldName(), getConverters());

        return DocumentQueryBuilder.delete().from(classRepresentation.getName())
                .where(idField.getName()).eq(value).build();
    }
}
