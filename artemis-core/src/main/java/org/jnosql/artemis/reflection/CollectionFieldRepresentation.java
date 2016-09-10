/*
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


import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.jnosql.diana.api.Value;

/**
 * Represents {@link FieldRepresentation} to field that implements {@link java.util.Collection}
 */
public class CollectionFieldRepresentation extends AbstractFieldRepresentation {

    private final Class valueClass;

    CollectionFieldRepresentation(FieldType type, Field field, String name, Class valueClass) {
        super(type, field, name);
        this.valueClass = valueClass;
    }

    public Class getValueClass() {
        return valueClass;
    }

    @Override
    public Object getValue(Value value, Reflections reflections) {
        Class<?> classType = field.getType();
        if (classType.equals(Set.class)) {
            return value.getSet(valueClass);
        }
        return value.getList(valueClass);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CollectionFieldRepresentation that = (CollectionFieldRepresentation) o;
        return type == that.type &&
                Objects.equals(field, that.field) &&
                Objects.equals(valueClass, that.valueClass) &&
                Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, field, name, valueClass);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SIMPLE_STYLE)
                .append("type", type)
                .append("field", field)
                .append("name", name)
                .append("valueClass", valueClass)
                .toString();
    }
}
