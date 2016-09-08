package org.jnosql.artemis.document;

import org.jnosql.diana.api.document.DocumentCollectionEntity;


public interface DocumentEntityConverter {

    DocumentCollectionEntity toDocument(Object entityInstance);

    <T> T toEntity(Class<T> entityClass, DocumentCollectionEntity entity);

    <T> T toEntity(DocumentCollectionEntity entity);
}
