package org.jnosql.artemis.document;


import java.util.Objects;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.jnosql.diana.api.document.DocumentCollectionEntity;

public class DefaultDocumentEntityPersist implements DocumentEntityPrePersist, DocumentEntityPostPersist {

    private final DocumentCollectionEntity entity;

    DefaultDocumentEntityPersist(DocumentCollectionEntity entity) {
        this.entity = entity;
    }

    @Override
    public DocumentCollectionEntity getEntity() {
        return entity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DefaultDocumentEntityPersist)) {
            return false;
        }
        DefaultDocumentEntityPersist that = (DefaultDocumentEntityPersist) o;
        return Objects.equals(entity, that.entity);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(entity);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("entity", entity)
                .toString();
    }
}
