package org.jnosql.artemis.column;


import java.util.Objects;
import org.jnosql.artemis.document.DefaultDocumentEntityPersist;
import org.jnosql.diana.api.document.DocumentCollectionEntity;

public interface DocumentEntityPrePersist {

    DocumentCollectionEntity getEntity();

    static DocumentEntityPrePersist of(DocumentCollectionEntity entity) throws NullPointerException {
        Objects.requireNonNull(entity, "Entity is required");
        return new DefaultDocumentEntityPersist(entity);
    }
}
