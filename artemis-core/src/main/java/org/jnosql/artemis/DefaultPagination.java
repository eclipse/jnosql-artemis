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


import java.util.Objects;

class DefaultPagination implements Pagination {

    private final long limit;

    private final long start;

    DefaultPagination(long limit, long start) {
        this.limit = limit;
        this.start = start;
    }

    @Override
    public long getLimit() {
        return limit;
    }

    @Override
    public long getStart() {
        return start;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || Pagination.class != o.getClass()) {
            return false;
        }
        Pagination that = (Pagination) o;
        return limit == that.getLimit() &&
                start == that.getStart();
    }

    @Override
    public int hashCode() {
        return Objects.hash(limit, start);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("DefaultPagination{");
        sb.append("limit=").append(limit);
        sb.append(", start=").append(start);
        sb.append('}');
        return sb.toString();
    }
}
