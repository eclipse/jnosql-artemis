package org.jnosql.artemis;

import java.util.Objects;

public interface EntityPrePersist {

    Object getValue();

    <T> T cast();

    static EntityPrePersist of(Object value) throws NullPointerException {
        Objects.requireNonNull(value, "value is required");
        return new DefaultEntityPersist(value);
    }
}
