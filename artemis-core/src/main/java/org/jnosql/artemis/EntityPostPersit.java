package org.jnosql.artemis;


public interface EntityPostPersit {

    Object getValue();

    <T> T cast();
}
