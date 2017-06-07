/*
 *  Copyright (c) 2017 Otávio Santana and others
 *   All rights reserved. This program and the accompanying materials
 *   are made available under the terms of the Eclipse Public License v1.0
 *   and Apache License v2.0 which accompanies this distribution.
 *   The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 *   and the Apache License v2.0 is available at http://www.opensource.org/licenses/apache2.0.php.
 *
 *   You may elect to redistribute this code under either of these licenses.
 *
 *   Contributors:
 *
 *   Otavio Santana
 */
package org.jnosql.artemis.reflection;


import org.apache.commons.lang3.builder.ToStringBuilder;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toMap;

class DefaultClassRepresentation implements ClassRepresentation {


    private final String name;

    private final List<String> fieldsName;

    private final Class<?> classInstance;

    private final List<FieldRepresentation> fields;

    private final Constructor constructor;

    private final Map<String, String> javaFieldGroupedByColumn;

    private final Map<String, FieldRepresentation> fieldsGroupedByName;

    private final Optional<FieldRepresentation> id;

    DefaultClassRepresentation(String name, List<String> fieldsName, Class<?> classInstance,
                               List<FieldRepresentation> fields, Constructor constructor) {
        this.name = name;
        this.fieldsName = fieldsName;
        this.classInstance = classInstance;
        this.fields = fields;
        this.constructor = constructor;
        this.fieldsGroupedByName = fields.stream()
                .collect(toMap(FieldRepresentation::getName,
                        Function.identity()));
        this.javaFieldGroupedByColumn = fields.stream()
                .collect(toMap(FieldRepresentation::getFieldName,
                        FieldRepresentation::getName));
        this.id = fields.stream().filter(FieldRepresentation::isId).findFirst();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<String> getFieldsName() {
        return fieldsName;
    }

    @Override
    public Class<?> getClassInstance() {
        return classInstance;
    }

    @Override
    public List<FieldRepresentation> getFields() {
        return fields;
    }

    @Override
    public Constructor getConstructor() {
        return constructor;
    }


    @Override
    public String getColumnField(String javaField) throws NullPointerException {
        requireNonNull(javaField, "javaField is required");
        return javaFieldGroupedByColumn.getOrDefault(javaField, javaField);

    }

    @Override
    public Map<String, FieldRepresentation> getFieldsGroupByName() {
        return fieldsGroupedByName;
    }

    @Override
    public Optional<FieldRepresentation> getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DefaultClassRepresentation)) {
            return false;
        }
        DefaultClassRepresentation that = (DefaultClassRepresentation) o;
        return Objects.equals(classInstance, that.classInstance);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(classInstance);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("name", name)
                .append("fieldsName", fieldsName)
                .append("classInstance", classInstance)
                .append("fields", fields)
                .append("id", id)
                .append("constructor", constructor)
                .toString();
    }

    /**
     * Creates a builder
     *
     * @return {@link ClassRepresentationBuilder}
     */
    static ClassRepresentationBuilder builder() {
        return new ClassRepresentationBuilder();
    }


}
