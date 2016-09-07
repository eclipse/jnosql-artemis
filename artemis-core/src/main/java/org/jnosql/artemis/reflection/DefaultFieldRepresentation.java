package org.jnosql.artemis.reflection;


import java.io.Serializable;
import java.lang.reflect.Field;

public class DefaultFieldRepresentation implements Serializable {

    private FieldType type;

    private Field field;

    private String name;
}
