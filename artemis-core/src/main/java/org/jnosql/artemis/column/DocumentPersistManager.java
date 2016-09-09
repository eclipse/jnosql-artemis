package org.jnosql.artemis.column;


import org.jnosql.diana.api.document.DocumentCollectionEntity;

public interface DocumentPersistManager {

    void firePreDocument(DocumentCollectionEntity entity);

    void firePostDocument(DocumentCollectionEntity entity);

    <T> void firePreEntity(T entity);

    <T> void firePostEntity(T entity);

}
