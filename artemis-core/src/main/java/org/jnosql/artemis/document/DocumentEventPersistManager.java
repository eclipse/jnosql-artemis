package org.jnosql.artemis.document;


import org.jnosql.diana.api.document.DocumentCollectionEntity;

public interface DocumentEventPersistManager {

    void firePreDocument(DocumentCollectionEntity entity);

    void firePostDocument(DocumentCollectionEntity entity);

    <T> void firePreEntity(T entity);

    <T> void firePostEntity(T entity);

}
