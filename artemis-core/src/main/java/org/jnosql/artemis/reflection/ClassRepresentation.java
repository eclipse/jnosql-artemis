package org.jnosql.artemis.reflection;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class ClassRepresentation implements Serializable {

    private final String name;

    private final List<String> fieldsName;

    private final Class<?> classInstance;

    private final List<FieldRepresentation> fields;

    private final boolean indexed;

    ClassRepresentation(String name, List<String> fieldsName, Class<?> classInstance, List<FieldRepresentation> fields, boolean indexed) {
        this.name = name;
        this.fieldsName = fieldsName;
        this.classInstance = classInstance;
        this.fields = fields;
        this.indexed = indexed;
    }

    public String getName() {
        return name;
    }

    public List<String> getFieldsName() {
        return fieldsName;
    }

    public Class<?> getClassInstance() {
        return classInstance;
    }

    public List<FieldRepresentation> getFields() {
        return fields;
    }

    public boolean isIndexed() {
        return indexed;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ClassRepresentation that = (ClassRepresentation) o;
        return indexed == that.indexed &&
                Objects.equals(name, that.name) &&
                Objects.equals(fieldsName, that.fieldsName) &&
                Objects.equals(classInstance, that.classInstance) &&
                Objects.equals(fields, that.fields);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, fieldsName, classInstance, fields, indexed);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("name", name)
                .append("fieldsName", fieldsName)
                .append("classInstance", classInstance)
                .append("fields", fields)
                .append("indexed", indexed)
                .toString();
    }
}
