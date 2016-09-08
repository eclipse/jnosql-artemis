package org.jnosql.artemis.reflection;

import java.lang.reflect.Field;

public class FieldRepresentationBuilder {

    private FieldType type;

    private Field field;

    private String name;

    private Class valueClass;

    private Class keyClass;

    public FieldRepresentationBuilder setType(FieldType type) {
        this.type = type;
        return this;
    }

    public FieldRepresentationBuilder setField(Field field) {
        this.field = field;
        return this;
    }

    public FieldRepresentationBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public FieldRepresentationBuilder setValueClass(Class valueClass) {
        this.valueClass = valueClass;
        return this;
    }

    public FieldRepresentationBuilder setKeyClass(Class keyClass) {
        this.keyClass = keyClass;
        return this;
    }

    public DefaultFieldRepresentation buildDefault() {
        return new DefaultFieldRepresentation(type, field, name);
    }

    public ListFieldRepresentation buildList() {
        return new ListFieldRepresentation(type, field, name, valueClass);
    }

    public SetFieldRepresentation buildSet() {
        return new SetFieldRepresentation(type, field, name, valueClass);
    }

    public MapFieldRepresentation buildMap() {
        return new MapFieldRepresentation(type, field, name, valueClass, keyClass);
    }
}
