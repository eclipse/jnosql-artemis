/*
 * Copyright 2017 Otavio Santana and others
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jnosql.artemis.key;


import org.jnosql.diana.api.key.KeyValueEntity;

import java.util.Objects;

public interface KeyValueEntityPrePersist {

    /**
     * The {@link org.jnosql.diana.api.key.KeyValueEntity}  after be saved
     *
     * @return the {@link KeyValueEntity} instance
     */
    KeyValueEntity<?> getEntity();

    /**
     * Creates the {@link KeyValueEntityPrePersist} instance
     *
     * @param entity the entity
     * @return {@link KeyValueEntityPrePersist} instance
     * @throws NullPointerException when the entity is null
     */
    static <T> KeyValueEntityPrePersist of(KeyValueEntity<T> entity) throws NullPointerException {
        Objects.requireNonNull(entity, "Entity is required");
        return new DefaultKeyValueEntityPrePersist(entity);
    }
}
