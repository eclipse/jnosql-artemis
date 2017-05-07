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
import org.apache.commons.lang3.builder.ToStringStyle;
import org.jnosql.artemis.AttributeConverter;
import org.jnosql.diana.api.TypeSupplier;
import org.jnosql.diana.api.Value;

import java.lang.reflect.Field;
import java.util.Objects;

public class GenericFieldRepresentation extends AbstractFieldRepresentation {

    private final TypeSupplier<?> typeSupplier;

    GenericFieldRepresentation(FieldType type, Field field, String name, TypeSupplier<?> typeSupplier, Class<? extends AttributeConverter> converter) {
        super(type, field, name, converter);
        this.typeSupplier = typeSupplier;
    }

    @Override
    public Object getValue(Value value) {
        return value.get(typeSupplier);
    }

    @Override
    public boolean isId() {
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GenericFieldRepresentation that = (GenericFieldRepresentation) o;
        return type == that.type &&
                Objects.equals(field, that.field) &&
                Objects.equals(typeSupplier, that.typeSupplier) &&
                Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, field, name, typeSupplier);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SIMPLE_STYLE)
                .append("type", type)
                .append("field", field)
                .append("name", name)
                .append("typeSupplier", typeSupplier)
                .toString();
    }
}
