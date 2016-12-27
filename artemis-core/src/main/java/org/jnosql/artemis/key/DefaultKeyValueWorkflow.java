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


        return validation
                .andThen(firePreEntity)
                .andThen(converterColumn)
                .andThen(firePreDocument)
                .andThen(action)
                .andThen(firePostDocument)
                .andThen(converterEntity)
                .andThen(firePostEntity);
    }
}
