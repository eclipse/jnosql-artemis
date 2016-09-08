package org.jnosql.artemis.reflection;


import java.io.Serializable;
import java.lang.reflect.Field;
import org.jnosql.diana.api.Value;

public interface FieldRepresentation extends Serializable {

    FieldType getType();

    Field getField();

    String getName();

    <T extends FieldRepresentation> T cast(FieldType type) throws IllegalStateException;

    Object getValue(Value value, Reflections reflections);

    static FieldRepresentationBuilder builder() {
        return new FieldRepresentationBuilder();
    }

}
