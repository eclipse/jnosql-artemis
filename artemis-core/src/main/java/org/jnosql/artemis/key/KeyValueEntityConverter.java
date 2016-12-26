package org.jnosql.artemis.key;


import org.jnosql.artemis.document.DocumentEntityConverter;
import org.jnosql.diana.api.document.DocumentEntity;
import org.jnosql.diana.api.key.KeyValueEntity;

/**
 * This interface represents the converter between an entity and the {@link KeyValueEntity}
 */
public interface KeyValueEntityConverter {

    /**
     * Converts the instance entity to {@link DocumentEntity}
     *
     * @param entityInstance the instnace
     * @return a {@link DocumentEntity} instance
     */
    KeyValueEntity<?> toKeyValue(Object entityInstance);

    /**
     * Converts a {@link KeyValueEntity} to entity
     *
     * @param entityClass the entity class
     * @param entity      the {@link KeyValueEntity} to be converted
     * @param <T>         the entity type
     * @return the instance from {@link KeyValueEntity}
     */
    <T> T toEntity(Class<T> entityClass, KeyValueEntity<?> entity);

    /**
     * Similar to {@link DocumentEntityConverter#toEntity(Class, DocumentEntity)}, but
     * search the instance by bucket name.
     *
     * @param entity the {@link DocumentEntity} to be converted
     * @param <T>    the entity type
     * @return the instance from {@link DocumentEntity}
     */
    <T> T toEntity(KeyValueEntity<?> entity);
}
