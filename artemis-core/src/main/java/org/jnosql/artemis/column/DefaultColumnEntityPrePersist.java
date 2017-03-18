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


import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.jnosql.diana.api.column.ColumnEntity;

import java.util.Objects;

/**
 * The default implementation to represents {@link ColumnEntityPrePersist}
 */
class DefaultColumnEntityPrePersist implements ColumnEntityPrePersist {

    private final ColumnEntity entity;

    DefaultColumnEntityPrePersist(ColumnEntity entity) {
        this.entity = entity;
    }

    @Override
    public ColumnEntity getEntity() {
        return entity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DefaultColumnEntityPrePersist)) {
            return false;
        }
        DefaultColumnEntityPrePersist that = (DefaultColumnEntityPrePersist) o;
        return Objects.equals(entity, that.entity);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(entity);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("entity", entity)
                .toString();
    }
}
