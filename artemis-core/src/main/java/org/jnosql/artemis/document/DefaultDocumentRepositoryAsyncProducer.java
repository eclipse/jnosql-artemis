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


import org.jnosql.diana.api.document.DocumentCollectionManagerAsync;

import javax.enterprise.inject.Vetoed;
import javax.inject.Inject;
import java.util.Objects;

/**
 * The default implementation of {@link DocumentRepositoryAsyncProducer}
 */
class DefaultDocumentRepositoryAsyncProducer implements DocumentRepositoryAsyncProducer {


    @Inject
    private DocumentEntityConverter converter;

    @Override
    public DocumentRepositoryAsync get(DocumentCollectionManagerAsync collectionManager) throws NullPointerException {
        Objects.requireNonNull(collectionManager, "collectionManager is required");
        return new ProducerAbstractDocumentRepositoryAsync(converter, collectionManager);
    }

    @Vetoed
    static class ProducerAbstractDocumentRepositoryAsync extends AbstractDocumentRepositoryAsync {

        private DocumentEntityConverter converter;

        private DocumentCollectionManagerAsync manager;

        ProducerAbstractDocumentRepositoryAsync(DocumentEntityConverter converter, DocumentCollectionManagerAsync manager) {
            this.converter = converter;
            this.manager = manager;
        }

        ProducerAbstractDocumentRepositoryAsync() {
        }

        @Override
        protected DocumentEntityConverter getConverter() {
            return converter;
        }

        @Override
        protected DocumentCollectionManagerAsync getManager() {
            return manager;
        }
    }
}
