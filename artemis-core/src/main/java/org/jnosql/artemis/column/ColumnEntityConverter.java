package org.jnosql.artemis.column;

import org.jnosql.diana.api.column.ColumnFamilyEntity;


public interface ColumnEntityConverter {

    ColumnFamilyEntity toColumn(Object entityInstance);

    <T> T toEntity(Class<T> entityClass, ColumnFamilyEntity entity);

    <T> T toEntity(ColumnFamilyEntity entity);
}
