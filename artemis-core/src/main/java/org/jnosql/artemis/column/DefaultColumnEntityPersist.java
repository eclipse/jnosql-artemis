package org.jnosql.artemis.column;


import java.util.Objects;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.jnosql.diana.api.column.ColumnFamilyEntity;

class DefaultColumnEntityPersist implements ColumnEntityPostPersist, ColumnEntityPrePersist {

    private final ColumnFamilyEntity entity;

    DefaultColumnEntityPersist(ColumnFamilyEntity entity) {
        this.entity = entity;
    }

    @Override
    public ColumnFamilyEntity getEntity() {
        return entity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DefaultColumnEntityPersist)) {
            return false;
        }
        DefaultColumnEntityPersist that = (DefaultColumnEntityPersist) o;
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
