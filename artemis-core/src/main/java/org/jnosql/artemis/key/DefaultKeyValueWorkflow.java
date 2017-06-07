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
package org.jnosql.artemis.key;

import org.jnosql.diana.api.key.KeyValueEntity;

import javax.inject.Inject;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.UnaryOperator;


/**
 * Default implentation of {@link KeyValueWorkflow}
 */
class DefaultKeyValueWorkflow implements KeyValueWorkflow {

    private KeyValueEventPersistManager eventPersistManager;


    private KeyValueEntityConverter converter;

    DefaultKeyValueWorkflow() {
    }

    @Inject
    DefaultKeyValueWorkflow(KeyValueEventPersistManager eventPersistManager, KeyValueEntityConverter converter) {
        this.eventPersistManager = eventPersistManager;
        this.converter = converter;
    }

    public <T> T flow(T entity, UnaryOperator<KeyValueEntity<?>> action) {

        Function<T, T> flow = getFlow(entity, action);

        return flow.apply(entity);

    }

    private <T> Function<T, T> getFlow(T entity, UnaryOperator<KeyValueEntity<?>> action) {
        UnaryOperator<T> validation = t -> Objects.requireNonNull(t, "entity is required");

        UnaryOperator<T> firePreEntity = t -> {
            eventPersistManager.firePreEntity(t);
            return t;
        };

        UnaryOperator<T> firePreKeyValueEntity = t -> {
            eventPersistManager.firePreKeyValueEntity(t);
            return t;
        };

        Function<T, KeyValueEntity<?>> converterColumn = t -> converter.toKeyValue(t);

        UnaryOperator<KeyValueEntity<?>> firePreDocument = t -> {
            eventPersistManager.firePreKeyValue(t);
            return t;
        };

        UnaryOperator<KeyValueEntity<?>> firePostDocument = t -> {
            eventPersistManager.firePostKeyValue(t);
            return t;
        };

        Function<KeyValueEntity<?>, T> converterEntity = t -> converter.toEntity((Class<T>) entity.getClass(), t);

        UnaryOperator<T> firePostEntity = t -> {
            eventPersistManager.firePostEntity(t);
            return t;
        };

        UnaryOperator<T> firePostKeyValueEntity = t -> {
            eventPersistManager.firePostKeyValueEntity(t);
            return t;
        };


        return validation
                .andThen(firePreEntity)
                .andThen(firePreKeyValueEntity)
                .andThen(converterColumn)
                .andThen(firePreDocument)
                .andThen(action)
                .andThen(firePostDocument)
                .andThen(converterEntity)
                .andThen(firePostEntity)
                .andThen(firePostKeyValueEntity);
    }
}
