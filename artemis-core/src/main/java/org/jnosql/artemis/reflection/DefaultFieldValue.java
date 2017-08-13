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


import org.jnosql.artemis.AttributeConverter;
import org.jnosql.artemis.Converters;
import org.jnosql.artemis.column.ColumnEntityConverter;
import org.jnosql.diana.api.column.Column;

import java.util.Objects;
import java.util.Optional;

final class DefaultFieldValue implements FieldValue {

    private final Object value;

    private final FieldRepresentation field;


    DefaultFieldValue(Object value, FieldRepresentation field) {
        this.value = value;
        this.field = Objects.requireNonNull(field, "field is required");
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public FieldRepresentation getField() {
        return field;
    }

    @Override
    public boolean isNotEmpty() {
        return value != null;
    }


    @Override
    public Column toColumn(ColumnEntityConverter converter, Converters converters) {

        if (FieldType.EMBEDDED.equals(getField().getType())) {
            return Column.of(field.getName(), converter.toColumn(value).getColumns());
        }
        Optional<Class<? extends AttributeConverter>> optionalConverter = field.getConverter();
        if (optionalConverter.isPresent()) {
            AttributeConverter attributeConverter = converters.get(optionalConverter.get());
            return Column.of(field.getName(), attributeConverter.convertToDatabaseColumn(value));
        }

        return Column.of(field.getName(), value);
    }

    @Override
    public String toString() {
        return "FieldValue{" +
                "value=" + value +
                ", field=" + field +
                '}';
    }
}
