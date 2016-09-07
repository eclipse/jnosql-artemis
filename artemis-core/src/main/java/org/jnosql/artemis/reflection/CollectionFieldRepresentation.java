package org.jnosql.artemis.reflection;


import java.lang.reflect.Field;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class CollectionFieldRepresentation extends DefaultFieldRepresentation {

    private final Class valueClass;

    CollectionFieldRepresentation(FieldType type, Field field, String name, Class valueClass) {
        super(type, field, name);
        this.valueClass = valueClass;
    }

    public Class getValueClass() {
        return valueClass;
    }

    @Override
    public <T extends FieldRepresentation> T cast(FieldType type) throws IllegalStateException {
        if (FieldType.COLLECTION.equals(type)) {
            return (T) this;
        }
        throw new IllegalStateException("The CollectionFieldRepresentation just can convert to type Collection");
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
