package org.jnosql.artemis;


import java.util.Objects;

public interface EntityPostPersit {

    Object getValue();

    <T> T cast();

    static EntityPostPersit of(Object value) throws NullPointerException {
        Objects.requireNonNull(value, "value is required");
        return new DefaultEntityPersist(value);
    }

}
