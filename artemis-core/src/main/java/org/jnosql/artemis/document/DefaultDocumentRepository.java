/*
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
package org.jnosql.artemis.document;

import org.jnosql.diana.api.document.DocumentCollectionManager;
import org.jnosql.diana.api.document.DocumentDeleteQuery;
import org.jnosql.diana.api.document.DocumentEntity;
import org.jnosql.diana.api.document.DocumentQuery;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.time.Duration;
import java.util.List;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

/**
 * The default implementation of {@link DocumentRepository}
 */
@SuppressWarnings("unchecked")
@DocumentRepositoryInterceptor
class DefaultDocumentRepository implements DocumentRepository {


    private DocumentEntityConverter converter;

    private Instance<DocumentCollectionManager> manager;

    private DocumentWorkflow workflow;


    @Inject
    DefaultDocumentRepository(DocumentEntityConverter converter, Instance<DocumentCollectionManager> manager, DocumentWorkflow workflow) {
        this.converter = converter;
        this.manager = manager;
        this.workflow = workflow;
    }

    DefaultDocumentRepository() {
    }

    @Override
    public <T> T save(T entity) throws NullPointerException {

        UnaryOperator<DocumentEntity> saveAction = e -> manager.get().save(e);
        return workflow.flow(entity, saveAction);
    }


    @Override
    public <T> T save(T entity, Duration ttl) {
        UnaryOperator<DocumentEntity> saveAction = e -> manager.get().save(e, ttl);
        return workflow.flow(entity, saveAction);
    }



    @Override
    public <T> T update(T entity) {

        UnaryOperator<DocumentEntity> saveAction = e -> manager.get().update(e);
        return workflow.flow(entity, saveAction);
    }



    @Override
    public void delete(DocumentDeleteQuery query) {
        manager.get().delete(query);
    }

    @Override
    public <T> List<T> find(DocumentQuery query) throws NullPointerException {
        List<DocumentEntity> entities = manager.get().find(query);
        Function<DocumentEntity, T> function = e -> converter.toEntity(e);
        return entities.stream().map(function).collect(Collectors.toList());
    }

}
