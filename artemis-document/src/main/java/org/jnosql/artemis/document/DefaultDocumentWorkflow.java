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


import org.jnosql.diana.api.document.DocumentEntity;

import javax.inject.Inject;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.UnaryOperator;

/**
 * The default implementation of {@link DocumentWorkflow}
 */
class DefaultDocumentWorkflow implements DocumentWorkflow {

    private DocumentEventPersistManager columnEventPersistManager;


    private DocumentEntityConverter converter;

    DefaultDocumentWorkflow() {
    }

    @Inject
    DefaultDocumentWorkflow(DocumentEventPersistManager columnEventPersistManager, DocumentEntityConverter converter) {
        this.columnEventPersistManager = columnEventPersistManager;
        this.converter = converter;
    }

    public <T> T flow(T entity, UnaryOperator<DocumentEntity> action) {

        Function<T, T> flow = getFlow(entity, action);

        return flow.apply(entity);

    }

    private <T> Function<T, T> getFlow(T entity, UnaryOperator<DocumentEntity> action) {
        UnaryOperator<T> validation = t -> Objects.requireNonNull(t, "entity is required");

        UnaryOperator<T> firePreEntity = t -> {
            columnEventPersistManager.firePreEntity(t);
            return t;
        };

        UnaryOperator<T> firePreDocumentEntity = t -> {
            columnEventPersistManager.firePreDocumentEntity(t);
            return t;
        };


        Function<T, DocumentEntity> converterColumn = t -> converter.toDocument(t);

        UnaryOperator<DocumentEntity> firePreDocument = t -> {
            columnEventPersistManager.firePreDocument(t);
            return t;
        };

        UnaryOperator<DocumentEntity> firePostDocument = t -> {
            columnEventPersistManager.firePostDocument(t);
            return t;
        };

        Function<DocumentEntity, T> converterEntity = t -> converter.toEntity((Class<T>) entity.getClass(), t);

        UnaryOperator<T> firePostEntity = t -> {
            columnEventPersistManager.firePostEntity(t);
            return t;
        };

        UnaryOperator<T> firePostDocumentEntity = t -> {
            columnEventPersistManager.firePostDocumentEntity(t);
            return t;
        };


        return validation
                .andThen(firePreEntity)
                .andThen(firePreDocumentEntity)
                .andThen(converterColumn)
                .andThen(firePreDocument)
                .andThen(action)
                .andThen(firePostDocument)
                .andThen(converterEntity)
                .andThen(firePostEntity)
                .andThen(firePostDocumentEntity);
    }
}
