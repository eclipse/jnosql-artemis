package org.jnosql.artemis.reflection;


import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Objects;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class DefaultFieldRepresentation implements FieldRepresentation {

    private final FieldType type;

    private final Field field;

    private final String name;

    DefaultFieldRepresentation(FieldType type, Field field, String name) {
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
    public <T extends FieldRepresentation> T cast(FieldType type) throws IllegalStateException {
        if (FieldType.DEFAULT.equals(type)) {
            return (T) this;
        }
        throw new IllegalStateException("The DefaulFieldRepresentation just can convert to type Default");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DefaultFieldRepresentation that = (DefaultFieldRepresentation) o;
        return type == that.type &&
                Objects.equals(field, that.field) &&
                Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, field, name);
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
