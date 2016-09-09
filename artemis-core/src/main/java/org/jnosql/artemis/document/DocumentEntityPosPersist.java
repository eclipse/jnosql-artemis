package org.jnosql.artemis.document;


public interface DocumentEntityPosPersist {

    Object getValue();

    <T> T cast();
}
