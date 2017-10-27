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


import org.jnosql.diana.api.column.ColumnEntity;

import javax.inject.Inject;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.UnaryOperator;

class DefaultColumnWorkflow implements ColumnWorkflow {


    private ColumnEventPersistManager columnEventPersistManager;


    private ColumnEntityConverter converter;

    DefaultColumnWorkflow() {
    }

    @Inject
    DefaultColumnWorkflow(ColumnEventPersistManager columnEventPersistManager, ColumnEntityConverter converter) {
        this.columnEventPersistManager = columnEventPersistManager;
        this.converter = converter;
    }

    public <T> T flow(T entity, UnaryOperator<ColumnEntity> action) {

        Function<T, T> flow = getFlow(entity, action);

        return flow.apply(entity);

    }

    private <T> Function<T, T> getFlow(T entity, UnaryOperator<ColumnEntity> action) {
        UnaryOperator<T> validation = t -> Objects.requireNonNull(t, "entity is required");

        UnaryOperator<T> firePreEntity = t -> {
            columnEventPersistManager.firePreEntity(t);
            return t;
        };

        UnaryOperator<T> firePreColumnEntity = t -> {
            columnEventPersistManager.firePreColumnEntity(t);
            return t;
        };

        Function<T, ColumnEntity> converterColumn = t -> converter.toColumn(t);

        UnaryOperator<ColumnEntity> firePreDocument = t -> {
            columnEventPersistManager.firePreColumn(t);
            return t;
        };

        UnaryOperator<ColumnEntity> firePostDocument = t -> {
            columnEventPersistManager.firePostColumn(t);
            return t;
        };

        Function<ColumnEntity, T> converterEntity = t -> converter.toEntity((Class<T>) entity.getClass(), t);

        UnaryOperator<T> firePostEntity = t -> {
            columnEventPersistManager.firePostEntity(t);
            return t;
        };

        UnaryOperator<T> firePostColumnEntity = t -> {
            columnEventPersistManager.firePostColumnEntity(t);
            return t;
        };

        return validation
                .andThen(firePreEntity)
                .andThen(firePreColumnEntity)
                .andThen(converterColumn)
                .andThen(firePreDocument)
                .andThen(action)
                .andThen(firePostDocument)
                .andThen(converterEntity)
                .andThen(firePostEntity)
                .andThen(firePostColumnEntity);
    }
}
