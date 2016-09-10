package org.jnosql.artemis.column;


import org.jnosql.diana.api.column.ColumnFamilyEntity;

public interface ColumnEventPersistManager {

    void firePreDocument(ColumnFamilyEntity entity);

    void firePostDocument(ColumnFamilyEntity entity);

    <T> void firePreEntity(T entity);

    <T> void firePostEntity(T entity);

}
