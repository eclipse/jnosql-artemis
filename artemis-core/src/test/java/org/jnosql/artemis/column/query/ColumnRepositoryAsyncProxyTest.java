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

import org.jnosql.artemis.DynamicQueryException;
import org.jnosql.artemis.Pagination;
import org.jnosql.artemis.RepositoryAsync;
import org.jnosql.artemis.WeldJUnit4Runner;
import org.jnosql.artemis.column.ColumnTemplateAsync;
import org.jnosql.artemis.model.Person;
import org.jnosql.artemis.reflection.ClassRepresentations;
import org.jnosql.artemis.reflection.Reflections;
import org.jnosql.diana.api.Condition;
import org.jnosql.diana.api.Sort;
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
import java.util.function.Consumer;

import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.jnosql.diana.api.column.ColumnCondition.eq;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;

@RunWith(WeldJUnit4Runner.class)
public class ColumnRepositoryAsyncProxyTest {

    private ColumnTemplateAsync repository;

    @Inject
    private ClassRepresentations classRepresentations;

    @Inject
    private Reflections reflections;

    private PersonAsyncRepository personRepository;


    @Before
    public void setUp() {
        this.repository = Mockito.mock(ColumnTemplateAsync.class);

        ColumnRepositoryAsyncProxy handler = new ColumnRepositoryAsyncProxy(repository,
                classRepresentations, PersonAsyncRepository.class, reflections);


        personRepository = (PersonAsyncRepository) Proxy.newProxyInstance(PersonAsyncRepository.class.getClassLoader(),
                new Class[]{PersonAsyncRepository.class},
                handler);
    }


    @Test
    public void shouldSave() {
        ArgumentCaptor<Person> captor = ArgumentCaptor.forClass(Person.class);
        Person person = Person.builder().withName("Ada")
                .withId(10L)
                .withPhones(singletonList("123123"))
                .build();
        repository.insert(person);
        verify(repository).insert(captor.capture());
        Person value = captor.getValue();
        assertEquals(person, value);
    }


    @Test
    public void shouldSaveWithTTl() {
        ArgumentCaptor<Person> captor = ArgumentCaptor.forClass(Person.class);
        Person person = Person.builder().withName("Ada")
                .withId(10L)
                .withPhones(singletonList("123123"))
                .build();
        repository.insert(person, Duration.ofHours(2));
        verify(repository).insert(captor.capture(), Mockito.eq(Duration.ofHours(2)));
        Person value = captor.getValue();
        assertEquals(person, value);
    }


    @Test
    public void shouldUpdate() {
        ArgumentCaptor<Person> captor = ArgumentCaptor.forClass(Person.class);
        Person person = Person.builder().withName("Ada")
                .withId(10L)
                .withPhones(singletonList("123123"))
                .build();
        repository.update(person);
        verify(repository).update(captor.capture());
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
        verify(repository).insert(captor.capture());
        Iterable<Person> persons = captor.getValue();
        assertThat(persons, containsInAnyOrder(person));
    }


    @Test
    public void shouldDeleteByName() {
        ArgumentCaptor<ColumnDeleteQuery> captor = ArgumentCaptor.forClass(ColumnDeleteQuery.class);
        personRepository.deleteByName("name");
        verify(repository).delete(captor.capture());
        ColumnDeleteQuery query = captor.getValue();
        ColumnCondition condition = query.getCondition().get();
        assertEquals("Person", query.getColumnFamily());
        assertEquals(Condition.EQUALS, condition.getCondition());
        assertEquals(Column.of("name", "name"), condition.getColumn());

    }

    @Test
    public void shouldDeleteByNameCallBack() {
        ArgumentCaptor<ColumnDeleteQuery> captor = ArgumentCaptor.forClass(ColumnDeleteQuery.class);
        ArgumentCaptor<Consumer> consumerCaptor = ArgumentCaptor.forClass(Consumer.class);

        Consumer<Void> voidConsumer = v -> {
        };
        personRepository.deleteByName("name", voidConsumer);
        verify(repository).delete(captor.capture(), consumerCaptor.capture());
        ColumnDeleteQuery query = captor.getValue();
        ColumnCondition condition = query.getCondition().get();
        assertEquals("Person", query.getColumnFamily());
        assertEquals(Condition.EQUALS, condition.getCondition());
        assertEquals(Column.of("name", "name"), condition.getColumn());
        assertEquals(voidConsumer, consumerCaptor.getValue());
    }


    @Test(expected = DynamicQueryException.class)
    public void shoudReturnErrorOnFindByName() {
        personRepository.findByName("name");
    }

    @Test
    public void shoudFindByName() {
        Consumer<List<Person>> callback = v -> {
        };

        ArgumentCaptor<ColumnQuery> captor = ArgumentCaptor.forClass(ColumnQuery.class);
        ArgumentCaptor<Consumer> consumerCaptor = ArgumentCaptor.forClass(Consumer.class);

        personRepository.findByName("name", callback);
        verify(repository).select(captor.capture(), consumerCaptor.capture());
        ColumnQuery query = captor.getValue();
        ColumnCondition condition = query.getCondition().get();
        assertEquals("Person", query.getColumnFamily());
        assertEquals(Condition.EQUALS, condition.getCondition());
        assertEquals(Column.of("name", "name"), condition.getColumn());
        assertEquals(callback, consumerCaptor.getValue());
    }

    @Test
    public void shoudFindByNameSort() {
        Consumer<List<Person>> callback = v -> {
        };

        Sort sort = Sort.of("age", Sort.SortType.ASC);

        ArgumentCaptor<ColumnQuery> captor = ArgumentCaptor.forClass(ColumnQuery.class);
        ArgumentCaptor<Consumer> consumerCaptor = ArgumentCaptor.forClass(Consumer.class);

        personRepository.findByName("name", sort, callback);
        verify(repository).select(captor.capture(), consumerCaptor.capture());
        ColumnQuery query = captor.getValue();
        ColumnCondition condition = query.getCondition().get();
        assertEquals("Person", query.getColumnFamily());
        assertEquals(Condition.EQUALS, condition.getCondition());
        assertEquals(Column.of("name", "name"), condition.getColumn());
        assertEquals(callback, consumerCaptor.getValue());
        assertEquals(sort, query.getSorts().get(0));
    }

    @Test
    public void shoudFindByNameSortPagination() {
        Consumer<List<Person>> callback = v -> {
        };

        Sort sort = Sort.of("age", Sort.SortType.ASC);
        Pagination pagination = Pagination.of(10, 20);
        ArgumentCaptor<ColumnQuery> captor = ArgumentCaptor.forClass(ColumnQuery.class);
        ArgumentCaptor<Consumer> consumerCaptor = ArgumentCaptor.forClass(Consumer.class);

        personRepository.findByName("name", sort, pagination, callback);
        verify(repository).select(captor.capture(), consumerCaptor.capture());
        ColumnQuery query = captor.getValue();
        ColumnCondition condition = query.getCondition().get();
        assertEquals("Person", query.getColumnFamily());
        assertEquals(Condition.EQUALS, condition.getCondition());
        assertEquals(Column.of("name", "name"), condition.getColumn());
        assertEquals(callback, consumerCaptor.getValue());
        assertEquals(sort, query.getSorts().get(0));
        assertEquals(pagination.getFirstResult(), query.getFirstResult());
        assertEquals(pagination.getMaxResults(), query.getMaxResults());
    }

    @Test
    public void shouldFindByNameOrderByAgeDesc() {
        Consumer<List<Person>> callback = v -> {
        };

        ArgumentCaptor<ColumnQuery> captor = ArgumentCaptor.forClass(ColumnQuery.class);
        ArgumentCaptor<Consumer> consumerCaptor = ArgumentCaptor.forClass(Consumer.class);

        personRepository.findByNameOrderByAgeDesc("name", callback);
        verify(repository).select(captor.capture(), consumerCaptor.capture());
        ColumnQuery query = captor.getValue();
        ColumnCondition condition = query.getCondition().get();
        assertEquals("Person", query.getColumnFamily());
        assertEquals(Condition.EQUALS, condition.getCondition());
        assertEquals(Column.of("name", "name"), condition.getColumn());
        assertEquals(callback, consumerCaptor.getValue());
        assertEquals(Sort.of("age", Sort.SortType.DESC), query.getSorts().get(0));

    }

    @Test
    public void shouldExecuteQuery() {
        Consumer<List<Person>> callback = v -> {
        };

        ArgumentCaptor<ColumnQuery> captor = ArgumentCaptor.forClass(ColumnQuery.class);
        ArgumentCaptor<Consumer> consumerCaptor = ArgumentCaptor.forClass(Consumer.class);
        ColumnQuery query = ColumnQuery.of("Person")
                .and(eq(Column.of("name", "Ada")));

        personRepository.query(query, callback);
        verify(repository).select(captor.capture(), consumerCaptor.capture());
        ColumnQuery queryCaptor = captor.getValue();
        ColumnCondition condition = query.getCondition().get();
        assertEquals(query, queryCaptor);
        assertEquals(callback, consumerCaptor.getValue());
    }

    @Test
    public void shouldExecuteDeleteQuery() {
        ArgumentCaptor<ColumnDeleteQuery> captor = ArgumentCaptor.forClass(ColumnDeleteQuery.class);
        ColumnDeleteQuery deleteQuery = ColumnDeleteQuery.of("Person")
                .and(eq(Column.of("name", "Ada")));

        personRepository.deleteQuery(deleteQuery);
        verify(repository).delete(captor.capture());
        assertEquals(deleteQuery, captor.getValue());
    }

    interface PersonAsyncRepository extends RepositoryAsync<Person, Long> {

        void deleteByName(String name);

        void deleteByName(String name, Consumer<Void> callback);

        void findByName(String name);

        void findByName(String name, Consumer<List<Person>> callBack);

        void findByNameOrderByAgeDesc(String name, Consumer<List<Person>> callBack);

        void findByName(String name, Sort sort, Consumer<List<Person>> callBack);

        void findByName(String name, Sort sort, Pagination pagination, Consumer<List<Person>> callBack);

        void query(ColumnQuery query, Consumer<List<Person>> callBack);

        void deleteQuery(ColumnDeleteQuery query);
    }

}