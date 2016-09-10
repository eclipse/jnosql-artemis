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
import java.util.Objects;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.jnosql.diana.api.Value;

/**
 * The representation of {@link FieldRepresentation} to {@link java.util.Map}
 */
public class MapFieldRepresentation extends AbstractFieldRepresentation {

    private final Class valueClass;

    private final Class keyClass;

    MapFieldRepresentation(FieldType type, Field field, String name, Class valueClass, Class keyClass) {
        super(type, field, name);
        this.valueClass = valueClass;
        this.keyClass = keyClass;
    }

    public Class getValueClass() {
        return valueClass;
    }

    public Class getKeyClass() {
        return keyClass;
    }

    @Override
    public Object getValue(Value value, Reflections reflections) {
        return value.getMap(keyClass, valueClass);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MapFieldRepresentation that = (MapFieldRepresentation) o;
        return type == that.type &&
                Objects.equals(field, that.field) &&
                Objects.equals(valueClass, that.valueClass) &&
                Objects.equals(keyClass, that.keyClass) &&
                Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, field, name, valueClass, keyClass);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SIMPLE_STYLE)
                .append("type", type)
                .append("field", field)
                .append("name", name)
                .append("keyClass", keyClass)
                .append("valueClass", valueClass)
                .toString();
    }
}
