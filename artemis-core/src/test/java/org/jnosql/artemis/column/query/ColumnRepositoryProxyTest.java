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
package org.jnosql.artemis.column.query;

import org.hamcrest.Matchers;
import org.jnosql.artemis.Repository;
import org.jnosql.artemis.WeldJUnit4Runner;
import org.jnosql.artemis.column.ColumnTemplate;
import org.jnosql.artemis.model.Person;
import org.jnosql.artemis.reflection.ClassRepresentations;
import org.jnosql.artemis.reflection.Reflections;
import org.jnosql.diana.api.Condition;
import org.jnosql.diana.api.column.Column;
import org.jnosql.diana.api.column.ColumnCondition;
import org.jnosql.diana.api.column.ColumnDeleteQuery;
import org.jnosql.diana.api.column.ColumnQuery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import javax.inject.Inject;
import java.lang.reflect.Proxy;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.jnosql.diana.api.column.ColumnCondition.eq;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(WeldJUnit4Runner.class)
public class ColumnRepositoryProxyTest {

    private ColumnTemplate template;

    @Inject
    private ClassRepresentations classRepresentations;

    @Inject
    private Reflections reflections;

    private PersonRepository personRepository;


    @Before
    public void setUp() {
        this.template = Mockito.mock(ColumnTemplate.class);

        ColumnRepositoryProxy handler = new ColumnRepositoryProxy(template,
                classRepresentations, PersonRepository.class, reflections);

        when(template.insert(any(Person.class))).thenReturn(Person.builder().build());
        when(template.insert(any(Person.class), any(Duration.class))).thenReturn(Person.builder().build());
        when(template.update(any(Person.class))).thenReturn(Person.builder().build());
        personRepository = (PersonRepository) Proxy.newProxyInstance(PersonRepository.class.getClassLoader(),
                new Class[]{PersonRepository.class},
                handler);
    }


    @Test
    public void shouldSaveUsingInsertWhenDataDoesNotExist() {
        when(template.singleResult(Mockito.any(ColumnQuery.class))).thenReturn(Optional.empty());

        ArgumentCaptor<Person> captor = ArgumentCaptor.forClass(Person.class);
        Person person = Person.builder().withName("Ada")
                .withId(10L)
                .withPhones(singletonList("123123"))
                .build();
        assertNotNull(personRepository.save(person));
        verify(template).insert(captor.capture());
        Person value = captor.getValue();
        assertEquals(person, value);
    }

    @Test
    public void shouldSaveUsingUpdateWhenDataExists() {
        when(template.singleResult(Mockito.any(ColumnQuery.class))).thenReturn(Optional.of(Person.builder().build()));

        ArgumentCaptor<Person> captor = ArgumentCaptor.forClass(Person.class);
        Person person = Person.builder().withName("Ada")
                .withId(10L)
                .withPhones(singletonList("123123"))
                .build();
        assertNotNull(personRepository.save(person));
        verify(template).update(captor.capture());
        Person value = captor.getValue();
        assertEquals(person, value);
    }


    @Test
    public void shouldSaveItarable() {
        ArgumentCaptor<Iterable> captor = ArgumentCaptor.forClass(Iterable.class);
        Person person = Person.builder().withName("Ada")
                .withId(10L)
                .withPhones(singletonList("123123"))
                .build();
        personRepository.save(singletonList(person));
        verify(template).insert(captor.capture());
        Iterable<Person> persons = captor.getValue();
        assertThat(persons, containsInAnyOrder(person));
    }


    @Test
    public void shouldFindByNameInstance() {

        when(template.singleResult(any(ColumnQuery.class))).thenReturn(Optional
                .of(Person.builder().build()));

        personRepository.findByName("name");

        ArgumentCaptor<ColumnQuery> captor = ArgumentCaptor.forClass(ColumnQuery.class);
        verify(template).singleResult(captor.capture());
        ColumnQuery query = captor.getValue();
        ColumnCondition condition = query.getCondition().get();
        assertEquals("Person", query.getColumnFamily());
        assertEquals(Condition.EQUALS, condition.getCondition());
        assertEquals(Column.of("name", "name"), condition.getColumn());

        assertNotNull(personRepository.findByName("name"));
        when(template.singleResult(any(ColumnQuery.class))).thenReturn(Optional
                .empty());

        assertNull(personRepository.findByName("name"));


    }

    @Test
    public void shouldFindByNameANDAge() {
        Person ada = Person.builder()
                .withAge(20).withName("Ada").build();

        when(template.select(any(ColumnQuery.class)))
                .thenReturn(singletonList(ada));

        List<Person> persons = personRepository.findByNameANDAge("name", 20);
        ArgumentCaptor<ColumnQuery> captor = ArgumentCaptor.forClass(ColumnQuery.class);
        verify(template).select(captor.capture());
        assertThat(persons, Matchers.contains(ada));

    }

    @Test
    public void shouldFindByAgeANDName() {
        Person ada = Person.builder()
                .withAge(20).withName("Ada").build();

        when(template.select(any(ColumnQuery.class)))
                .thenReturn(singletonList(ada));

        Set<Person> persons = personRepository.findByAgeANDName(20, "name");
        ArgumentCaptor<ColumnQuery> captor = ArgumentCaptor.forClass(ColumnQuery.class);
        verify(template).select(captor.capture());
        assertThat(persons, Matchers.contains(ada));

    }

    @Test
    public void shouldFindByNameANDAgeOrderByName() {
        Person ada = Person.builder()
                .withAge(20).withName("Ada").build();

        when(template.select(any(ColumnQuery.class)))
                .thenReturn(singletonList(ada));

        Stream<Person> persons = personRepository.findByNameANDAgeOrderByName("name", 20);
        ArgumentCaptor<ColumnQuery> captor = ArgumentCaptor.forClass(ColumnQuery.class);
        verify(template).select(captor.capture());
        assertThat(persons.collect(Collectors.toList()), Matchers.contains(ada));

    }

    @Test
    public void shouldFindByNameANDAgeOrderByAge() {
        Person ada = Person.builder()
                .withAge(20).withName("Ada").build();

        when(template.select(any(ColumnQuery.class)))
                .thenReturn(singletonList(ada));

        Queue<Person> persons = personRepository.findByNameANDAgeOrderByAge("name", 20);
        ArgumentCaptor<ColumnQuery> captor = ArgumentCaptor.forClass(ColumnQuery.class);
        verify(template).select(captor.capture());
        assertThat(persons, Matchers.contains(ada));

    }

    @Test
    public void shouldDeleteByName() {
        ArgumentCaptor<ColumnDeleteQuery> captor = ArgumentCaptor.forClass(ColumnDeleteQuery.class);
        personRepository.deleteByName("Ada");
        verify(template).delete(captor.capture());
        ColumnDeleteQuery deleteQuery = captor.getValue();
        ColumnCondition condition = deleteQuery.getCondition().get();
        assertEquals("Person", deleteQuery.getColumnFamily());
        assertEquals(Condition.EQUALS, condition.getCondition());
        assertEquals(Column.of("name", "Ada"), condition.getColumn());

    }

    @Test
    public void shouldExecuteQuery() {
        ArgumentCaptor<ColumnQuery> captor = ArgumentCaptor.forClass(ColumnQuery.class);
        Person ada = Person.builder()
                .withAge(20).withName("Ada").build();
        when(template.singleResult(any(ColumnQuery.class)))
                .thenReturn(Optional.of(ada));

        ColumnQuery query = ColumnQuery.of("Person").and(eq(Column.of("name", "Ada")));
        Person person = personRepository.query(query);
        verify(template).singleResult(captor.capture());
        assertEquals(ada, person);
        assertEquals(query, captor.getValue());

    }

    @Test
    public void shouldDeleteQuery() {
        ArgumentCaptor<ColumnDeleteQuery> captor = ArgumentCaptor.forClass(ColumnDeleteQuery.class);
        ColumnDeleteQuery query = ColumnDeleteQuery.of("Person").and(eq(Column.of("name", "Ada")));
        personRepository.deleteQuery(query);
        verify(template).delete(captor.capture());
        assertEquals(query, captor.getValue());

    }

    @Test
    public void shouldFindById() {
        ArgumentCaptor<ColumnQuery> captor = ArgumentCaptor.forClass(ColumnQuery.class);
        personRepository.findById(10L);
        verify(template).singleResult(captor.capture());

        ColumnQuery query = captor.getValue();

        assertEquals("Person", query.getColumnFamily());
        assertEquals(ColumnCondition.eq(Column.of("_id", 10L)), query.getCondition().get());
    }

    @Test
    public void shouldFindByIds() {
        when(template.singleResult(any(ColumnQuery.class))).thenReturn(Optional.of(Person.builder().build()));
        ArgumentCaptor<ColumnQuery> captor = ArgumentCaptor.forClass(ColumnQuery.class);
        personRepository.findById(singletonList(10L));
        verify(template).singleResult(captor.capture());

        ColumnQuery query = captor.getValue();

        assertEquals("Person", query.getColumnFamily());
        assertEquals(ColumnCondition.eq(Column.of("_id", 10L)), query.getCondition().get());
        personRepository.findById(asList(1L, 2L, 3L));
        verify(template, times(4)).singleResult(any(ColumnQuery.class));
    }

    @Test
    public void shouldDeleteById() {
        ArgumentCaptor<ColumnDeleteQuery> captor = ArgumentCaptor.forClass(ColumnDeleteQuery.class);
        personRepository.deleteById(10L);
        verify(template).delete(captor.capture());

        ColumnDeleteQuery query = captor.getValue();

        assertEquals("Person", query.getColumnFamily());
        assertEquals(ColumnCondition.eq(Column.of("_id", 10L)), query.getCondition().get());
    }

    @Test
    public void shouldDeleteByIds() {
        ArgumentCaptor<ColumnDeleteQuery> captor = ArgumentCaptor.forClass(ColumnDeleteQuery.class);
        personRepository.deleteById(singletonList(10L));
        verify(template).delete(captor.capture());

        ColumnDeleteQuery query = captor.getValue();

        assertEquals("Person", query.getColumnFamily());
        assertEquals(ColumnCondition.eq(Column.of("_id", 10L)), query.getCondition().get());

        personRepository.deleteById(asList(1L, 2L, 3L));
        verify(template, times(4)).delete(any(ColumnDeleteQuery.class));
    }


    @Test
    public void shouldContainsById() {
        when(template.singleResult(any(ColumnQuery.class))).thenReturn(Optional.of(Person.builder().build()));

        assertTrue(personRepository.existsById(10L));
        Mockito.verify(template).singleResult(any(ColumnQuery.class));

        when(template.singleResult(any(ColumnQuery.class))).thenReturn(Optional.empty());
        assertFalse(personRepository.existsById(10L));

    }


    interface PersonRepository extends Repository<Person, Long> {

        Person findByName(String name);

        void deleteByName(String name);

        Optional<Person> findByAge(Integer age);

        List<Person> findByNameANDAge(String name, Integer age);

        Set<Person> findByAgeANDName(Integer age, String name);

        Stream<Person> findByNameANDAgeOrderByName(String name, Integer age);

        Queue<Person> findByNameANDAgeOrderByAge(String name, Integer age);

        Person query(ColumnQuery query);

        void deleteQuery(ColumnDeleteQuery query);
    }
}