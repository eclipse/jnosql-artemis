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
package org.jnosql.artemis;


import org.jnosql.artemis.document.DocumentTemplate;
import org.jnosql.artemis.document.DocumentTemplateAsync;
import org.jnosql.artemis.model.Person;
import org.jnosql.artemis.model.User;
import org.jnosql.diana.api.Value;
import org.jnosql.diana.api.column.ColumnEntity;
import org.jnosql.diana.api.column.ColumnFamilyManager;
import org.jnosql.diana.api.column.ColumnFamilyManagerAsync;
import org.jnosql.diana.api.document.Document;
import org.jnosql.diana.api.document.DocumentCollectionManager;
import org.jnosql.diana.api.document.DocumentCollectionManagerAsync;
import org.jnosql.diana.api.document.DocumentEntity;
import org.jnosql.diana.api.key.BucketManager;
import org.mockito.Mockito;

import javax.enterprise.inject.Produces;

import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MockProducer {

    @Produces
    public DocumentCollectionManager getDocumentCollectionManager() {
        DocumentEntity entity = DocumentEntity.of("Person");
        entity.add(Document.of("name", "Default"));
        entity.add(Document.of("age", 10));
        DocumentCollectionManager manager = mock(DocumentCollectionManager.class);
        when(manager.insert(Mockito.any(DocumentEntity.class))).thenReturn(entity);
        return manager;

    }

    @Produces
    @Database(value = DatabaseType.DOCUMENT, provider = "documentRepositoryMock")
    public DocumentCollectionManager getDocumentCollectionManagerMock() {
        DocumentEntity entity = DocumentEntity.of("Person");
        entity.add(Document.of("name", "documentRepositoryMock"));
        entity.add(Document.of("age", 10));
        DocumentCollectionManager manager = mock(DocumentCollectionManager.class);
        when(manager.insert(Mockito.any(DocumentEntity.class))).thenReturn(entity);
        return manager;

    }

    @Produces
    public ColumnFamilyManager getColumnFamilyManager() {
        ColumnEntity entity = ColumnEntity.of("Person");
        entity.add(org.jnosql.diana.api.column.Column.of("name", "Default"));
        entity.add(org.jnosql.diana.api.column.Column.of("age", 10));
        ColumnFamilyManager manager = mock(ColumnFamilyManager.class);
        when(manager.insert(Mockito.any(ColumnEntity.class))).thenReturn(entity);
        return manager;

    }

    @Produces
    @Database(value = DatabaseType.COLUMN, provider = "columnRepositoryMock")
    public ColumnFamilyManager getColumnFamilyManagerMock() {
        ColumnEntity entity = ColumnEntity.of("Person");
        entity.add(org.jnosql.diana.api.column.Column.of("name", "columnRepositoryMock"));
        entity.add(org.jnosql.diana.api.column.Column.of("age", 10));
        ColumnFamilyManager manager = mock(ColumnFamilyManager.class);
        when(manager.insert(Mockito.any(ColumnEntity.class))).thenReturn(entity);
        return manager;

    }


    @Produces
    @Database(value = DatabaseType.DOCUMENT, provider = "documentRepositoryMock")
    public DocumentTemplate getDocumentRepository() {
        DocumentTemplate documentTemplate = mock(DocumentTemplate.class);
        when(documentTemplate.save(Mockito.any(Person.class))).thenReturn(Person.builder()
                .withName("documentRepositoryMock").build());
        return documentTemplate;
    }


    @Produces
    public DocumentCollectionManagerAsync getDocumentCollectionManagerAsync() {
        return Mockito.mock(DocumentCollectionManagerAsync.class);
    }


    @Produces
    @Database(value = DatabaseType.DOCUMENT, provider = "documentRepositoryMock")
    public DocumentCollectionManagerAsync getDocumentCollectionManagerAsyncMock() {
        return Mockito.mock(DocumentCollectionManagerAsync.class);
    }
    @Produces
    public ColumnFamilyManagerAsync getColumnFamilyManagerAsync() {
        return Mockito.mock(ColumnFamilyManagerAsync.class);
    }


    @Produces
    @Database(value = DatabaseType.COLUMN, provider = "columnRepositoryMock")
    public ColumnFamilyManagerAsync getColumnFamilyManagerAsyncMock() {
        return Mockito.mock(ColumnFamilyManagerAsync.class);
    }

    @Produces
    @Database(value = DatabaseType.DOCUMENT, provider = "documentRepositoryMock")
    public DocumentTemplateAsync getDocumentRepositoryAsync() {
        return mock(DocumentTemplateAsync.class);
    }


    @Produces
    public BucketManager getBucketManager() {
        BucketManager bucketManager = Mockito.mock(BucketManager.class);
        Person person = Person.builder().withName("Default").build();
        when(bucketManager.get("key")).thenReturn(Optional.ofNullable(Value.of(person)));
        when(bucketManager.get("user")).thenReturn(Optional.of(Value.of(new User("Default", "Default", 10))));
        return bucketManager;
    }

    @Produces
    @Database(value = DatabaseType.KEY_VALUE, provider = "keyvalueMock")
    public BucketManager getBucketManagerMock() {
        BucketManager bucketManager = Mockito.mock(BucketManager.class);
        Person person = Person.builder().withName("keyvalueMock").build();
        when(bucketManager.get("key")).thenReturn(Optional.ofNullable(Value.of(person)));
        when(bucketManager.get("user")).thenReturn(Optional.of(Value.of(new User("keyvalueMock", "keyvalueMock", 10))));
        return bucketManager;
    }


}
