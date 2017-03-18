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

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

/**
 * The default implementation of {@link DocumentEventPersistManager}
 */
@ApplicationScoped
class DefaultDocumentEventPersistManager implements DocumentEventPersistManager {

    @Inject
    private Event<DocumentEntityPrePersist> documentEntityPrePersistEvent;

    @Inject
    private Event<DocumentEntityPostPersist> documentEntityPostPersistEvent;

    @Inject
    private Event<EntityPrePersist> entityPrePersistEvent;

    @Inject
    private Event<EntityPostPersit> entityPostPersitEvent;

    @Inject
    private Event<EntityDocumentPrePersist> entityDocumentPrePersist;

    @Inject
    private Event<EntityDocumentPostPersist> entityDocumentPostPersist;

    @Inject
    private Event<DocumentQueryExecute> documentQueryExecute;

    @Inject
    private Event<DocumentDeleteQueryExecute> documentDeleteQueryExecute;

    @Override
    public void firePreDocument(DocumentEntity entity) {
        documentEntityPrePersistEvent.fire(DocumentEntityPrePersist.of(entity));
    }

    @Override
    public void firePostDocument(DocumentEntity entity) {
        documentEntityPostPersistEvent.fire(DocumentEntityPostPersist.of(entity));
    }

    @Override
    public <T> void firePreEntity(T entity) {
        entityPrePersistEvent.fire(EntityPrePersist.of(entity));
    }

    @Override
    public <T> void firePostEntity(T entity) {
        entityPostPersitEvent.fire(EntityPostPersit.of(entity));
    }

    @Override
    public <T> void firePreDocumentEntity(T entity) {
        entityDocumentPrePersist.fire(EntityDocumentPrePersist.of(entity));
    }

    @Override
    public <T> void firePostDocumentEntity(T entity) {
        entityDocumentPostPersist.fire(EntityDocumentPostPersist.of(entity));
    }

    @Override
    public void firePreQuery(DocumentQuery query) {
        documentQueryExecute.fire(DocumentQueryExecute.of(query));
    }

    @Override
    public void firePreDeleteQuery(DocumentDeleteQuery query) {
        documentDeleteQueryExecute.fire(DocumentDeleteQueryExecute.of(query));
    }
}
