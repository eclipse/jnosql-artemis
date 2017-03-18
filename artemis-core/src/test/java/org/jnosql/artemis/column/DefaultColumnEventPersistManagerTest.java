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

import org.jnosql.artemis.EntityPostPersit;
import org.jnosql.artemis.EntityPrePersist;
import org.jnosql.diana.api.column.ColumnDeleteQuery;
import org.jnosql.diana.api.column.ColumnEntity;
import org.jnosql.diana.api.column.ColumnQuery;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.enterprise.event.Event;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class DefaultColumnEventPersistManagerTest {


    @InjectMocks
    private DefaultColumnEventPersistManager subject;

    @Mock
    private Event<ColumnEntityPrePersist> documentEntityPrePersistEvent;

    @Mock
    private Event<ColumnEntityPostPersist> documentEntityPostPersistEvent;

    @Mock
    private Event<EntityPrePersist> entityPrePersistEvent;

    @Mock
    private Event<EntityPostPersit> entityPostPersitEvent;

    @Mock
    private Event<EntityColumnPrePersist> entityColumnPrePersist;

    @Mock
    private Event<EntityColumnPostPersist> entityColumnPostPersist;

    @Mock
    private Event<ColumnQueryExecute> columnQueryExecute;

    @Mock
    private Event<ColumnDeleteQueryExecute> columnDeleteQueryExecute;


    @Test
    public void shouldFirePreColumn() {
        ColumnEntity entity = ColumnEntity.of("columnFamily");
        subject.firePreColumn(entity);
        ArgumentCaptor<ColumnEntityPrePersist> captor = ArgumentCaptor.forClass(ColumnEntityPrePersist.class);
        verify(documentEntityPrePersistEvent).fire(captor.capture());

        ColumnEntityPrePersist captorValue = captor.getValue();
        assertEquals(entity, captorValue.getEntity());
    }


    @Test
    public void shouldFirePostColumn() {
        ColumnEntity entity = ColumnEntity.of("columnFamily");
        subject.firePostColumn(entity);
        ArgumentCaptor<ColumnEntityPostPersist> captor = ArgumentCaptor.forClass(ColumnEntityPostPersist.class);
        verify(documentEntityPostPersistEvent).fire(captor.capture());

        ColumnEntityPostPersist captorValue = captor.getValue();
        assertEquals(entity, captorValue.getEntity());
    }

    @Test
    public void shouldFirePreEntity() {
        Jedi jedi = new Jedi();
        jedi.name = "Luke";
        subject.firePreEntity(jedi);
        ArgumentCaptor<EntityPrePersist> captor = ArgumentCaptor.forClass(EntityPrePersist.class);
        verify(entityPrePersistEvent).fire(captor.capture());
        EntityPrePersist value = captor.getValue();
        assertEquals(jedi, value.getValue());
    }

    @Test
    public void shouldFirePostEntity() {
        Jedi jedi = new Jedi();
        jedi.name = "Luke";
        subject.firePostEntity(jedi);
        ArgumentCaptor<EntityPostPersit> captor = ArgumentCaptor.forClass(EntityPostPersit.class);
        verify(entityPostPersitEvent).fire(captor.capture());
        EntityPostPersit value = captor.getValue();
        assertEquals(jedi, value.getValue());
    }

    @Test
    public void shouldFirePreColumnEntity() {
        Jedi jedi = new Jedi();
        jedi.name = "Luke";
        subject.firePreColumnEntity(jedi);
        ArgumentCaptor<EntityColumnPrePersist> captor = ArgumentCaptor.forClass(EntityColumnPrePersist.class);
        verify(entityColumnPrePersist).fire(captor.capture());
        EntityColumnPrePersist value = captor.getValue();
        assertEquals(jedi, value.getValue());
    }

    @Test
    public void shouldFirePostColumnEntity() {
        Jedi jedi = new Jedi();
        jedi.name = "Luke";
        subject.firePostColumnEntity(jedi);
        ArgumentCaptor<EntityColumnPostPersist> captor = ArgumentCaptor.forClass(EntityColumnPostPersist.class);
        verify(entityColumnPostPersist).fire(captor.capture());
        EntityColumnPostPersist value = captor.getValue();
        assertEquals(jedi, value.getValue());
    }

    @Test
    public void shouldFirePreQuery() {
        ColumnQuery query = ColumnQuery.of("person");
        subject.firePreQuery(query);
        ArgumentCaptor<ColumnQueryExecute> captor = ArgumentCaptor.forClass(ColumnQueryExecute.class);
        verify(columnQueryExecute).fire(captor.capture());
        assertEquals(query, captor.getValue().getQuery());
    }

    @Test
    public void shouldFirePreDeleteQuery() {
        ColumnDeleteQuery query = ColumnDeleteQuery.of("person");
        subject.firePreDeleteQuery(query);
        ArgumentCaptor<ColumnDeleteQueryExecute> captor = ArgumentCaptor.forClass(ColumnDeleteQueryExecute.class);
        verify(columnDeleteQueryExecute).fire(captor.capture());
        assertEquals(query, captor.getValue().getQuery());
    }


    class Jedi {
        private String name;
    }
}