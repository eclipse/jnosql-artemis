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

import org.jnosql.diana.api.column.ColumnDeleteQuery;

import java.util.Objects;


class DefaultColumnDeleteQueryExecute implements ColumnDeleteQueryExecute {

    private final ColumnDeleteQuery query;

    public DefaultColumnDeleteQueryExecute(ColumnDeleteQuery query) {
        this.query = Objects.requireNonNull(query, "query is required");
    }

    @Override
    public ColumnDeleteQuery getQuery() {
        return query;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ColumnDeleteQueryExecute)) {
            return false;
        }
        ColumnDeleteQueryExecute that = (ColumnDeleteQueryExecute) o;
        return Objects.equals(query, that.getQuery());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(query);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("DefaultColumnDeleteQueryExecute{");
        sb.append("query=").append(query);
        sb.append('}');
        return sb.toString();
    }
}
