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

import javax.enterprise.inject.Vetoed;
import javax.inject.Inject;
import java.util.Objects;

/**
 * The default implementation of {@link DocumentRepositoryProducer}
 */
class DefaultDocumentRepositoryProducer implements DocumentRepositoryProducer {


    @Inject
    private DocumentEntityConverter converter;

    @Inject
    private DocumentWorkflow workflow;


    @Override
    public DocumentRepository get(DocumentCollectionManager collectionManager) throws NullPointerException {
        Objects.requireNonNull(collectionManager, "collectionManager is required");
        return new ProducerDocumentRepository(converter, collectionManager, workflow);
    }

    @Vetoed
    static class ProducerDocumentRepository extends AbstractDocumentRepository {

        private DocumentEntityConverter converter;

        private DocumentCollectionManager manager;

        private DocumentWorkflow workflow;

        ProducerDocumentRepository(DocumentEntityConverter converter, DocumentCollectionManager manager, DocumentWorkflow workflow) {
            this.converter = converter;
            this.manager = manager;
            this.workflow = workflow;
        }

        ProducerDocumentRepository() {
        }

        @Override
        protected DocumentEntityConverter getConverter() {
            return converter;
        }

        @Override
        protected DocumentCollectionManager getManager() {
            return manager;
        }

        @Override
        protected DocumentWorkflow getWorkflow() {
            return workflow;
        }
    }
}
