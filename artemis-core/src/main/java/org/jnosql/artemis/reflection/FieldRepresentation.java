package org.jnosql.artemis.reflection;


import java.io.Serializable;
import java.lang.reflect.Field;

public interface FieldRepresentation extends Serializable {

    FieldType getType();

    Field getField();

    String getName();

    <T extends FieldRepresentation> T safeCast(FieldType type) throws IllegalStateException;
}
