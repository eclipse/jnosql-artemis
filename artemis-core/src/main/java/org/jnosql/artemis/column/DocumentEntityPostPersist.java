package org.jnosql.artemis.column;


import java.util.Objects;
import org.jnosql.artemis.document.DefaultDocumentEntityPersist;
import org.jnosql.diana.api.document.DocumentCollectionEntity;

public interface DocumentEntityPostPersist {

    DocumentCollectionEntity getEntity();

    static DocumentEntityPostPersist of(DocumentCollectionEntity entity) throws NullPointerException {
        Objects.requireNonNull(entity, "Entity is required");
        return new DefaultDocumentEntityPersist(entity);
    }

}
