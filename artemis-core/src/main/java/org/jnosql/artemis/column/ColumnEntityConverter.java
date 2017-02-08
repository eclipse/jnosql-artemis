/*
 * Copyright 2017 Eclipse Foundation
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
package org.jnosql.artemis.column;

import org.jnosql.diana.api.column.ColumnEntity;


/**
 * This interface represents the converter between an entity and the {@link ColumnEntity}
 */
public interface ColumnEntityConverter {

    /**
     * Converts the instance entity to {@link ColumnEntity}
     *
     * @param entityInstance the instnace
     * @return a {@link ColumnEntity} instance
     */
    ColumnEntity toColumn(Object entityInstance);

    /**
     * Converts a {@link ColumnEntity} to entity
     *
     * @param entityClass the entity class
     * @param entity      the {@link ColumnEntity} to be converted
     * @param <T>         the entity type
     * @return the instance from {@link ColumnEntity}
     */
    <T> T toEntity(Class<T> entityClass, ColumnEntity entity);

    /**
     * Similar to {@link ColumnEntityConverter#toEntity(Class, ColumnEntity)}, but
     * search the instance type from {@link ColumnEntity#getName()}
     *
     * @param entity the {@link ColumnEntity} to be converted
     * @param <T>    the entity type
     * @return the instance from {@link ColumnEntity}
     */
    <T> T toEntity(ColumnEntity entity);
}
