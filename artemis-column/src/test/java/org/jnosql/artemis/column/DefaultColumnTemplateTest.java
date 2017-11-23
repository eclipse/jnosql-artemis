/*
 *  Copyright (c) 2017 Otávio Santana and others
 *   All rights reserved. This program and the accompanying materials
 *   are made available under the terms of the Eclipse Public License v1.0
 *   and Apache License v2.0 which accompanies this distribution.
 *   The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 *   and the Apache License v2.0 is available at http://www.opensource.org/licenses/apache2.0.php.
 *
 *   You may elect to redistribute this code under either of these licenses.
 *
 *   Contributors:
 *
 *   Otavio Santana
 */
package org.jnosql.artemis.column;

import org.jnosql.artemis.CDIJUnitRunner;
import org.jnosql.artemis.IdNotFoundException;
import org.jnosql.artemis.model.Job;
import org.jnosql.artemis.model.Person;
import org.jnosql.artemis.reflection.ClassRepresentations;
import org.jnosql.diana.api.column.Column;
import org.jnosql.diana.api.column.ColumnCondition;
import org.jnosql.diana.api.column.ColumnDeleteQuery;
import org.jnosql.diana.api.column.ColumnEntity;
import org.jnosql.diana.api.column.ColumnFamilyManager;
import org.jnosql.diana.api.column.ColumnQuery;
import org.jnosql.diana.api.column.query.ColumnQueryBuilder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.time.Duration;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;

@RunWith(CDIJUnitRunner.class)
public class DefaultColumnTemplateTest {

    private Person person = Person.builder().
            withAge().
            withPhones(Arrays.asList("234", "432")).
            withName("Name")
            .withId(19)
            .withIgnore().build();

    private Column[] columns = new Column[]{
            Column.of("age", 10),
            Column.of("phones", Arrays.asList("234", "432")),
            Column.of("name", "Name"),
            Column.of("id", 19L),
    };


    @Inject
    private ColumnEntityConverter converter;

    @Inject
    private ClassRepresentations classRepresentations;

    private ColumnFamilyManager managerMock;

    private DefaultColumnTemplate subject;

    private ArgumentCaptor<ColumnEntity> captor;

    private ColumnEventPersistManager columnEventPersistManager;

    @SuppressWarnings("unchecked")
    @Before
    public void setUp() {
        managerMock = Mockito.mock(ColumnFamilyManager.class);
        columnEventPersistManager = Mockito.mock(ColumnEventPersistManager.class);
        captor = ArgumentCaptor.forClass(ColumnEntity.class);
        Instance<ColumnFamilyManager> instance = Mockito.mock(Instance.class);
        Mockito.when(instance.get()).thenReturn(managerMock);
        this.subject = new DefaultColumnTemplate(converter, instance, new DefaultColumnWorkflow(columnEventPersistManager, converter),
                columnEventPersistManager, classRepresentations);
    }

    @Test
    public void shouldInsert() {
        ColumnEntity document = ColumnEntity.of("Person");
        document.addAll(Stream.of(columns).collect(Collectors.toList()));

        Mockito.when(managerMock
                .insert(Mockito.any(ColumnEntity.class)))
                .thenReturn(document);

        subject.insert(this.person);
        verify(managerMock).insert(captor.capture());
        verify(columnEventPersistManager).firePostEntity(Mockito.any(Person.class));
        verify(columnEventPersistManager).firePreEntity(Mockito.any(Person.class));
        verify(columnEventPersistManager).firePreColumn(Mockito.any(ColumnEntity.class));
        verify(columnEventPersistManager).firePostColumn(Mockito.any(ColumnEntity.class));
        ColumnEntity value = captor.getValue();
        assertEquals("Person", value.getName());
        assertEquals(4, value.getColumns().size());
    }


    @Test
    public void shouldInsertTTL() {
        ColumnEntity document = ColumnEntity.of("Person");
        document.addAll(Stream.of(columns).collect(Collectors.toList()));

        Mockito.when(managerMock
                .insert(Mockito.any(ColumnEntity.class),
                        Mockito.any(Duration.class)))
                .thenReturn(document);

        subject.insert(this.person, Duration.ofHours(2));
        verify(managerMock).insert(captor.capture(), Mockito.eq(Duration.ofHours(2)));
        verify(columnEventPersistManager).firePostEntity(Mockito.any(Person.class));
        verify(columnEventPersistManager).firePreEntity(Mockito.any(Person.class));
        verify(columnEventPersistManager).firePreColumn(Mockito.any(ColumnEntity.class));
        verify(columnEventPersistManager).firePostColumn(Mockito.any(ColumnEntity.class));
        ColumnEntity value = captor.getValue();
        assertEquals("Person", value.getName());
        assertEquals(4, value.getColumns().size());
    }

    @Test
    public void shouldUpdate() {
        ColumnEntity document = ColumnEntity.of("Person");
        document.addAll(Stream.of(columns).collect(Collectors.toList()));

        Mockito.when(managerMock
                .update(Mockito.any(ColumnEntity.class)))
                .thenReturn(document);

        subject.update(this.person);
        verify(managerMock).update(captor.capture());
        verify(columnEventPersistManager).firePostEntity(Mockito.any(Person.class));
        verify(columnEventPersistManager).firePreEntity(Mockito.any(Person.class));
        verify(columnEventPersistManager).firePreColumn(Mockito.any(ColumnEntity.class));
        verify(columnEventPersistManager).firePostColumn(Mockito.any(ColumnEntity.class));
        ColumnEntity value = captor.getValue();
        assertEquals("Person", value.getName());
        assertEquals(4, value.getColumns().size());
    }

    @Test
    public void shouldDelete() {

        ColumnDeleteQuery query = ColumnQueryBuilder.delete().from("delete").build();
        subject.delete(query);
        verify(managerMock).delete(query);
    }

    @Test
    public void shouldSelect() {
        ColumnQuery query = ColumnQueryBuilder.select().from("delete").build();
        subject.select(query);
        verify(managerMock).select(query);
    }

    @Test(expected = NullPointerException.class)
    public void shouldReturnErrorWhenFindIdHasIdNull() {
        subject.find(Person.class, null);
    }

    @Test(expected = NullPointerException.class)
    public void shouldReturnErrorWhenFindIdHasClassNull() {
        subject.find(null, "10");
    }

    @Test(expected = IdNotFoundException.class)
    public void shouldReturnErrorWhenThereIsNotIdInFind() {
        subject.find(Job.class, "10");
    }

    @Test
    public void shouldReturnFind() {
        subject.find(Person.class, "10");
        ArgumentCaptor<ColumnQuery> queryCaptor = ArgumentCaptor.forClass(ColumnQuery.class);
        verify(managerMock).select(queryCaptor.capture());
        ColumnQuery query = queryCaptor.getValue();
        ColumnCondition condition = query.getCondition().get();

        assertEquals("Person", query.getColumnFamily());
        assertEquals(ColumnCondition.eq(Column.of("_id", "10")), condition);

    }
}