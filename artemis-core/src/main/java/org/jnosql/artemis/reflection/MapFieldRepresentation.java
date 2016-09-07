package org.jnosql.artemis.reflection;


import java.lang.reflect.Field;
import java.util.Objects;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class MapFieldRepresentation extends AbstractFieldRepresentation {

    private final Class valueClass;

    private final Class keyClass;

    MapFieldRepresentation(FieldType type, Field field, String name, Class valueClass, Class keyClass) {
        super(type, field, name);
        this.valueClass = valueClass;
        this.keyClass = keyClass;
    }

    public Class getValueClass() {
        return valueClass;
    }

    public Class getKeyClass() {
        return keyClass;
    }

    @Override
    public <T extends FieldRepresentation> T cast(FieldType type) throws IllegalStateException {
        if (FieldType.MAP.equals(type)) {
            return (T) this;
        }
        throw new IllegalStateException("The MapFieldRepresentation just can convert to type Map");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MapFieldRepresentation that = (MapFieldRepresentation) o;
        return type == that.type &&
                Objects.equals(field, that.field) &&
                Objects.equals(valueClass, that.valueClass) &&
                Objects.equals(keyClass, that.keyClass) &&
                Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, field, name, valueClass, keyClass);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("type", type)
                .append("field", field)
                .append("name", name)
                .append("keyClass", keyClass)
                .append("valueClass", valueClass)
                .toString();
    }
}
