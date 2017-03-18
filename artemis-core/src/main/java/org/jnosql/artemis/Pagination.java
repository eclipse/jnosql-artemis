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
package org.jnosql.artemis;

/**
 * Interface for pagination information.
 */
public interface Pagination {

    /**
     * Returns the max number of row in a query
     *
     * @return the limit to be used in a query
     */
    long getLimit();

    /**
     * Gets when the result starts
     *
     * @return the start
     */
    long getStart();

    /**
     * Creates a default pagination
     *
     * @param start the start
     *              {@link org.jnosql.diana.api.document.DocumentQuery#setLimit(long)}
     *              {@link org.jnosql.diana.api.column.ColumnQuery#setLimit(long)}
     * @param limit the limit {@link org.jnosql.diana.api.document.DocumentQuery#setLimit(long)}
     *              {@link org.jnosql.diana.api.column.ColumnQuery#setLimit(long)}
     * @return the pagination instance
     */
    static Pagination of(long start, long limit) {
        return new DefaultPagination(limit, start);
    }
}
