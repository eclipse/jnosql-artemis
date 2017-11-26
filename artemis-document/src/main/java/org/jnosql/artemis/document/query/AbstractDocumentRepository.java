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
package org.jnosql.artemis.document.query;

import org.jnosql.artemis.Repository;
import org.jnosql.artemis.document.DocumentTemplate;
import org.jnosql.artemis.reflection.ClassRepresentation;
import org.jnosql.artemis.reflection.FieldRepresentation;
import org.jnosql.artemis.reflection.Reflections;
import org.jnosql.diana.api.document.Document;
import org.jnosql.diana.api.document.DocumentDeleteQuery;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;
import static java.util.stream.StreamSupport.stream;
import static org.jnosql.artemis.IdNotFoundException.KEY_NOT_FOUND_EXCEPTION_SUPPLIER;
import static org.jnosql.diana.api.document.DocumentCondition.eq;
import static org.jnosql.diana.api.document.query.DocumentQueryBuilder.delete;

/**
 * The {@link Repository} template method
 */
public abstract class AbstractDocumentRepository<T, ID> implements Repository<T, ID> {

    protected abstract DocumentTemplate getTemplate();

    protected abstract ClassRepresentation getClassRepresentation();

    protected abstract Reflections getReflections();

    @Override
    public <S extends T> S save(S entity) throws NullPointerException {
        Objects.requireNonNull(entity, "Entity is required");
        Object id = getReflections().getValue(entity, getIdField().getNativeField());
        if (nonNull(id) && existsById((ID) id)) {
            return getTemplate().update(entity);
        } else {
            return getTemplate().insert(entity);
        }
    }

    @Override
    public <S extends T> Iterable<S> save(Iterable<S> entities) throws NullPointerException {
        requireNonNull(entities, "entities is required");
        return StreamSupport.stream(entities.spliterator(), false).map(this::save).collect(toList());
    }


    @Override
    public void deleteById(ID id) throws NullPointerException {
        requireNonNull(id, "is is required");

        String documentName = this.getIdField().getName();
        DocumentDeleteQuery query = delete().from(getClassRepresentation().getName())
                .where(eq(Document.of(documentName, id))).build();
        getTemplate().delete(query);
    }

    @Override
    public void deleteById(Iterable<ID> ids) throws NullPointerException {
        requireNonNull(ids, "ids is required");
        ids.forEach(this::deleteById);
    }

    @Override
    public Optional<T> findById(ID id) throws NullPointerException {
        requireNonNull(id, "id is required");


        return getTemplate().find(getEntityClass(), id);
    }


    @Override
    public Iterable<T> findById(Iterable<ID> ids) throws NullPointerException {
        requireNonNull(ids, "ids is required");
        return (Iterable) stream(ids.spliterator(), false)
                .flatMap(optionalToStream()).collect(Collectors.toList());
    }

    private FieldRepresentation getIdField() {
        return getClassRepresentation().getId().orElseThrow(KEY_NOT_FOUND_EXCEPTION_SUPPLIER);
    }

    private Function optionalToStream() {
        return id -> {
            Optional entity = this.findById((ID) id);
            return entity.isPresent() ? Stream.of(entity.get()) : Stream.empty();
        };
    }

    private Class<T> getEntityClass() {
        return (Class<T>) getClassRepresentation().getClassInstance();
    }

    @Override
    public boolean existsById(ID id) throws NullPointerException {
        return findById(id).isPresent();
    }

}
