package org.jnosql.artemis.key;


import org.jnosql.diana.api.key.KeyValueEntity;

import java.util.Objects;

public interface KeyValueEntityPostPersist {

    /**
     * The {@link org.jnosql.diana.api.key.KeyValueEntity}  after be saved
     *
     * @return the {@link KeyValueEntity} instance
     */
    KeyValueEntity<?> getEntity();

    /**
     * Creates the {@link KeyValueEntityPostPersist} instance
     *
     * @param entity the entity
     * @return {@link KeyValueEntityPostPersist} instance
     * @throws NullPointerException when the entity is null
     */
    static <T> KeyValueEntityPostPersist of(KeyValueEntity<T> entity) throws NullPointerException {
        Objects.requireNonNull(entity, "Entity is required");
        return new DefaultKeyValuePersist(entity);
    }
}
