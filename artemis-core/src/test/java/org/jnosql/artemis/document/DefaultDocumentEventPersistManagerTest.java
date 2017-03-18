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
package org.jnosql.artemis.document;

import org.jnosql.artemis.EntityPostPersit;
import org.jnosql.artemis.EntityPrePersist;
import org.jnosql.diana.api.document.DocumentDeleteQuery;
import org.jnosql.diana.api.document.DocumentEntity;
import org.jnosql.diana.api.document.DocumentQuery;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.enterprise.event.Event;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class DefaultDocumentEventPersistManagerTest {

    @InjectMocks
    private DefaultDocumentEventPersistManager subject;

    @Mock
    private Event<DocumentEntityPrePersist> documentEntityPrePersistEvent;

    @Mock
    private Event<DocumentEntityPostPersist> documentEntityPostPersistEvent;

    @Mock
    private Event<EntityPrePersist> entityPrePersistEvent;

    @Mock
    private Event<EntityPostPersit> entityPostPersitEvent;

    @Mock
    private Event<EntityDocumentPrePersist> entityDocumentPrePersist;

    @Mock
    private Event<EntityDocumentPostPersist> entityDocumentPostPersist;

    @Mock
    private Event<DocumentQueryExecute> documentQueryExecute;

    @Mock
    private Event<DocumentDeleteQueryExecute> documentDeleteQueryExecute;


    @Test
    public void shouldFirePreColumn() {
        DocumentEntity entity = DocumentEntity.of("collection");
        subject.firePreDocument(entity);
        ArgumentCaptor<DocumentEntityPrePersist> captor = ArgumentCaptor.forClass(DocumentEntityPrePersist.class);
        verify(documentEntityPrePersistEvent).fire(captor.capture());

        DocumentEntityPrePersist captorValue = captor.getValue();
        assertEquals(entity, captorValue.getEntity());
    }


    @Test
    public void shouldFirePostColumn() {
        DocumentEntity entity = DocumentEntity.of("collection");
        subject.firePostDocument(entity);
        ArgumentCaptor<DocumentEntityPostPersist> captor = ArgumentCaptor.forClass(DocumentEntityPostPersist.class);
        verify(documentEntityPostPersistEvent).fire(captor.capture());

        DocumentEntityPostPersist captorValue = captor.getValue();
        assertEquals(entity, captorValue.getEntity());
    }

    @Test
    public void shouldFirePreEntity() {
        Jedi jedi = new Jedi();
        jedi.name = "Luke";
        subject.firePreEntity(jedi);
        ArgumentCaptor<EntityPrePersist> captor = ArgumentCaptor.forClass(EntityPrePersist.class);
        verify(entityPrePersistEvent).fire(captor.capture());
        EntityPrePersist value = captor.getValue();
        assertEquals(jedi, value.getValue());
    }

    @Test
    public void shouldFirePostEntity() {
        Jedi jedi = new Jedi();
        jedi.name = "Luke";
        subject.firePostEntity(jedi);
        ArgumentCaptor<EntityPostPersit> captor = ArgumentCaptor.forClass(EntityPostPersit.class);
        verify(entityPostPersitEvent).fire(captor.capture());
        EntityPostPersit value = captor.getValue();
        assertEquals(jedi, value.getValue());
    }
    //

    @Test
    public void shouldFirePreDocumentEntity() {
        Jedi jedi = new Jedi();
        jedi.name = "Luke";
        subject.firePreDocumentEntity(jedi);
        ArgumentCaptor<EntityDocumentPrePersist> captor = ArgumentCaptor.forClass(EntityDocumentPrePersist.class);
        verify(entityDocumentPrePersist).fire(captor.capture());
        EntityDocumentPrePersist value = captor.getValue();
        assertEquals(jedi, value.getValue());
    }

    @Test
    public void shouldFirePostDocumentEntity() {
        Jedi jedi = new Jedi();
        jedi.name = "Luke";
        subject.firePostDocumentEntity(jedi);
        ArgumentCaptor<EntityDocumentPostPersist> captor = ArgumentCaptor.forClass(EntityDocumentPostPersist.class);
        verify(entityDocumentPostPersist).fire(captor.capture());
        EntityDocumentPostPersist value = captor.getValue();
        assertEquals(jedi, value.getValue());
    }

    @Test
    public void shouldFirePreQuery() {
        DocumentQuery query = DocumentQuery.of("collection");
        subject.firePreQuery(query);
        ArgumentCaptor<DocumentQueryExecute> captor = ArgumentCaptor.forClass(DocumentQueryExecute.class);
        verify(documentQueryExecute).fire(captor.capture());
        assertEquals(query, captor.getValue().getQuery());
    }

    @Test
    public void shouldFirePreDeleteQuery() {
        DocumentDeleteQuery query = DocumentDeleteQuery.of("collection");
        subject.firePreDeleteQuery(query);
        ArgumentCaptor<DocumentDeleteQueryExecute> captor = ArgumentCaptor.forClass(DocumentDeleteQueryExecute.class);
        verify(documentDeleteQueryExecute).fire(captor.capture());
        assertEquals(query, captor.getValue().getQuery());
    }


    class Jedi {
        private String name;
    }

}