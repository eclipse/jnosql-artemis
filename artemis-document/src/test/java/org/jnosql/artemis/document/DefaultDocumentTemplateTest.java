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
import org.jnosql.artemis.IdNotFoundException;
import org.jnosql.artemis.model.Animal;
import org.jnosql.artemis.model.Person;
import org.jnosql.artemis.reflection.ClassRepresentations;
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
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.jnosql.diana.api.document.query.DocumentQueryBuilder.delete;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
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
                documentEventPersistManager, classRepresentations);
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
    public void shouldDelete() {

        DocumentDeleteQuery query = delete().from("delete").build();
        subject.delete(query);
        verify(managerMock).delete(query);
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
        subject.find(Animal.class, "10");
    }

    @Test
    public void shouldReturnFind() {
        subject.find(Person.class, "10");
        ArgumentCaptor<DocumentQuery> queryCaptor = ArgumentCaptor.forClass(DocumentQuery.class);
        verify(managerMock).select(queryCaptor.capture());
        DocumentQuery query = queryCaptor.getValue();
        DocumentCondition condition = query.getCondition().get();

        assertEquals("Person", query.getDocumentCollection());
        assertEquals(DocumentCondition.eq(Document.of("_id", "10")), condition);

    }

}