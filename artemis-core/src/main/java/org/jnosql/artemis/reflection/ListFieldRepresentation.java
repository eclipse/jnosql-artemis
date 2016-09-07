package org.jnosql.artemis.reflection;


import java.lang.reflect.Field;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class ListFieldRepresentation extends DefaultFieldRepresentation {

    private final Class valueClass;

    ListFieldRepresentation(FieldType type, Field field, String name, Class valueClass) {
        super(type, field, name);
        this.valueClass = valueClass;
    }

    public Class getValueClass() {
        return valueClass;
    }

    @Override
    public <T extends FieldRepresentation> T cast(FieldType type) throws IllegalStateException {
        if (FieldType.LIST.equals(type)) {
            return (T) this;
        }
        throw new IllegalStateException("The ListFieldRepresentation just can convert to type List");
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
