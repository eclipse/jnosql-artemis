/*
 * Copyright 2017 Otavio Santana and others
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jnosql.artemis.column.query;


import org.jnosql.artemis.RepositoryAsync;
import org.jnosql.artemis.column.ColumnTemplateAsync;
import org.jnosql.artemis.reflection.ClassRepresentation;
import org.jnosql.artemis.reflection.FieldRepresentation;
import org.jnosql.artemis.reflection.Reflections;
import org.jnosql.diana.api.ExecuteAsyncQueryException;
import org.jnosql.diana.api.column.Column;
import org.jnosql.diana.api.column.ColumnCondition;
import org.jnosql.diana.api.column.ColumnDeleteQuery;
import org.jnosql.diana.api.column.ColumnQuery;

import java.time.Duration;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

import static java.util.Objects.requireNonNull;
import static org.jnosql.artemis.IdNotFoundException.KEY_NOT_FOUND_EXCEPTION_SUPPLIER;

/**
 * The template method to {@link RepositoryAsync}
 */
public abstract class AbstractColumnRepositoryAsync<T, ID> implements RepositoryAsync<T, ID> {

    protected abstract ColumnTemplateAsync getTemplate();

    protected abstract Reflections getReflections();

    protected abstract ClassRepresentation getClassRepresentation();


    @Override
    public void save(T entity) throws ExecuteAsyncQueryException, UnsupportedOperationException, NullPointerException {
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
    public void save(T entity, Duration ttl) throws ExecuteAsyncQueryException, UnsupportedOperationException, NullPointerException {
        Objects.requireNonNull(entity, "Entity is required");
        Object id = getReflections().getValue(entity, getIdField().getField());
        Consumer<Boolean> callBack = exist -> {
            if (exist) {
                getTemplate().update(entity);
            } else {
                getTemplate().insert(entity, ttl);
            }
        };
        existsById((ID) id, callBack);
    }

    @Override
    public void save(Iterable<T> entities) throws ExecuteAsyncQueryException, UnsupportedOperationException, NullPointerException {
        Objects.requireNonNull(entities, "entities is required");
        entities.forEach(this::save);
    }

    @Override
    public void save(Iterable<T> entities, Duration ttl) throws NullPointerException {
        Objects.requireNonNull(entities, "entities is required");
        Objects.requireNonNull(ttl, "ttl is required");
        entities.forEach(e -> save(e, ttl));
    }

    @Override
    public void deleteById(ID id) throws NullPointerException {
        requireNonNull(id, "is is required");
        ColumnDeleteQuery query = ColumnDeleteQuery.of(getClassRepresentation().getName());
        String columnName = this.getIdField().getName();
        query.with(ColumnCondition.eq(Column.of(columnName, id)));
        getTemplate().delete(query);
    }

    @Override
    public void delete(Iterable<T> entities) throws NullPointerException {
        requireNonNull(entities, "entities is required");
        entities.forEach(this::delete);
    }

    @Override
    public void delete(T entity) throws NullPointerException {
        requireNonNull(entity, "entity is required");
        Object idValue = getReflections().getValue(entity, this.getIdField().getField());
        requireNonNull(idValue, "id value is required");
        deleteById((ID) idValue);
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
        ColumnQuery query = ColumnQuery.of(getClassRepresentation().getName());
        String columnName = this.getIdField().getName();
        query.with(ColumnCondition.eq(Column.of(columnName, id)));
        getTemplate().singleResult(query, callBack);
    }

    private FieldRepresentation getIdField() {
        return getClassRepresentation().getId().orElseThrow(KEY_NOT_FOUND_EXCEPTION_SUPPLIER);
    }
}
