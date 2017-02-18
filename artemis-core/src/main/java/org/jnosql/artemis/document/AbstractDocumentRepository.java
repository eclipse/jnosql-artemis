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
package org.jnosql.artemis.document;


import org.jnosql.diana.api.document.DocumentCollectionManager;
import org.jnosql.diana.api.document.DocumentDeleteQuery;
import org.jnosql.diana.api.document.DocumentEntity;
import org.jnosql.diana.api.document.DocumentQuery;

import java.time.Duration;
import java.util.List;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

/**
 * This class provides a skeletal implementation of the {@link DocumentRepository} interface,
 * to minimize the effort required to implement this interface.
 */
public abstract class AbstractDocumentRepository implements DocumentRepository {


    protected abstract DocumentEntityConverter getConverter();

    protected abstract DocumentCollectionManager getManager();

    protected abstract DocumentWorkflow getWorkflow();

    @Override
    public <T> T save(T entity) throws NullPointerException {

        UnaryOperator<DocumentEntity> saveAction = e -> getManager().save(e);
        return getWorkflow().flow(entity, saveAction);
    }


    @Override
    public <T> T save(T entity, Duration ttl) {
        UnaryOperator<DocumentEntity> saveAction = e -> getManager().save(e, ttl);
        return getWorkflow().flow(entity, saveAction);
    }



    @Override
    public <T> T update(T entity) {

        UnaryOperator<DocumentEntity> saveAction = e -> getManager().update(e);
        return getWorkflow().flow(entity, saveAction);
    }



    @Override
    public void delete(DocumentDeleteQuery query) {
        getManager().delete(query);
    }

    @Override
    public <T> List<T> find(DocumentQuery query) throws NullPointerException {
        List<DocumentEntity> entities = getManager().find(query);
        Function<DocumentEntity, T> function = e -> getConverter().toEntity(e);
        return entities.stream().map(function).collect(Collectors.toList());
    }
}
