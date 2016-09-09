package org.jnosql.artemis;

public interface EntityPrePersist {

    Object getValue();

    <T> T cast();
}
