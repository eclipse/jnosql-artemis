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


import org.jnosql.artemis.reflection.ClassRepresentations;
import org.jnosql.diana.api.document.DocumentCollectionManager;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Vetoed;
import javax.inject.Inject;
import java.util.Objects;

/**
 * The default implementation of {@link DocumentTemplateProducer}
 */
@ApplicationScoped
class DefaultDocumentTemplateProducer implements DocumentTemplateProducer {


    @Inject
    private DocumentEntityConverter converter;

    @Inject
    private DocumentWorkflow workflow;

    @Inject
    private DocumentEventPersistManager persistManager;

    @Inject
    private ClassRepresentations classRepresentations;


    @Override
    public DocumentTemplate get(DocumentCollectionManager collectionManager) throws NullPointerException {
        Objects.requireNonNull(collectionManager, "collectionManager is required");
        return new ProducerDocumentTemplate(converter, collectionManager, workflow,
                persistManager, classRepresentations);
    }

    @Vetoed
    static class ProducerDocumentTemplate extends AbstractDocumentTemplate {

        private DocumentEntityConverter converter;

        private DocumentCollectionManager manager;

        private DocumentWorkflow workflow;

        private DocumentEventPersistManager persistManager;

        private ClassRepresentations classRepresentations;
        ProducerDocumentTemplate(DocumentEntityConverter converter, DocumentCollectionManager manager,
                                 DocumentWorkflow workflow,
                                 DocumentEventPersistManager persistManager,
                                 ClassRepresentations classRepresentations) {
            this.converter = converter;
            this.manager = manager;
            this.workflow = workflow;
            this.persistManager = persistManager;
            this.classRepresentations = classRepresentations;
        }

        ProducerDocumentTemplate() {
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

        @Override
        protected DocumentEventPersistManager getPersistManager() {
            return persistManager;
        }

        @Override
        protected ClassRepresentations getClassRepresentations() {
            return classRepresentations;
        }
    }
}
