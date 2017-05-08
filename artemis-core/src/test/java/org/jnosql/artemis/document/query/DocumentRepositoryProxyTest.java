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

import org.hamcrest.Matchers;
import org.jnosql.artemis.Repository;
import org.jnosql.artemis.WeldJUnit4Runner;
import org.jnosql.artemis.document.DocumentTemplate;
import org.jnosql.artemis.model.Person;
import org.jnosql.artemis.reflection.ClassRepresentations;
import org.jnosql.diana.api.Condition;
import org.jnosql.diana.api.document.Document;
import org.jnosql.diana.api.document.DocumentCondition;
import org.jnosql.diana.api.document.DocumentDeleteQuery;
import org.jnosql.diana.api.document.DocumentQuery;
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

import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.jnosql.diana.api.document.DocumentCondition.eq;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(WeldJUnit4Runner.class)
public class DocumentRepositoryProxyTest {

    private DocumentTemplate repository;

    @Inject
    private ClassRepresentations classRepresentations;

    private PersonRepository personRepository;


    @Before
    public void setUp() {
        this.repository = Mockito.mock(DocumentTemplate.class);

        DocumentRepositoryProxy handler = new DocumentRepositoryProxy(repository,
                classRepresentations, PersonRepository.class);

        when(repository.insert(any(Person.class))).thenReturn(Person.builder().build());
        when(repository.insert(any(Person.class), any(Duration.class))).thenReturn(Person.builder().build());
        when(repository.update(any(Person.class))).thenReturn(Person.builder().build());
        personRepository = (PersonRepository) Proxy.newProxyInstance(PersonRepository.class.getClassLoader(),
                new Class[]{PersonRepository.class},
                handler);
    }


    @Test
    public void shouldSave() {
        ArgumentCaptor<Person> captor = ArgumentCaptor.forClass(Person.class);
        Person person = Person.builder().withName("Ada")
                .withId(10L)
                .withPhones(singletonList("123123"))
                .build();
        assertNotNull(personRepository.save(person));
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
        assertNotNull(personRepository.save(person, Duration.ofHours(2)));
        verify(repository).insert(captor.capture(), Mockito.eq(Duration.ofHours(2)));
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
    public void shouldFindByNameInstance() {

        when(repository.singleResult(Mockito.any(DocumentQuery.class))).thenReturn(Optional
                .of(Person.builder().build()));

        personRepository.findByName("name");

        ArgumentCaptor<DocumentQuery> captor = ArgumentCaptor.forClass(DocumentQuery.class);
        verify(repository).singleResult(captor.capture());
        DocumentQuery query = captor.getValue();
        DocumentCondition condition = query.getCondition().get();
        assertEquals("Person", query.getCollection());
        assertEquals(Condition.EQUALS, condition.getCondition());
        assertEquals(Document.of("name", "name"), condition.getDocument());

        assertNotNull(personRepository.findByName("name"));
        when(repository.singleResult(Mockito.any(DocumentQuery.class))).thenReturn(Optional
                .empty());

        assertNull(personRepository.findByName("name"));


    }

    @Test
    public void shouldFindByNameANDAge() {
        Person ada = Person.builder()
                .withAge(20).withName("Ada").build();

        when(repository.select(Mockito.any(DocumentQuery.class)))
                .thenReturn(singletonList(ada));

        List<Person> persons = personRepository.findByNameANDAge("name", 20);
        ArgumentCaptor<DocumentQuery> captor = ArgumentCaptor.forClass(DocumentQuery.class);
        verify(repository).select(captor.capture());
        assertThat(persons, Matchers.contains(ada));

    }

    @Test
    public void shouldFindByAgeANDName() {
        Person ada = Person.builder()
                .withAge(20).withName("Ada").build();

        when(repository.select(Mockito.any(DocumentQuery.class)))
                .thenReturn(singletonList(ada));

        Set<Person> persons = personRepository.findByAgeANDName(20, "name");
        ArgumentCaptor<DocumentQuery> captor = ArgumentCaptor.forClass(DocumentQuery.class);
        verify(repository).select(captor.capture());
        assertThat(persons, Matchers.contains(ada));

    }

    @Test
    public void shouldFindByNameANDAgeOrderByName() {
        Person ada = Person.builder()
                .withAge(20).withName("Ada").build();

        when(repository.select(Mockito.any(DocumentQuery.class)))
                .thenReturn(singletonList(ada));

        Stream<Person> persons = personRepository.findByNameANDAgeOrderByName("name", 20);
        ArgumentCaptor<DocumentQuery> captor = ArgumentCaptor.forClass(DocumentQuery.class);
        verify(repository).select(captor.capture());
        assertThat(persons.collect(Collectors.toList()), Matchers.contains(ada));

    }

    @Test
    public void shouldFindByNameANDAgeOrderByAge() {
        Person ada = Person.builder()
                .withAge(20).withName("Ada").build();

        when(repository.select(Mockito.any(DocumentQuery.class)))
                .thenReturn(singletonList(ada));

        Queue<Person> persons = personRepository.findByNameANDAgeOrderByAge("name", 20);
        ArgumentCaptor<DocumentQuery> captor = ArgumentCaptor.forClass(DocumentQuery.class);
        verify(repository).select(captor.capture());
        assertThat(persons, Matchers.contains(ada));

    }

    @Test
    public void shouldDeleteByName() {
        ArgumentCaptor<DocumentDeleteQuery> captor = ArgumentCaptor.forClass(DocumentDeleteQuery.class);
        personRepository.deleteByName("Ada");
        verify(repository).delete(captor.capture());
        DocumentDeleteQuery deleteQuery = captor.getValue();
        DocumentCondition condition = deleteQuery.getCondition().get();
        assertEquals("Person", deleteQuery.getCollection());
        assertEquals(Condition.EQUALS, condition.getCondition());
        assertEquals(Document.of("name", "Ada"), condition.getDocument());

    }

    @Test
    public void shouldExecuteQuery() {
        ArgumentCaptor<DocumentQuery> captor = ArgumentCaptor.forClass(DocumentQuery.class);
        Person ada = Person.builder()
                .withAge(20).withName("Ada").build();
        when(repository.singleResult(Mockito.any(DocumentQuery.class)))
                .thenReturn(Optional.of(ada));

        DocumentQuery query = DocumentQuery.of("Person").with(eq(Document.of("name", "Ada")));
        Person person = personRepository.query(query);
        verify(repository).singleResult(captor.capture());
        assertEquals(ada, person);
        assertEquals(query, captor.getValue());

    }

    @Test
    public void shouldDeleteQuery() {
        ArgumentCaptor<DocumentDeleteQuery> captor = ArgumentCaptor.forClass(DocumentDeleteQuery.class);
        DocumentDeleteQuery query = DocumentDeleteQuery.of("Person").and(eq(Document.of("name", "Ada")));
        personRepository.deleteQuery(query);
        verify(repository).delete(captor.capture());
        assertEquals(query, captor.getValue());

    }

    interface PersonRepository extends Repository<Person, Long> {

        Person findByName(String name);

        void deleteByName(String name);

        Optional<Person> findByAge(Integer age);

        List<Person> findByNameANDAge(String name, Integer age);

        Set<Person> findByAgeANDName(Integer age, String name);

        Stream<Person> findByNameANDAgeOrderByName(String name, Integer age);

        Queue<Person> findByNameANDAgeOrderByAge(String name, Integer age);


        Person query(DocumentQuery query);

        void deleteQuery(DocumentDeleteQuery query);
    }
}