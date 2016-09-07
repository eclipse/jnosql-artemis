package org.jnosql.artemis.reflection;

import java.lang.reflect.Field;
import org.apache.commons.lang3.builder.ToStringBuilder;

abstract class AbstractFieldRepresentation implements FieldRepresentation {

    protected final FieldType type;

    protected final Field field;

    protected final String name;

    AbstractFieldRepresentation(FieldType type, Field field, String name) {
        this.type = type;
        this.field = field;
        this.name = name;
    }

    @Override
    public FieldType getType() {
        return type;
    }

    @Override
    public Field getField() {
        return field;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("type", type)
                .append("field", field)
                .append("name", name)
                .toString();
    }
}
