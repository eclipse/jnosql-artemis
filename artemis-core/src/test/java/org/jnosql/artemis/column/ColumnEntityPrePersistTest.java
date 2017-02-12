package org.jnosql.artemis.column;

import org.jnosql.diana.api.column.ColumnEntity;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class ColumnEntityPrePersistTest {

    @Test(expected = NullPointerException.class)
    public void shouldReturnNPEWhenEntityIsNull() {
        ColumnEntityPrePersist.of(null);
    }

    @Test
    public void shouldReturnInstance() {
        ColumnEntity entity = ColumnEntity.of("columnFamily");
        ColumnEntityPrePersist prePersist = ColumnEntityPrePersist.of(entity);
        assertEquals(entity, prePersist.getEntity());
    }
}