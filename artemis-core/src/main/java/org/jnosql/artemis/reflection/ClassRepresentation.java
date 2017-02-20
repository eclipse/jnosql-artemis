/*
 * Copyright 2017 Otavio Santana and others
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jnosql.artemis.reflection;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * This class is a representation of {@link Class} in cached way
 */
public class ClassRepresentation implements Serializable {

    private final String name;

    private final List<String> fieldsName;

    private final Class<?> classInstance;

    private final List<FieldRepresentation> fields;

    private final Constructor constructor;

    ClassRepresentation(String name, List<String> fieldsName, Class<?> classInstance, List<FieldRepresentation> fields, Constructor constructor) {
        this.name = name;
        this.fieldsName = fieldsName;
        this.classInstance = classInstance;
        this.fields = fields;
        this.constructor = constructor;
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

    public Constructor getConstructor() {
        return constructor;
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
                .append("constructor", constructor)
                .toString();
    }


    public Map<String, FieldRepresentation> getFieldsGroupByName() {
        return fields.stream()
                .collect(Collectors.toMap(FieldRepresentation::getName,
                        Function.identity()));
    }

    static ClassRepresentationBuilder builder() {
        return new ClassRepresentationBuilder();
    }

}
