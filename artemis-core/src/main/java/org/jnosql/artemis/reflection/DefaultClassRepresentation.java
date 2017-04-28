/*
 * Copyright 2017 Otavio Santana and others
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jnosql.artemis.reflection;


import org.apache.commons.lang3.builder.ToStringBuilder;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toMap;

class DefaultClassRepresentation implements ClassRepresentation{


    private final String name;

    private final List<String> fieldsName;

    private final Class<?> classInstance;

    private final List<FieldRepresentation> fields;

    private final Constructor constructor;

    private final Map<String, String> javaFieldGroupedByColumn;

    private final Map<String, FieldRepresentation> fieldsGroupedByName;

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
                .append("constructor", constructor)
                .toString();
    }

    /**
     * Creates a builder
     * @return {@link ClassRepresentationBuilder}
     */
    static ClassRepresentationBuilder builder() {
        return new ClassRepresentationBuilder();
    }


}
