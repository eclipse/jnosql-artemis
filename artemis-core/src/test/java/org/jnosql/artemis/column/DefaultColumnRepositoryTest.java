/*
 * Copyright 2017 Eclipse Foundation
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jnosql.artemis.column;

import org.jnosql.artemis.WeldJUnit4Runner;
import org.jnosql.artemis.model.Person;
import org.jnosql.diana.api.column.Column;
import org.jnosql.diana.api.column.ColumnDeleteQuery;
import org.jnosql.diana.api.column.ColumnEntity;
import org.jnosql.diana.api.column.ColumnFamilyManager;
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

@RunWith(WeldJUnit4Runner.class)
public class DefaultColumnRepositoryTest {

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

    private ColumnFamilyManager managerMock;

    private DefaultColumnRepository subject;

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
        this.subject = new DefaultColumnRepository(converter, instance, new DefaultColumnWorkflow(columnEventPersistManager, converter));
    }

    @Test
    public void shouldSave() {
        ColumnEntity document = ColumnEntity.of("Person");
        document.addAll(Stream.of(columns).collect(Collectors.toList()));

        Mockito.when(managerMock
                .save(Mockito.any(ColumnEntity.class)))
                .thenReturn(document);

        subject.save(this.person);
        verify(managerMock).save(captor.capture());
        verify(columnEventPersistManager).firePostEntity(Mockito.any(Person.class));
        verify(columnEventPersistManager).firePreEntity(Mockito.any(Person.class));
        verify(columnEventPersistManager).firePreColumn(Mockito.any(ColumnEntity.class));
        verify(columnEventPersistManager).firePostColumn(Mockito.any(ColumnEntity.class));
        ColumnEntity value = captor.getValue();
        assertEquals("Person", value.getName());
        assertEquals(4, value.getColumns().size());
    }


    @Test
    public void shouldSaveTTL() {
        ColumnEntity document = ColumnEntity.of("Person");
        document.addAll(Stream.of(columns).collect(Collectors.toList()));

        Mockito.when(managerMock
                .save(Mockito.any(ColumnEntity.class),
                        Mockito.any(Duration.class)))
                .thenReturn(document);

        subject.save(this.person, Duration.ofHours(2));
        verify(managerMock).save(captor.capture(), Mockito.eq(Duration.ofHours(2)));
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
        ColumnDeleteQuery query = ColumnDeleteQuery.of("delete");
        subject.delete(query);
        verify(managerMock).delete(query);
    }
}