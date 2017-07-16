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
package org.jnosql.artemis.column.query;


import org.jnosql.artemis.RepositoryAsync;
import org.jnosql.artemis.column.ColumnTemplateAsync;
import org.jnosql.artemis.reflection.ClassRepresentation;
import org.jnosql.artemis.reflection.FieldRepresentation;
import org.jnosql.artemis.reflection.Reflections;
import org.jnosql.diana.api.ExecuteAsyncQueryException;
import org.jnosql.diana.api.column.Column;
import org.jnosql.diana.api.column.ColumnDeleteQuery;
import org.jnosql.diana.api.column.ColumnQuery;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

import static java.util.Objects.requireNonNull;
import static org.jnosql.artemis.IdNotFoundException.KEY_NOT_FOUND_EXCEPTION_SUPPLIER;
import static org.jnosql.diana.api.column.ColumnCondition.eq;
import static org.jnosql.diana.api.column.query.ColumnQueryBuilder.delete;
import static org.jnosql.diana.api.column.query.ColumnQueryBuilder.select;

/**
 * The template method to {@link RepositoryAsync}
 */
public abstract class AbstractColumnRepositoryAsync<T, ID> implements RepositoryAsync<T, ID> {

    protected abstract ColumnTemplateAsync getTemplate();

    protected abstract Reflections getReflections();

    protected abstract ClassRepresentation getClassRepresentation();


    @Override
    public <S extends T> void save(S entity) throws ExecuteAsyncQueryException, UnsupportedOperationException, NullPointerException {
        Objects.requireNonNull(entity, "Entity is required");
        Object id = getReflections().getValue(entity, getIdField().getField());
        Consumer<Boolean> callBack = exist -> {
            if (exist) {
                getTemplate().update(entity);
            } else {
                getTemplate().insert(entity);
            }
        };
        existsById((ID) id, callBack);
    }


    @Override
    public <S extends T> void save(Iterable<S> entities) throws ExecuteAsyncQueryException, UnsupportedOperationException, NullPointerException {
        Objects.requireNonNull(entities, "entities is required");
        entities.forEach(this::save);
    }


    @Override
    public void deleteById(ID id) throws NullPointerException {
        requireNonNull(id, "is is required");

        String columnName = this.getIdField().getName();

        ColumnDeleteQuery query =  delete().from(getClassRepresentation().getName())
                .where(eq(Column.of(columnName, id))).build();
        getTemplate().delete(query);
    }


    @Override
    public void existsById(ID id, Consumer<Boolean> callBack) throws NullPointerException {
        Consumer<Optional<T>> as = o -> callBack.accept(o.isPresent());
        findById(id, as);
    }


    @Override
    public void findById(ID id, Consumer<Optional<T>> callBack) throws NullPointerException {
        requireNonNull(id, "id is required");
        requireNonNull(callBack, "callBack is required");

        String columnName = this.getIdField().getName();
        ColumnQuery query = select().from(getClassRepresentation().getName())
                .where(eq(Column.of(columnName, id))).build();
        getTemplate().singleResult(query, callBack);
    }

    private FieldRepresentation getIdField() {
        return getClassRepresentation().getId().orElseThrow(KEY_NOT_FOUND_EXCEPTION_SUPPLIER);
    }
}
