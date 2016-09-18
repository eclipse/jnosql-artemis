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
package org.jnosql.artemis.document;

import org.jnosql.diana.api.document.DocumentEntity;

/**
 * This interface represents the converter between an entity and the {@link DocumentEntity}
 */
public interface DocumentEntityConverter {

    /**
     * Converts the instance entity to {@link DocumentEntity}
     *
     * @param entityInstance the instnace
     * @return a {@link DocumentEntity} instance
     */
    DocumentEntity toDocument(Object entityInstance);

    /**
     * Converts a {@link DocumentEntity} to entity
     *
     * @param entityClass the entity class
     * @param entity      the {@link DocumentEntity} to be converted
     * @param <T>         the entity type
     * @return the instance from {@link DocumentEntity}
     */
    <T> T toEntity(Class<T> entityClass, DocumentEntity entity);

    /**
     * Similar to {@link DocumentEntityConverter#toEntity(Class, DocumentEntity)}, but
     * search the instance type from {@link DocumentEntity#getName()}
     *
     * @param entity the {@link DocumentEntity} to be converted
     * @param <T>    the entity type
     * @return the instance from {@link DocumentEntity}
     */
    <T> T toEntity(DocumentEntity entity);
}
