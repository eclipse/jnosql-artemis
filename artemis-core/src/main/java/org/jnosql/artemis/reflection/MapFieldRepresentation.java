package org.jnosql.artemis.reflection;


import java.lang.reflect.Field;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class MapFieldRepresentation extends DefaultFieldRepresentation {

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
    public String toString() {
        return new ToStringBuilder(this)
                .append("type", type)
                .append("field", field)
                .append("name", name)
                .append("valueClass", valueClass)
                .toString();
    }
}
