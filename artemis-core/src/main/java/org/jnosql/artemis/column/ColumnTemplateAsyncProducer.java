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
package org.jnosql.artemis.column;


import org.jnosql.diana.api.column.ColumnFamilyManagerAsync;

/**
 * The producer of {@link ColumnTemplate}
 *
 * @param <T> the ColumnTemplate instance
 */
public interface ColumnTemplateAsyncProducer<T extends ColumnTemplateAsync> {

    /**
     * creates a {@link ColumnFamilyManagerAsync}
     *
     * @param columnFamilyManager the columnFamilyManager
     * @return a new instance
     * @throws NullPointerException when columnFamilyManager is null
     */
    T get(ColumnFamilyManagerAsync columnFamilyManager) throws NullPointerException;

}
