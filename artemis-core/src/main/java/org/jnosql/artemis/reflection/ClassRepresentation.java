package org.jnosql.artemis.reflection;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class ClassRepresentation implements Serializable {

    private final String name;

    private final List<String> fieldsName;

    private final Class<?> classInstance;

    private final List<FieldRepresentation> fields;

    ClassRepresentation(String name, List<String> fieldsName, Class<?> classInstance, List<FieldRepresentation> fields) {
        this.name = name;
        this.fieldsName = fieldsName;
        this.classInstance = classInstance;
        this.fields = fields;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ClassRepresentation that = (ClassRepresentation) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(fieldsName, that.fieldsName) &&
                Objects.equals(classInstance, that.classInstance) &&
                Objects.equals(fields, that.fields);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, fieldsName, classInstance, fields);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("name", name)
                .append("fieldsName", fieldsName)
                .append("classInstance", classInstance)
                .append("fields", fields)
                .toString();
    }


    public Map<String, FieldRepresentation> getFieldsGroupByName() {
        return fields.stream()
                .collect(Collectors.toMap(FieldRepresentation::getName,
                        Function.identity()));
    }

    public static ClassRepresentationBuilder builder() {
        return new ClassRepresentationBuilder();
    }

}
