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


import org.jnosql.diana.api.column.ColumnQuery;

/**
 * When a column query is executed this event if fired
 */
public interface ColumnQueryExecute {

    /**
     * The ColumnQuery before executed
     *
     * @return the {@link ColumnQuery}
     */
    ColumnQuery getQuery();


    /**
     * Returns a ColumnQueryExecute instance
     *
     * @param query the query
     * @return a ColumnQueryExecute instance
     */
    static ColumnQueryExecute of(ColumnQuery query) throws NullPointerException {
        return new DefaultColumnQueryExecute(query);
    }
}
