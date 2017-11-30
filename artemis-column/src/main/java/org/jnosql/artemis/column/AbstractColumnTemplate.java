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


import org.jnosql.artemis.IdNotFoundException;
import org.jnosql.artemis.reflection.ClassRepresentation;
import org.jnosql.artemis.reflection.ClassRepresentations;
import org.jnosql.artemis.reflection.FieldRepresentation;
import org.jnosql.diana.api.column.ColumnDeleteQuery;
import org.jnosql.diana.api.column.ColumnEntity;
import org.jnosql.diana.api.column.ColumnFamilyManager;
import org.jnosql.diana.api.column.ColumnQuery;
import org.jnosql.diana.api.column.query.ColumnQueryBuilder;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

/**
 * The template method to {@link ColumnTemplate}
 */
public abstract class AbstractColumnTemplate implements ColumnTemplate {


    protected abstract ColumnEntityConverter getConverter();

    protected abstract ColumnFamilyManager getManager();

    protected abstract ColumnWorkflow getFlow();

    protected abstract ColumnEventPersistManager getEventManager();

    protected abstract ClassRepresentations getClassRepresentations();

    private final UnaryOperator<ColumnEntity> insert = e -> getManager().insert(e);

    private final UnaryOperator<ColumnEntity> update = e -> getManager().update(e);

    @Override
    public <T> T insert(T entity) throws NullPointerException {
        requireNonNull(entity, "entity is required");

        return getFlow().flow(entity, insert);
    }


    @Override
    public <T> T insert(T entity, Duration ttl) {
        requireNonNull(entity, "entity is required");
        requireNonNull(ttl, "ttl is required");
        UnaryOperator<ColumnEntity> insert = e -> getManager().insert(e, ttl);
        return getFlow().flow(entity, insert);
    }


    @Override
    public <T> T update(T entity) {
        requireNonNull(entity, "entity is required");
        return getFlow().flow(entity, update);
    }


    @Override
    public void delete(ColumnDeleteQuery query) {
        requireNonNull(query, "query is required");
        getEventManager().firePreDeleteQuery(query);
        getManager().delete(query);
    }


    @Override
    public <T> List<T> select(ColumnQuery query) throws NullPointerException {
        requireNonNull(query, "query is required");
        getEventManager().firePreQuery(query);
        List<ColumnEntity> entities = getManager().select(query);
        Function<ColumnEntity, T> function = e -> getConverter().toEntity(e);
        return entities.stream().map(function).collect(Collectors.toList());
    }

    @Override
    public <T, ID> Optional<T> find(Class<T> entityClass, ID id) throws NullPointerException, IdNotFoundException {
        requireNonNull(entityClass, "entityClass is required");
        requireNonNull(id, "id is required");
        ClassRepresentation classRepresentation = getClassRepresentations().get(entityClass);
        FieldRepresentation idField = classRepresentation.getId()
                .orElseThrow(() -> IdNotFoundException.newInstance(entityClass));

        ColumnQuery query = ColumnQueryBuilder.select().from(classRepresentation.getName())
                .where(idField.getName()).eq(id).build();

        return singleResult(query);
    }

    @Override
    public <T, ID> void delete(Class<T> entityClass, ID id) throws NullPointerException, IdNotFoundException {
        requireNonNull(entityClass, "entityClass is required");
        requireNonNull(id, "id is required");

        ClassRepresentation classRepresentation = getClassRepresentations().get(entityClass);
        FieldRepresentation idField = classRepresentation.getId()
                .orElseThrow(() -> IdNotFoundException.newInstance(entityClass));
        ColumnDeleteQuery query = ColumnQueryBuilder.delete().from(classRepresentation.getName())
                .where(idField.getName()).eq(id).build();
        getManager().delete(query);
    }
}
