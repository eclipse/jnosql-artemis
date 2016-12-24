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
package org.jnosql.artemis.column;


import org.jnosql.diana.api.column.ColumnEntity;

import java.util.function.UnaryOperator;

/**
 * This implementation defines the workflow to save an Entity on {@link ColumnCrudOperation}.
 * The default implementation follows:
 *  <p>{@link ColumnEventPersistManager#firePreEntity(Object)}</p>
 *  <p>{@link ColumnEntityConverter#toColumn(Object)}</p>
 *  <p>{@link ColumnEventPersistManager#firePreDocument(ColumnEntity)}</p>
 *  <p>Database alteration</p>
 *  <p>{@link ColumnEventPersistManager#firePostDocument(ColumnEntity)}</p>
 *  <p>{@link ColumnEventPersistManager#firePostEntity(Object)}</p>
 */
public interface ColumnWorkflow {

    /**
     * Executes the workflow to do an interaction on a database column family.
     *
     * @param entity the entity to be saved
     * @param action the alteration to be executed on database
     * @param <T>    the entity type
     * @return after the workflow the the entity response
     * @see {@link ColumnCrudOperation#save(Object, java.time.Duration)} {@link ColumnCrudOperation#save(Object)}
     * {@link ColumnCrudOperation#update(Object)}
     */
    <T> T flow(T entity, UnaryOperator<ColumnEntity> action) throws NullPointerException;
}
