package org.jnosql.artemis.reflection;


import java.lang.reflect.Field;
import java.util.Objects;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class DefaultFieldRepresentation extends AbstractFieldRepresentation {


    DefaultFieldRepresentation(FieldType type, Field field, String name) {
        super(type, field, name);
    }

    @Override
    public <T extends FieldRepresentation> T cast(FieldType type) throws IllegalStateException {
        if (FieldType.DEFAULT.equals(type)) {
            return (T) this;
        }
        throw new IllegalStateException("The DefaultFieldRepresentation just can convert to type DEFAULT");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AbstractFieldRepresentation that = (AbstractFieldRepresentation) o;
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
