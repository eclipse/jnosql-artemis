package org.jnosql.artemis.document;


import org.jnosql.diana.api.document.DocumentCollectionManagerAsync;

import javax.inject.Inject;
import java.util.Objects;


class DefaultDocumentRepositoryAsyncProducer implements DocumentRepositoryAsyncProducer {


    @Inject
    private DocumentEntityConverter converter;

    @Override
    public DocumentRepositoryAsync get(DocumentCollectionManagerAsync collectionManager) throws NullPointerException {
        Objects.requireNonNull(collectionManager, "collectionManager is required");
        return new ProducerAbstractDocumentRepositoryAsync(converter, collectionManager);
    }

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
