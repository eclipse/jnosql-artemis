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


import org.jnosql.diana.api.document.DocumentEntity;
import org.jnosql.diana.api.key.KeyValueEntity;

/**
 * This interface represents the converter between an entity and the {@link KeyValueEntity}
 */
public interface KeyValueEntityConverter {

    /**
     * Converts the instance entity to {@link DocumentEntity}
     *
     * @param entityInstance the instnace
     * @return a {@link DocumentEntity} instance
     * @throws KeyNotFoundException when the entityInstance hasn't a field with {@link org.jnosql.artemis.Key}
     * @throws NullPointerException when the entityInstance is null
     */
    <T> KeyValueEntity<T> toKeyValue(Object entityInstance) throws KeyNotFoundException, NullPointerException;

    /**
     * Converts a {@link KeyValueEntity} to entity
     *
     * @param entityClass the entity class
     * @param entity      the {@link KeyValueEntity} to be converted
     * @param <T>         the entity type
     * @return the instance from {@link KeyValueEntity}
     * @throws KeyNotFoundException when the entityInstance hasn't a field with {@link org.jnosql.artemis.Key}
     * @throws NullPointerException when the entityInstance is null
     */
    <T> T toEntity(Class<T> entityClass, KeyValueEntity<?> entity) throws KeyNotFoundException, NullPointerException;

}
