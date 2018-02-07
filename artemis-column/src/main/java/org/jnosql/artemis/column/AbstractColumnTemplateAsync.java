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
package org.jnosql.artemis.column;

import org.jnosql.artemis.Converters;
import org.jnosql.artemis.IdNotFoundException;
import org.jnosql.artemis.column.util.ConverterUtil;
import org.jnosql.artemis.reflection.ClassRepresentation;
import org.jnosql.artemis.reflection.ClassRepresentations;
import org.jnosql.artemis.reflection.FieldRepresentation;
import org.jnosql.diana.api.ExecuteAsyncQueryException;
import org.jnosql.diana.api.column.ColumnDeleteQuery;
import org.jnosql.diana.api.column.ColumnEntity;
import org.jnosql.diana.api.column.ColumnFamilyManagerAsync;
import org.jnosql.diana.api.column.ColumnQuery;
import org.jnosql.diana.api.column.query.ColumnQueryBuilder;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;


/**
 * The template method to {@link ColumnTemplateAsync}
 */
public abstract class AbstractColumnTemplateAsync implements ColumnTemplateAsync {

    private static final Consumer EMPTY = t -> {
    };

    protected abstract ColumnEntityConverter getConverter();

    protected abstract ColumnFamilyManagerAsync getManager();

    protected abstract ClassRepresentations getClassRepresentations();

    protected abstract Converters getConverters();

    @Override
    public <T> void insert(T entity) throws ExecuteAsyncQueryException, UnsupportedOperationException, NullPointerException {
        insert(entity, EMPTY);
    }

    @Override
    public <T> void insert(T entity, Duration ttl) throws ExecuteAsyncQueryException, UnsupportedOperationException, NullPointerException {
        insert(entity, ttl, t -> {
        });
    }

    @Override
    public <T> void insert(T entity, Consumer<T> callBack) throws ExecuteAsyncQueryException, UnsupportedOperationException, NullPointerException {
        requireNonNull(entity, "entity is required");
        requireNonNull(callBack, "callBack is required");
        Consumer<ColumnEntity> dianaCallBack = c -> callBack.accept((T) getConverter().toEntity(entity.getClass(), c));
        getManager().insert(getConverter().toColumn(entity), dianaCallBack);
    }

    @Override
    public <T> void insert(T entity, Duration ttl, Consumer<T> callBack) throws ExecuteAsyncQueryException, UnsupportedOperationException, NullPointerException {
        requireNonNull(entity, "entity is required");
        requireNonNull(ttl, "ttl is required");
        requireNonNull(callBack, "callBack is required");
        Consumer<ColumnEntity> dianaCallBack = c -> callBack.accept((T) getConverter().toEntity(entity.getClass(), c));
        getManager().insert(getConverter().toColumn(entity), ttl, dianaCallBack);
    }

    @Override
    public <T> void update(T entity) throws ExecuteAsyncQueryException, UnsupportedOperationException, NullPointerException {
        requireNonNull(entity, "entity is required");
        update(entity, t -> {
        });
    }

    @Override
    public <T> void update(T entity, Consumer<T> callBack) throws ExecuteAsyncQueryException, UnsupportedOperationException, NullPointerException {
        requireNonNull(entity, "entity is required");
        requireNonNull(callBack, "callBack is required");
        Consumer<ColumnEntity> dianaCallBack = c -> callBack.accept((T) getConverter().toEntity(entity.getClass(), c));
        getManager().update(getConverter().toColumn(entity), dianaCallBack);
    }

    @Override
    public void delete(ColumnDeleteQuery query) throws ExecuteAsyncQueryException, UnsupportedOperationException, NullPointerException {
        requireNonNull(query, "query is required");
        getManager().delete(query);
    }

    @Override
    public void delete(ColumnDeleteQuery query, Consumer<Void> callBack) throws ExecuteAsyncQueryException, UnsupportedOperationException, NullPointerException {
        requireNonNull(query, "query is required");
        requireNonNull(callBack, "callBack is required");
        getManager().delete(query);
    }

    @Override
    public <T> void select(ColumnQuery query, Consumer<List<T>> callBack) throws ExecuteAsyncQueryException, UnsupportedOperationException, NullPointerException {
        requireNonNull(query, "query is required");
        requireNonNull(callBack, "callBack is required");

        Consumer<List<ColumnEntity>> dianaCallBack = d -> callBack.accept(
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

        ColumnQuery query = ColumnQueryBuilder.select().from(classRepresentation.getName())
                .where(idField.getName()).eq(value).build();

        singleResult(query, callBack);
    }

    @Override
    public <T, ID> void delete(Class<T> entityClass, ID id, Consumer<Void> callBack) throws
            NullPointerException, IdNotFoundException {

        requireNonNull(entityClass, "entityClass is required");
        requireNonNull(id, "id is required");
        requireNonNull(callBack, "callBack is required");

        ColumnDeleteQuery query = getDeleteQuery(entityClass, id);

        delete(query, callBack);
    }


    @Override
    public <T, ID> void delete(Class<T> entityClass, ID id) throws
            NullPointerException, IdNotFoundException {
        requireNonNull(entityClass, "entityClass is required");
        requireNonNull(id, "id is required");

        ColumnDeleteQuery query = getDeleteQuery(entityClass, id);

        delete(query);
    }

    private <T, ID> ColumnDeleteQuery getDeleteQuery(Class<T> entityClass, ID id) {
        ClassRepresentation classRepresentation = getClassRepresentations().get(entityClass);
        FieldRepresentation idField = classRepresentation.getId()
                .orElseThrow(() -> IdNotFoundException.newInstance(entityClass));

        Object value = ConverterUtil.getValue(id, classRepresentation, idField.getFieldName(), getConverters());
        return ColumnQueryBuilder.delete().from(classRepresentation.getName())
                .where(idField.getName()).eq(value).build();
    }
}

