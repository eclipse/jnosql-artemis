package org.jnosql.artemis.column;


import java.util.Objects;
import org.jnosql.diana.api.column.ColumnFamilyEntity;

public interface ColumnEntityPostPersist {

    ColumnFamilyEntity getEntity();

    static ColumnEntityPostPersist of(ColumnFamilyEntity entity) throws NullPointerException {
        Objects.requireNonNull(entity, "Entity is required");
        return new DefaultColumnEntityPersist(entity);
    }

}
