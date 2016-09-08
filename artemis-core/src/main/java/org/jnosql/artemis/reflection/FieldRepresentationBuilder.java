package org.jnosql.artemis.reflection;

import java.lang.reflect.Field;

public class FieldRepresentationBuilder {

    private FieldType type;

    private Field field;

    private String name;

    private Class valueClass;

    private Class keyClass;

    public FieldRepresentationBuilder withType(FieldType type) {
        this.type = type;
        return this;
    }

    public FieldRepresentationBuilder withField(Field field) {
        this.field = field;
        return this;
    }

    public FieldRepresentationBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public FieldRepresentationBuilder withValueClass(Class valueClass) {
        this.valueClass = valueClass;
        return this;
    }

    public FieldRepresentationBuilder withKeyClass(Class keyClass) {
        this.keyClass = keyClass;
        return this;
    }

    public DefaultFieldRepresentation buildDefault() {
        return new DefaultFieldRepresentation(type, field, name);
    }

    public MapFieldRepresentation buildMap() {
        return new MapFieldRepresentation(type, field, name, valueClass, keyClass);
    }

    public CollectionFieldRepresentation buildCollection() {
        return new CollectionFieldRepresentation(type, field, name, valueClass);
    }
}
