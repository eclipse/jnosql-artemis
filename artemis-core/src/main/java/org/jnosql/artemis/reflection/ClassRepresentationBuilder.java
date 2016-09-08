package org.jnosql.artemis.reflection;

import java.util.Collections;
import java.util.List;

class ClassRepresentationBuilder {

    private String name;

    private List<String> fieldsName = Collections.emptyList();

    private Class<?> classInstance;

    private List<FieldRepresentation> fields = Collections.emptyList();

    public ClassRepresentationBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public ClassRepresentationBuilder withFieldsName(List<String> fieldsName) {
        this.fieldsName = fieldsName;
        return this;
    }

    public ClassRepresentationBuilder withClassInstance(Class<?> classInstance) {
        this.classInstance = classInstance;
        return this;
    }

    public ClassRepresentationBuilder withFields(List<FieldRepresentation> fields) {
        this.fields = fields;
        return this;
    }

    public ClassRepresentation build() {
        return new ClassRepresentation(name, fieldsName, classInstance, fields);
    }
}