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


import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;

class DefaultClassRepresentation implements ClassRepresentation {


    private final String name;

    private final List<String> fieldsName;

    private final Class<?> classInstance;

    private final List<FieldRepresentation> fields;

    private final InstanceSupplier instanceSupplier;

    private final Map<String, NativeMapping> javaFieldGroupedByColumn;

    private final Map<String, FieldRepresentation> fieldsGroupedByName;

    private final FieldRepresentation id;

    DefaultClassRepresentation(String name, List<String> fieldsName, Class<?> classInstance,
                               List<FieldRepresentation> fields,
                               Map<String, NativeMapping> javaFieldGroupedByColumn,
                               Map<String, FieldRepresentation> fieldsGroupedByName, InstanceSupplier instanceSupplier) {
        this.name = name;
        this.fieldsName = fieldsName;
        this.classInstance = classInstance;
        this.fields = fields;
        this.fieldsGroupedByName = fieldsGroupedByName;
        this.javaFieldGroupedByColumn = javaFieldGroupedByColumn;
        this.instanceSupplier = instanceSupplier;
        this.id = fields.stream().filter(FieldRepresentation::isId).findFirst().orElse(null);
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
    public <T> T newInstance() {
        return (T) instanceSupplier.get();
    }

    @Override
    public String getColumnField(String javaField) {
        requireNonNull(javaField, "javaField is required");
        return ofNullable(javaFieldGroupedByColumn.get(javaField))
                .map(NativeMapping::getNativeField).orElse(javaField);

    }

    @Override
    public Optional<FieldRepresentation> getFieldRepresentation(String javaField) {
        requireNonNull(javaField, "javaField is required");
        return ofNullable(javaFieldGroupedByColumn.get(javaField))
                .map(NativeMapping::getFieldRepresentation);
    }

    @Override
    public Map<String, FieldRepresentation> getFieldsGroupByName() {
        return fieldsGroupedByName;
    }

    @Override
    public Optional<FieldRepresentation> getId() {
        return Optional.ofNullable(id);
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
        final StringBuilder sb = new StringBuilder("DefaultClassRepresentation{");
        sb.append("name='").append(name).append('\'');
        sb.append(", fieldsName=").append(fieldsName);
        sb.append(", classInstance=").append(classInstance);
        sb.append(", fields=").append(fields);
        sb.append(", javaFieldGroupedByColumn=").append(javaFieldGroupedByColumn);
        sb.append(", fieldsGroupedByName=").append(fieldsGroupedByName);
        sb.append(", id=").append(id);
        sb.append('}');
        return sb.toString();
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
