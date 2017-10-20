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


import org.jnosql.artemis.RepositoryAsync;
import org.jnosql.artemis.document.DocumentTemplateAsync;
import org.jnosql.artemis.reflection.ClassRepresentation;
import org.jnosql.artemis.reflection.FieldRepresentation;
import org.jnosql.artemis.reflection.Reflections;
import org.jnosql.diana.api.ExecuteAsyncQueryException;
import org.jnosql.diana.api.document.Document;
import org.jnosql.diana.api.document.DocumentDeleteQuery;
import org.jnosql.diana.api.document.DocumentQuery;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNull;
import static org.jnosql.artemis.IdNotFoundException.KEY_NOT_FOUND_EXCEPTION_SUPPLIER;
import static org.jnosql.diana.api.document.DocumentCondition.eq;
import static org.jnosql.diana.api.document.query.DocumentQueryBuilder.delete;
import static org.jnosql.diana.api.document.query.DocumentQueryBuilder.select;

/**
 * The {@link RepositoryAsync} template method
 */
public abstract class AbstractDocumentRepositoryAsync<T, ID> implements RepositoryAsync<T, ID> {


    protected abstract DocumentTemplateAsync getTemplate();

    protected abstract Reflections getReflections();

    protected abstract ClassRepresentation getClassRepresentation();


    @Override
    public <S extends T> void save(S entity) throws ExecuteAsyncQueryException, UnsupportedOperationException, NullPointerException {
        Objects.requireNonNull(entity, "Entity is required");
        Object id = getReflections().getValue(entity, getIdField().getNativeField());
        if (isNull(id)) {
            getTemplate().insert(entity);
            return;
        }

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

        String documentName = this.getIdField().getName();

        DocumentDeleteQuery query = delete().from(getClassRepresentation().getName())
                .where(eq(Document.of(documentName, id)))
                .build();

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

        DocumentQuery query = select().from(getClassRepresentation()
                .getName())
                .where(eq(Document.of(columnName, id))).build();
        getTemplate().singleResult(query, callBack);
    }

    private FieldRepresentation getIdField() {
        return getClassRepresentation().getId().orElseThrow(KEY_NOT_FOUND_EXCEPTION_SUPPLIER);
    }

}
