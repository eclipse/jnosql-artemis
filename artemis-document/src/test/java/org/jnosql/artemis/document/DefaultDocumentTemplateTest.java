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
package org.jnosql.artemis.document;

import org.jnosql.artemis.CDIJUnitRunner;
import org.jnosql.artemis.Converters;
import org.jnosql.artemis.IdNotFoundException;
import org.jnosql.artemis.model.Job;
import org.jnosql.artemis.model.Person;
import org.jnosql.artemis.reflection.ClassRepresentations;
import org.jnosql.diana.api.NonUniqueResultException;
import org.jnosql.diana.api.document.Document;
import org.jnosql.diana.api.document.DocumentCollectionManager;
import org.jnosql.diana.api.document.DocumentCondition;
import org.jnosql.diana.api.document.DocumentDeleteQuery;
import org.jnosql.diana.api.document.DocumentEntity;
import org.jnosql.diana.api.document.DocumentQuery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.time.Duration;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.jnosql.diana.api.document.query.DocumentQueryBuilder.delete;
import static org.jnosql.diana.api.document.query.DocumentQueryBuilder.select;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@RunWith(CDIJUnitRunner.class)
public class DefaultDocumentTemplateTest {

    private Person person = Person.builder().
            withAge().
            withPhones(Arrays.asList("234", "432")).
            withName("Name")
            .withId(19)
            .withIgnore().build();

    private Document[] documents = new Document[]{
            Document.of("age", 10),
            Document.of("phones", Arrays.asList("234", "432")),
            Document.of("name", "Name"),
            Document.of("id", 19L),
    };


    @Inject
    private DocumentEntityConverter converter;

    @Inject
    private ClassRepresentations classRepresentations;

    @Inject
    private Converters converters;

    private DocumentCollectionManager managerMock;

    private DefaultDocumentTemplate subject;

    private ArgumentCaptor<DocumentEntity> captor;

    private DocumentEventPersistManager documentEventPersistManager;

    @SuppressWarnings("unchecked")
    @Before
    public void setUp() {
        managerMock = Mockito.mock(DocumentCollectionManager.class);
        documentEventPersistManager = Mockito.mock(DocumentEventPersistManager.class);
        captor = ArgumentCaptor.forClass(DocumentEntity.class);
        Instance<DocumentCollectionManager> instance = Mockito.mock(Instance.class);
        when(instance.get()).thenReturn(managerMock);
        DefaultDocumentWorkflow workflow = new DefaultDocumentWorkflow(documentEventPersistManager, converter);
        this.subject = new DefaultDocumentTemplate(converter, instance, workflow,
                documentEventPersistManager, classRepresentations, converters);
    }

    @Test
    public void shouldSave() {
        DocumentEntity document = DocumentEntity.of("Person");
        document.addAll(Stream.of(documents).collect(Collectors.toList()));

        when(managerMock
                .insert(any(DocumentEntity.class)))
                .thenReturn(document);

        subject.insert(this.person);
        verify(managerMock).insert(captor.capture());
        verify(documentEventPersistManager).firePostEntity(any(Person.class));
        verify(documentEventPersistManager).firePreEntity(any(Person.class));
        verify(documentEventPersistManager).firePreDocument(any(DocumentEntity.class));
        verify(documentEventPersistManager).firePostDocument(any(DocumentEntity.class));
        DocumentEntity value = captor.getValue();
        assertEquals("Person", value.getName());
        assertEquals(4, value.getDocuments().size());
    }

    @Test
    public void shouldSaveTTL() {

        Duration twoHours = Duration.ofHours(2L);

        DocumentEntity document = DocumentEntity.of("Person");
        document.addAll(Stream.of(documents).collect(Collectors.toList()));

        when(managerMock.insert(any(DocumentEntity.class),
                Mockito.eq(twoHours)))
                .thenReturn(document);

        subject.insert(this.person, twoHours);
        verify(managerMock).insert(captor.capture(), Mockito.eq(twoHours));
        verify(documentEventPersistManager).firePostEntity(any(Person.class));
        verify(documentEventPersistManager).firePreEntity(any(Person.class));
        verify(documentEventPersistManager).firePreDocument(any(DocumentEntity.class));
        verify(documentEventPersistManager).firePostDocument(any(DocumentEntity.class));
        DocumentEntity value = captor.getValue();
        assertEquals("Person", value.getName());
        assertEquals(4, value.getDocuments().size());
    }


    @Test
    public void shouldUpdate() {
        DocumentEntity document = DocumentEntity.of("Person");
        document.addAll(Stream.of(documents).collect(Collectors.toList()));

        when(managerMock
                .update(any(DocumentEntity.class)))
                .thenReturn(document);

        subject.update(this.person);
        verify(managerMock).update(captor.capture());
        verify(documentEventPersistManager).firePostEntity(any(Person.class));
        verify(documentEventPersistManager).firePreEntity(any(Person.class));
        verify(documentEventPersistManager).firePreDocument(any(DocumentEntity.class));
        verify(documentEventPersistManager).firePostDocument(any(DocumentEntity.class));
        DocumentEntity value = captor.getValue();
        assertEquals("Person", value.getName());
        assertEquals(4, value.getDocuments().size());
    }


    @Test
    public void shouldInsertEntitiesTTL() {
        DocumentEntity documentEntity = DocumentEntity.of("Person");
        documentEntity.addAll(Stream.of(documents).collect(Collectors.toList()));
        Duration duration = Duration.ofHours(2);

        Mockito.when(managerMock
                .insert(any(DocumentEntity.class), Mockito.eq(duration)))
                .thenReturn(documentEntity);

        subject.insert(Arrays.asList(person, person), duration);
        verify(managerMock, times(2)).insert(any(DocumentEntity.class), any(Duration.class));
    }

    @Test
    public void shouldInsertEntities() {
        DocumentEntity documentEntity = DocumentEntity.of("Person");
        documentEntity.addAll(Stream.of(documents).collect(Collectors.toList()));

        Mockito.when(managerMock
                .insert(any(DocumentEntity.class)))
                .thenReturn(documentEntity);

        subject.insert(Arrays.asList(person, person));
        verify(managerMock, times(2)).insert(any(DocumentEntity.class));
    }

    @Test
    public void shouldUpdateEntities() {
        DocumentEntity documentEntity = DocumentEntity.of("Person");
        documentEntity.addAll(Stream.of(documents).collect(Collectors.toList()));

        Mockito.when(managerMock
                .update(any(DocumentEntity.class)))
                .thenReturn(documentEntity);

        subject.update(Arrays.asList(person, person));
        verify(managerMock, times(2)).update(any(DocumentEntity.class));
    }


    @Test
    public void shouldDelete() {

        DocumentDeleteQuery query = delete().from("delete").build();
        subject.delete(query);
        verify(managerMock).delete(query);
    }

    @Test
    public void shouldSelect() {
        DocumentQuery query = select().from("delete").build();
        subject.select(query);
        verify(managerMock).select(query);
    }


    @Test
    public void shouldReturnSingleResult() {
        DocumentEntity columnEntity = DocumentEntity.of("Person");
        columnEntity.addAll(Stream.of(documents).collect(Collectors.toList()));

        Mockito.when(managerMock
                .select(any(DocumentQuery.class)))
                .thenReturn(singletonList(columnEntity));

        DocumentQuery query = select().from("person").build();

        Optional<Person> result = subject.singleResult(query);
        assertTrue(result.isPresent());
    }

    @Test
    public void shouldReturnSingleResultIsEmpty() {
        Mockito.when(managerMock
                .select(any(DocumentQuery.class)))
                .thenReturn(emptyList());

        DocumentQuery query = select().from("person").build();

        Optional<Person> result = subject.singleResult(query);
        assertFalse(result.isPresent());
    }

    @Test(expected = NonUniqueResultException.class)
    public void shouldReturnErrorWhenThereMoreThanASingleResult() {
        DocumentEntity columnEntity = DocumentEntity.of("Person");
        columnEntity.addAll(Stream.of(documents).collect(Collectors.toList()));

        Mockito.when(managerMock
                .select(any(DocumentQuery.class)))
                .thenReturn(Arrays.asList(columnEntity, columnEntity));

        DocumentQuery query = select().from("person").build();

        subject.singleResult(query);
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
        ArgumentCaptor<DocumentQuery> queryCaptor = ArgumentCaptor.forClass(DocumentQuery.class);
        verify(managerMock).select(queryCaptor.capture());
        DocumentQuery query = queryCaptor.getValue();
        DocumentCondition condition = query.getCondition().get();

        assertEquals("Person", query.getDocumentCollection());
        assertEquals(DocumentCondition.eq(Document.of("_id", 10L)), condition);

    }

    @Test
    public void shouldDeleteEntity() {
        subject.delete(Person.class, "10");
        ArgumentCaptor<DocumentDeleteQuery> queryCaptor = ArgumentCaptor.forClass(DocumentDeleteQuery.class);
        verify(managerMock).delete(queryCaptor.capture());
        DocumentDeleteQuery query = queryCaptor.getValue();
        DocumentCondition condition = query.getCondition().get();

        assertEquals("Person", query.getDocumentCollection());
        assertEquals(DocumentCondition.eq(Document.of("_id", 10L)), condition);

    }

}