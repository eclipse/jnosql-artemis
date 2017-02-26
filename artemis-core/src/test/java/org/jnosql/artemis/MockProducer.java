/*
 * Copyright 2017 Otavio Santana and others
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
package org.jnosql.artemis;


import org.jnosql.artemis.column.ColumnRepository;
import org.jnosql.artemis.column.ColumnRepositoryAsync;
import org.jnosql.artemis.document.DocumentRepository;
import org.jnosql.artemis.document.DocumentRepositoryAsync;
import org.jnosql.artemis.model.Person;
import org.jnosql.diana.api.column.ColumnEntity;
import org.jnosql.diana.api.column.ColumnFamilyManager;
import org.jnosql.diana.api.column.ColumnFamilyManagerAsync;
import org.jnosql.diana.api.document.Document;
import org.jnosql.diana.api.document.DocumentCollectionManager;
import org.jnosql.diana.api.document.DocumentCollectionManagerAsync;
import org.jnosql.diana.api.document.DocumentEntity;
import org.mockito.Mockito;

import javax.enterprise.inject.Produces;

import static org.mockito.Mockito.mock;

public class MockProducer {

    @Produces
    public DocumentCollectionManager getDocumentCollectionManager() {
        DocumentEntity entity = DocumentEntity.of("Person");
        entity.add(Document.of("name", "Default"));
        entity.add(Document.of("age", 10));
        DocumentCollectionManager manager = mock(DocumentCollectionManager.class);
        Mockito.when(manager.save(Mockito.any(DocumentEntity.class))).thenReturn(entity);
        return manager;

    }

    @Produces
    public ColumnFamilyManager getColumnFamilyManager() {
        ColumnEntity entity = ColumnEntity.of("Person");
        entity.add(org.jnosql.diana.api.column.Column.of("name", "Default"));
        entity.add(org.jnosql.diana.api.column.Column.of("age", 10));
        ColumnFamilyManager manager = mock(ColumnFamilyManager.class);
        Mockito.when(manager.save(Mockito.any(ColumnEntity.class))).thenReturn(entity);
        return manager;

    }

    @Produces
    @ArtemisDatabase(value = DatabaseType.DOCUMENT, provider = "documentRepositoryMock")
    public DocumentRepository getDocumentRepository() {
        DocumentRepository documentRepository = mock(DocumentRepository.class);
        Mockito.when(documentRepository.save(Mockito.any(Person.class))).thenReturn(Person.builder()
                .withName("documentRepositoryMock").build());
        return documentRepository;
    }

    @Produces
    @ArtemisDatabase(value = DatabaseType.COLUMN, provider = "columnRepositoryMock")
    public ColumnRepository getColumnRepository() {
        ColumnRepository documentRepository = mock(ColumnRepository.class);
        Mockito.when(documentRepository.save(Mockito.any(Person.class))).thenReturn(Person.builder()
                .withName("columnRepositoryMock").build());
        return documentRepository;
    }


    @Produces
    public DocumentCollectionManagerAsync getDocumentCollectionManagerAsync() {
        return Mockito.mock(DocumentCollectionManagerAsync.class);
    }

    @Produces
    public ColumnFamilyManagerAsync getColumnFamilyManagerAsync() {
        return Mockito.mock(ColumnFamilyManagerAsync.class);
    }


    @Produces
    @ArtemisDatabase(value = DatabaseType.DOCUMENT, provider = "documentRepositoryMock")
    public DocumentRepositoryAsync getDocumentRepositoryAsync() {
        return mock(DocumentRepositoryAsync.class);
    }

    @Produces
    @ArtemisDatabase(value = DatabaseType.COLUMN, provider = "columnRepositoryMock")
    public ColumnRepositoryAsync getColumnRepositoryAsync() {
        return mock(ColumnRepositoryAsync.class);
    }

}
