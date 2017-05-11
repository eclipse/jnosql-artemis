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
package org.jnosql.artemis.document.query;

import org.jnosql.artemis.DynamicQueryException;
import org.jnosql.artemis.Pagination;
import org.jnosql.artemis.RepositoryAsync;
import org.jnosql.artemis.WeldJUnit4Runner;
import org.jnosql.artemis.document.DocumentTemplateAsync;
import org.jnosql.artemis.model.Person;
import org.jnosql.artemis.reflection.ClassRepresentations;
import org.jnosql.artemis.reflection.Reflections;
import org.jnosql.diana.api.Condition;
import org.jnosql.diana.api.Sort;
import org.jnosql.diana.api.document.Document;
import org.jnosql.diana.api.document.DocumentCondition;
import org.jnosql.diana.api.document.DocumentDeleteQuery;
import org.jnosql.diana.api.document.DocumentQuery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mockito;

import javax.inject.Inject;
import java.lang.reflect.Proxy;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.jnosql.diana.api.document.DocumentCondition.eq;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


@RunWith(WeldJUnit4Runner.class)
public class DocumentRepositoryAsyncProxyTest {

    private DocumentTemplateAsync template;

    @Inject
    private ClassRepresentations classRepresentations;

    @Inject
    private Reflections reflections;

    private PersonAsyncRepository personRepository;


    @Before
    public void setUp() {
        this.template = Mockito.mock(DocumentTemplateAsync.class);

        DocumentRepositoryAsyncProxy handler = new DocumentRepositoryAsyncProxy(template,
                classRepresentations, PersonAsyncRepository.class, reflections);


        personRepository = (PersonAsyncRepository) Proxy.newProxyInstance(PersonAsyncRepository.class.getClassLoader(),
                new Class[]{PersonAsyncRepository.class},
                handler);
    }


    @Test
    public void shouldSaveUsingInsertWhenThereIsNotData() {
        ArgumentCaptor<Person> captor = ArgumentCaptor.forClass(Person.class);
        ArgumentCaptor<Consumer> consumerCaptor = ArgumentCaptor.forClass(Consumer.class);

        Person person = Person.builder().withName("Ada")
                .withId(10L)
                .withPhones(singletonList("123123"))
                .build();
        personRepository.save(person);
        verify(template).singleResult(Mockito.any(DocumentQuery.class), consumerCaptor.capture());
        Consumer consumer = consumerCaptor.getValue();
        consumer.accept(Optional.empty());
        verify(template).insert(captor.capture());
        Person value = captor.getValue();
        assertEquals(person, value);
    }

    @Test
    public void shouldSaveUsingUpdateWhenThereIsData() {
        ArgumentCaptor<Person> captor = ArgumentCaptor.forClass(Person.class);
        ArgumentCaptor<Consumer> consumerCaptor = ArgumentCaptor.forClass(Consumer.class);

        Person person = Person.builder().withName("Ada")
                .withId(10L)
                .withPhones(singletonList("123123"))
                .build();
        personRepository.save(person);
        verify(template).singleResult(Mockito.any(DocumentQuery.class), consumerCaptor.capture());
        Consumer consumer = consumerCaptor.getValue();
        consumer.accept(Optional.of(Person.builder().build()));
        verify(template).update(captor.capture());
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
        template.insert(person, Duration.ofHours(2));
        verify(template).insert(captor.capture(), Matchers.eq(Duration.ofHours(2)));
        Person value = captor.getValue();
        assertEquals(person, value);
    }



    @Test
    public void shouldDeleteByName() {
        ArgumentCaptor<DocumentDeleteQuery> captor = ArgumentCaptor.forClass(DocumentDeleteQuery.class);
        personRepository.deleteByName("name");
        verify(template).delete(captor.capture());
        DocumentDeleteQuery query = captor.getValue();
        DocumentCondition condition = query.getCondition().get();
        assertEquals("Person", query.getCollection());
        assertEquals(Condition.EQUALS, condition.getCondition());
        assertEquals(Document.of("name", "name"), condition.getDocument());

    }

    @Test
    public void shouldDeleteByNameCallBack() {
        ArgumentCaptor<DocumentDeleteQuery> captor = ArgumentCaptor.forClass(DocumentDeleteQuery.class);
        ArgumentCaptor<Consumer> consumerCaptor = ArgumentCaptor.forClass(Consumer.class);

        Consumer<Void> voidConsumer = v -> {
        };
        personRepository.deleteByName("name", voidConsumer);
        verify(template).delete(captor.capture(), consumerCaptor.capture());
        DocumentDeleteQuery query = captor.getValue();
        DocumentCondition condition = query.getCondition().get();
        assertEquals("Person", query.getCollection());
        assertEquals(Condition.EQUALS, condition.getCondition());
        assertEquals(Document.of("name", "name"), condition.getDocument());
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

        ArgumentCaptor<DocumentQuery> captor = ArgumentCaptor.forClass(DocumentQuery.class);
        ArgumentCaptor<Consumer> consumerCaptor = ArgumentCaptor.forClass(Consumer.class);

        personRepository.findByName("name", callback);
        verify(template).select(captor.capture(), consumerCaptor.capture());
        DocumentQuery query = captor.getValue();
        DocumentCondition condition = query.getCondition().get();
        assertEquals("Person", query.getCollection());
        assertEquals(Condition.EQUALS, condition.getCondition());
        assertEquals(Document.of("name", "name"), condition.getDocument());
        assertEquals(callback, consumerCaptor.getValue());
    }

    @Test
    public void shoudFindByNameSort() {
        Consumer<List<Person>> callback = v -> {
        };

        Sort sort = Sort.of("age", Sort.SortType.ASC);

        ArgumentCaptor<DocumentQuery> captor = ArgumentCaptor.forClass(DocumentQuery.class);
        ArgumentCaptor<Consumer> consumerCaptor = ArgumentCaptor.forClass(Consumer.class);

        personRepository.findByName("name", sort, callback);
        verify(template).select(captor.capture(), consumerCaptor.capture());
        DocumentQuery query = captor.getValue();
        DocumentCondition condition = query.getCondition().get();
        assertEquals("Person", query.getCollection());
        assertEquals(Condition.EQUALS, condition.getCondition());
        assertEquals(Document.of("name", "name"), condition.getDocument());
        assertEquals(callback, consumerCaptor.getValue());
        assertEquals(sort, query.getSorts().get(0));
    }

    @Test
    public void shoudFindByNameSortPagination() {
        Consumer<List<Person>> callback = v -> {
        };

        Sort sort = Sort.of("age", Sort.SortType.ASC);
        Pagination pagination = Pagination.of(10, 20);
        ArgumentCaptor<DocumentQuery> captor = ArgumentCaptor.forClass(DocumentQuery.class);
        ArgumentCaptor<Consumer> consumerCaptor = ArgumentCaptor.forClass(Consumer.class);

        personRepository.findByName("name", sort, pagination, callback);
        verify(template).select(captor.capture(), consumerCaptor.capture());
        DocumentQuery query = captor.getValue();
        DocumentCondition condition = query.getCondition().get();
        assertEquals("Person", query.getCollection());
        assertEquals(Condition.EQUALS, condition.getCondition());
        assertEquals(Document.of("name", "name"), condition.getDocument());
        assertEquals(callback, consumerCaptor.getValue());
        assertEquals(sort, query.getSorts().get(0));
        assertEquals(pagination.getFirstResult(), query.getFirstResult());
        assertEquals(pagination.getMaxResults(), query.getMaxResults());
    }

    @Test
    public void shouldFindByNameOrderByAgeDesc() {
        Consumer<List<Person>> callback = v -> {
        };

        ArgumentCaptor<DocumentQuery> captor = ArgumentCaptor.forClass(DocumentQuery.class);
        ArgumentCaptor<Consumer> consumerCaptor = ArgumentCaptor.forClass(Consumer.class);

        personRepository.findByNameOrderByAgeDesc("name", callback);
        verify(template).select(captor.capture(), consumerCaptor.capture());
        DocumentQuery query = captor.getValue();
        DocumentCondition condition = query.getCondition().get();
        assertEquals("Person", query.getCollection());
        assertEquals(Condition.EQUALS, condition.getCondition());
        assertEquals(Document.of("name", "name"), condition.getDocument());
        assertEquals(callback, consumerCaptor.getValue());
        assertEquals(Sort.of("age", Sort.SortType.DESC), query.getSorts().get(0));

    }

    @Test
    public void shouldExecuteQuery() {
        Consumer<List<Person>> callback = v -> {
        };

        ArgumentCaptor<DocumentQuery> captor = ArgumentCaptor.forClass(DocumentQuery.class);
        ArgumentCaptor<Consumer> consumerCaptor = ArgumentCaptor.forClass(Consumer.class);
        DocumentQuery query = DocumentQuery.of("Person")
                .with(eq(Document.of("name", "Ada")));

        personRepository.query(query, callback);
        verify(template).select(captor.capture(), consumerCaptor.capture());
        DocumentQuery queryCaptor = captor.getValue();
        DocumentCondition condition = query.getCondition().get();
        assertEquals(query, queryCaptor);
        assertEquals(callback, consumerCaptor.getValue());
    }

    //
    @Test
    public void shouldExecuteDeleteQuery() {
        ArgumentCaptor<DocumentDeleteQuery> captor = ArgumentCaptor.forClass(DocumentDeleteQuery.class);
        DocumentDeleteQuery deleteQuery = DocumentDeleteQuery.of("Person")
                .and(DocumentCondition.eq(Document.of("name", "Ada")));

        personRepository.deleteQuery(deleteQuery);
        verify(template).delete(captor.capture());
        assertEquals(deleteQuery, captor.getValue());
    }

    @Test
    public void shouldFindById() {
        ArgumentCaptor<DocumentQuery> captor = ArgumentCaptor.forClass(DocumentQuery.class);
        Consumer<Optional<Person>> callBack = p -> {};
        personRepository.findById(10L, callBack);
        verify(template).singleResult(captor.capture(), Matchers.eq(callBack));

        DocumentQuery query = captor.getValue();

        assertEquals("Person", query.getCollection());
        assertEquals(DocumentCondition.eq(Document.of("_id", 10L)), query.getCondition().get());
    }


    @Test
    public void shouldDeleteById() {
        ArgumentCaptor<DocumentDeleteQuery> captor = ArgumentCaptor.forClass(DocumentDeleteQuery.class);
        personRepository.deleteById(10L);
        verify(template).delete(captor.capture());

        DocumentDeleteQuery query = captor.getValue();

        assertEquals("Person", query.getCollection());
        assertEquals(DocumentCondition.eq(Document.of("_id", 10L)), query.getCondition().get());
    }

    @Test
    public void shouldDeleteByEntity() {

        ArgumentCaptor<DocumentDeleteQuery> captor = ArgumentCaptor.forClass(DocumentDeleteQuery.class);
        personRepository.delete(Person.builder().withId(10L).build());
        verify(template).delete(captor.capture());

        DocumentDeleteQuery query = captor.getValue();

        assertEquals("Person", query.getCollection());
        assertEquals(DocumentCondition.eq(Document.of("_id", 10L)), query.getCondition().get());
    }

    @Test
    public void shouldDeleteByEntities() {

        ArgumentCaptor<DocumentDeleteQuery> captor = ArgumentCaptor.forClass(DocumentDeleteQuery.class);
        Person person = Person.builder().withId(10L).build();
        personRepository.delete(singletonList(person));
        verify(template).delete(captor.capture());

        DocumentDeleteQuery query = captor.getValue();

        assertEquals("Person", query.getCollection());
        assertEquals(DocumentCondition.eq(Document.of("_id", 10L)), query.getCondition().get());

        personRepository.delete(asList(person, person, person));
        verify(template, times(4)).delete(any(DocumentDeleteQuery.class));
    }

    @Test
    public void shouldExistsById() {
        Consumer<Boolean> callback = v -> {
        };
        personRepository.existsById(10L, callback);
        verify(template).singleResult(any(DocumentQuery.class), any(Consumer.class));


    }



    interface PersonAsyncRepository extends RepositoryAsync<Person, Long> {

        void deleteByName(String name);

        void deleteByName(String name, Consumer<Void> callback);

        void findByName(String name);

        void findByName(String name, Consumer<List<Person>> callBack);

        void findByNameOrderByAgeDesc(String name, Consumer<List<Person>> callBack);

        void findByName(String name, Sort sort, Consumer<List<Person>> callBack);

        void findByName(String name, Sort sort, Pagination pagination, Consumer<List<Person>> callBack);

        void query(DocumentQuery query, Consumer<List<Person>> callBack);

        void deleteQuery(DocumentDeleteQuery query);
    }

}