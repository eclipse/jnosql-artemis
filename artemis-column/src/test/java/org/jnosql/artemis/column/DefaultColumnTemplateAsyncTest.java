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
import org.jnosql.artemis.model.Person;
import org.jnosql.artemis.reflection.ClassRepresentations;
import org.jnosql.diana.api.column.Column;
import org.jnosql.diana.api.column.ColumnDeleteQuery;
import org.jnosql.diana.api.column.ColumnEntity;
import org.jnosql.diana.api.column.ColumnFamilyManagerAsync;
import org.jnosql.diana.api.column.ColumnQuery;
import org.jnosql.diana.api.column.query.ColumnQueryBuilder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.jnosql.diana.api.column.query.ColumnQueryBuilder.delete;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;

@RunWith(CDIJUnitRunner.class)
public class DefaultColumnTemplateAsyncTest {

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

    private ColumnFamilyManagerAsync managerMock;

    private DefaultColumnTemplateAsync subject;

    private ArgumentCaptor<ColumnEntity> captor;

    @SuppressWarnings("unchecked")
    @Before
    public void setUp() {
        managerMock = Mockito.mock(ColumnFamilyManagerAsync.class);
        ColumnEventPersistManager columnEventPersistManager = Mockito.mock(ColumnEventPersistManager.class);
        captor = ArgumentCaptor.forClass(ColumnEntity.class);
        Instance<ColumnFamilyManagerAsync> instance = Mockito.mock(Instance.class);
        Mockito.when(instance.get()).thenReturn(managerMock);
        this.subject = new DefaultColumnTemplateAsync(converter, instance, classRepresentations);
    }

    @Test
    public void shouldInsert() {
        ColumnEntity document = ColumnEntity.of("Person");
        document.addAll(Stream.of(columns).collect(Collectors.toList()));


        subject.insert(this.person);
        verify(managerMock).insert(captor.capture(), Mockito.any(Consumer.class));
        ColumnEntity value = captor.getValue();
        assertEquals(document.getName(), value.getName());
    }


    @Test
    public void shouldInsertTTL() {
        ColumnEntity document = ColumnEntity.of("Person");
        document.addAll(Stream.of(columns).collect(Collectors.toList()));


        subject.insert(this.person);
        verify(managerMock).insert(Mockito.any(ColumnEntity.class), Mockito.any(Consumer.class));
    }

    @Test
    public void shouldUpdate() {
        ColumnEntity document = ColumnEntity.of("Person");
        document.addAll(Stream.of(columns).collect(Collectors.toList()));


        subject.update(this.person);
        verify(managerMock).update(captor.capture(), Mockito.any(Consumer.class));
        ColumnEntity value = captor.getValue();
        assertEquals(document.getName(), value.getName());
    }

    @Test
    public void shouldDelete() {

        ColumnDeleteQuery query = delete().from("delete").build();
        subject.delete(query);
        verify(managerMock).delete(query);
    }

    @Test
    public void shouldSelect() {

        ColumnQuery query = ColumnQueryBuilder.select().from("Person").build();
        Consumer<List<Person>> callback = l -> {

        };
        subject.select(query, callback);
        verify(managerMock).select(Mockito.eq(query), Mockito.any());
    }
}