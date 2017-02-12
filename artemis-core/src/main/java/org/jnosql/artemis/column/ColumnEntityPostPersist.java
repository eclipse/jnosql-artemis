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

import java.util.Objects;

/**
 * The interface represents the model when the {@link ColumnEntity} be saved that  event will fired.
 */
public interface ColumnEntityPostPersist {

    /**
     * The {@link ColumnEntity}  after be saved
     *
     * @return the {@link ColumnEntity} instance
     */
    ColumnEntity getEntity();

    /**
     * Creates the {@link ColumnEntityPostPersist} instance
     *
     * @param entity the entity
     * @return {@link ColumnEntityPostPersist} instance
     * @throws NullPointerException when the entity is null
     */
    static ColumnEntityPostPersist of(ColumnEntity entity) throws NullPointerException {
        Objects.requireNonNull(entity, "Entity is required");
        return new DefaultColumnEntityPersist(entity);
    }

}
