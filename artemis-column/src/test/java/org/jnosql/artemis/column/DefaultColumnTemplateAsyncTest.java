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

import org.awaitility.Awaitility;
import org.jnosql.artemis.CDIExtension;
import org.jnosql.artemis.Converters;
import org.jnosql.artemis.model.Person;
import org.jnosql.artemis.reflection.ClassRepresentations;
import org.jnosql.diana.api.column.Column;
import org.jnosql.diana.api.column.ColumnCondition;
import org.jnosql.diana.api.column.ColumnDeleteQuery;
import org.jnosql.diana.api.column.ColumnEntity;
import org.jnosql.diana.api.column.ColumnFamilyManagerAsync;
import org.jnosql.diana.api.column.ColumnQuery;
import org.jnosql.diana.api.column.query.ColumnQueryBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.w3c.dom.css.DocumentCSS;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Collections.singletonList;
import static org.awaitility.Awaitility.await;
import static org.jnosql.diana.api.column.query.ColumnQueryBuilder.delete;
import static org.jnosql.diana.api.column.query.ColumnQueryBuilder.select;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(CDIExtension.class)
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

    @Inject
    private Converters converters;

    private ColumnFamilyManagerAsync managerMock;

    private DefaultColumnTemplateAsync subject;

    private ArgumentCaptor<ColumnEntity> captor;

    @SuppressWarnings("unchecked")
    @BeforeEach
    public void setUp() {
        managerMock = Mockito.mock(ColumnFamilyManagerAsync.class);
        ColumnEventPersistManager columnEventPersistManager = Mockito.mock(ColumnEventPersistManager.class);
        captor = ArgumentCaptor.forClass(ColumnEntity.class);
        Instance<ColumnFamilyManagerAsync> instance = Mockito.mock(Instance.class);
        Mockito.when(instance.get()).thenReturn(managerMock);
        this.subject = new DefaultColumnTemplateAsync(converter, instance, classRepresentations, converters);
    }


    @Test
    public void shouldCheckNullParameterInInsert() {

        assertThrows(NullPointerException.class, () -> subject.insert(null));
        assertThrows(NullPointerException.class, () -> subject.insert((Iterable) null));
        assertThrows(NullPointerException.class, () -> subject.insert(this.person, (Duration) null));
        assertThrows(NullPointerException.class, () -> subject.insert(null, Duration.ofSeconds(1L)));
        assertThrows(NullPointerException.class, () -> subject.insert(this.person, (Consumer<Person>) null));
        assertThrows(NullPointerException.class, () -> subject.insert(null, System.out::println));

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


        subject.insert(this.person, Duration.ofSeconds(1L));
        verify(managerMock).insert(Mockito.any(ColumnEntity.class), Mockito.eq(Duration.ofSeconds(1L)), Mockito.any(Consumer.class));
    }

    @Test
    public void shouldInsertIterable() {
        ColumnEntity document = ColumnEntity.of("Person");
        document.addAll(Stream.of(columns).collect(Collectors.toList()));

        subject.insert(singletonList(this.person));
        verify(managerMock).insert(captor.capture(), Mockito.any(Consumer.class));
        ColumnEntity value = captor.getValue();
        assertEquals(document.getName(), value.getName());
    }

    @Test
    public void shouldInsertIterableTTL() {
        ColumnEntity document = ColumnEntity.of("Person");
        document.addAll(Stream.of(columns).collect(Collectors.toList()));

        subject.insert(singletonList(this.person), Duration.ofSeconds(1L));
        verify(managerMock).insert(Mockito.any(ColumnEntity.class), Mockito.eq(Duration.ofSeconds(1L)), Mockito.any(Consumer.class));
    }

    @Test
    public void shouldCheckNullParameterInUpdate() {
        assertThrows(NullPointerException.class, () -> subject.update(null));
        assertThrows(NullPointerException.class, () -> subject.update((Iterable) null));
        assertThrows(NullPointerException.class, () -> subject.update(singletonList(person), null));
        assertThrows(NullPointerException.class, () -> subject.update((Iterable) null, System.out::println));
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
    public void shouldUpdateIterable() {
        ColumnEntity document = ColumnEntity.of("Person");
        document.addAll(Stream.of(columns).collect(Collectors.toList()));

        subject.update(singletonList(this.person));
        verify(managerMock).update(captor.capture(), Mockito.any(Consumer.class));
        ColumnEntity value = captor.getValue();
        assertEquals(document.getName(), value.getName());
    }

    @Test
    public void shouldCheckNullParameterInDelete() {
        assertThrows(NullPointerException.class, () -> subject.delete(null));
        assertThrows(NullPointerException.class, () -> subject.delete(delete().from("delete").build(), null));
        assertThrows(NullPointerException.class, () -> subject.delete(Person.class, null));
        assertThrows(NullPointerException.class, () -> subject.delete((Class) null, 10L));
        assertThrows(NullPointerException.class, () -> subject.delete(Person.class, 10L, null));
    }

    @Test
    public void shouldDelete() {

        ColumnDeleteQuery query = delete().from("delete").build();
        subject.delete(query);
        verify(managerMock).delete(query);
    }

    @Test
    public void shouldDeleteCallBack() {

        ColumnDeleteQuery query = delete().from("delete").build();
        Consumer<Void> callback = v -> {

        };
        subject.delete(query, callback);
        verify(managerMock).delete(query, callback);
    }

    @Test
    public void shouldDeleteByEntity() {
        subject.delete(Person.class, 10L);

        ArgumentCaptor<ColumnDeleteQuery> queryCaptor = ArgumentCaptor.forClass(ColumnDeleteQuery.class);
        verify(managerMock).delete(queryCaptor.capture());

        ColumnDeleteQuery query = queryCaptor.getValue();
        assertEquals("Person", query.getColumnFamily());
        assertEquals(ColumnCondition.eq(Column.of("_id", 10L)), query.getCondition().get());

    }

    @Test
    public void shouldDeleteByEntityCallBack() {

        Consumer<Void> callback = v -> {
        };
        subject.delete(Person.class, 10L, callback);

        ArgumentCaptor<ColumnDeleteQuery> queryCaptor = ArgumentCaptor.forClass(ColumnDeleteQuery.class);
        verify(managerMock).delete(queryCaptor.capture(), Mockito.eq(callback));

        ColumnDeleteQuery query = queryCaptor.getValue();
        assertEquals("Person", query.getColumnFamily());
        assertEquals(ColumnCondition.eq(Column.of("_id", 10L)), query.getCondition().get());

    }

    @Test
    public void shouldCheckNullParameterInSelect() {
        assertThrows(NullPointerException.class, () -> subject.select(null, null));
        assertThrows(NullPointerException.class, () -> subject.select(null, System.out::println));
        assertThrows(NullPointerException.class, () -> subject.select(select().from("Person").build(),
                null));
    }


    @Test
    public void shouldSelect() {

        ArgumentCaptor<Consumer<List<ColumnEntity>>> dianaCallbackCaptor = ArgumentCaptor.forClass(Consumer.class);
        ColumnQuery query = ColumnQueryBuilder.select().from("Person").build();
        AtomicBoolean condition = new AtomicBoolean(false);
        Consumer<List<Person>> callback = l -> {
            condition.set(true);
        };
        subject.select(query, callback);
        verify(managerMock).select(Mockito.any(ColumnQuery.class), dianaCallbackCaptor.capture());
        Consumer<List<ColumnEntity>> dianaCallBack = dianaCallbackCaptor.getValue();
        dianaCallBack.accept(Collections.singletonList(ColumnEntity.of("Person", Arrays.asList(columns))));
        verify(managerMock).select(Mockito.eq(query), Mockito.any());
        await().untilTrue(condition);
    }

    @Test
    public void shouldReturnSingleResult() {

        ArgumentCaptor<Consumer<List<ColumnEntity>>> dianaCallbackCaptor = ArgumentCaptor.forClass(Consumer.class);
        ColumnQuery query = ColumnQueryBuilder.select().from("Person").build();
        AtomicBoolean condition = new AtomicBoolean(false);
        Consumer<Optional<Person>> callback = l -> {
            condition.set(true);
        };
        subject.singleResult(query, callback);
        verify(managerMock).select(Mockito.any(ColumnQuery.class), dianaCallbackCaptor.capture());
        Consumer<List<ColumnEntity>> dianaCallBack = dianaCallbackCaptor.getValue();
        dianaCallBack.accept(Collections.singletonList(ColumnEntity.of("Person", Arrays.asList(columns))));
        verify(managerMock).select(Mockito.eq(query), Mockito.any());
        await().untilTrue(condition);
    }

    @Test
    public void shouldCheckNullParameterInFindById() {
        assertThrows(NullPointerException.class, () -> subject.find(null, null, null));
        assertThrows(NullPointerException.class, () -> subject.find(Person.class, null, null));
        assertThrows(NullPointerException.class, () -> subject.find(Person.class, 10L, null));
        assertThrows(NullPointerException.class, () -> subject.find(Person.class, null, System.out::println));
        assertThrows(NullPointerException.class, () -> subject.find(null, null, System.out::println));
        assertThrows(NullPointerException.class, () -> subject.find(null, 10L, System.out::println));
        assertThrows(NullPointerException.class, () -> subject.find(null, 10L, null));
    }

    @Test
    public void shouldFindById() {
        AtomicBoolean condition = new AtomicBoolean(false);
        Consumer<Optional<Person>> callback = l -> {
            condition.set(true);
        };
        subject.find(Person.class, 10L, callback);
    }
}