package org.jnosql.artemis.reflection;


import java.io.Serializable;
import java.lang.reflect.Field;

public interface FieldRepresentation extends Serializable {

    FieldType getType();

    Field getField();

    String getName();

    <T extends FieldRepresentation> T cast(FieldType type) throws IllegalStateException;

    static FieldRepresentationBuilder builder() {
        return new FieldRepresentationBuilder();
    }
}
