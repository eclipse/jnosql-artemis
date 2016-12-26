package org.jnosql.artemis.key;


import org.jnosql.diana.api.key.KeyValueEntity;

public interface KeyValueEntityPostPersist {

    /**
     * The {@link org.jnosql.diana.api.key.KeyValueEntity}  after be saved
     *
     * @return the {@link KeyValueEntity} instance
     */
    KeyValueEntity<?> getEntity();
}
