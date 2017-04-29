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


import org.jnosql.diana.api.document.DocumentCollectionManager;
import org.jnosql.diana.api.document.DocumentDeleteQuery;
import org.jnosql.diana.api.document.DocumentEntity;
import org.jnosql.diana.api.document.DocumentQuery;

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

/**
 * This class provides a skeletal implementation of the {@link DocumentTemplate} interface,
 * to minimize the effort required to implement this interface.
 */
public abstract class AbstractDocumentTemplate implements DocumentTemplate {


    protected abstract DocumentEntityConverter getConverter();

    protected abstract DocumentCollectionManager getManager();

    protected abstract DocumentWorkflow getWorkflow();

    protected abstract DocumentEventPersistManager getPersistManager();

    @Override
    public <T> T save(T entity) throws NullPointerException {
        Objects.requireNonNull(entity, "entity is required");
        UnaryOperator<DocumentEntity> saveAction = e -> getManager().save(e);
        return getWorkflow().flow(entity, saveAction);
    }


    @Override
    public <T> T save(T entity, Duration ttl) {
        Objects.requireNonNull(entity, "entity is required");
        Objects.requireNonNull(ttl, "ttl is required");
        UnaryOperator<DocumentEntity> saveAction = e -> getManager().save(e, ttl);
        return getWorkflow().flow(entity, saveAction);
    }



    @Override
    public <T> T update(T entity) {
        Objects.requireNonNull(entity, "entity is required");
        UnaryOperator<DocumentEntity> saveAction = e -> getManager().update(e);
        return getWorkflow().flow(entity, saveAction);
    }



    @Override
    public void delete(DocumentDeleteQuery query) {
        Objects.requireNonNull(query, "query is required");
        getPersistManager().firePreDeleteQuery(query);
        getManager().delete(query);
    }

    @Override
    public <T> List<T> find(DocumentQuery query) throws NullPointerException {
        Objects.requireNonNull(query, "query is required");
        getPersistManager().firePreQuery(query);
        List<DocumentEntity> entities = getManager().find(query);
        Function<DocumentEntity, T> function = e -> getConverter().toEntity(e);
        return entities.stream().map(function).collect(Collectors.toList());
    }
}
