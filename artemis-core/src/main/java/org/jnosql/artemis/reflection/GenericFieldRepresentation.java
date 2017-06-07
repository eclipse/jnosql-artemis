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
