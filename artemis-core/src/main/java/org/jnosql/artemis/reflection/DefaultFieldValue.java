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


import org.jnosql.artemis.AttributeConverter;
import org.jnosql.artemis.Converters;
import org.jnosql.artemis.column.ColumnEntityConverter;
import org.jnosql.artemis.document.DocumentEntityConverter;
import org.jnosql.diana.api.column.Column;
import org.jnosql.diana.api.document.Document;

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
    public Document toDocument(DocumentEntityConverter converter, Converters converters) {
        if (FieldType.EMBEDDED.equals(field.getType())) {
            return Document.of(field.getName(), converter.toDocument(value).getDocuments());
        }
        Optional<Class<? extends AttributeConverter>> optionalConverter = field.getConverter();
        if (optionalConverter.isPresent()) {
            AttributeConverter attributeConverter = converters.get(optionalConverter.get());
            return Document.of(field.getName(), attributeConverter.convertToDatabaseColumn(value));
        }
        return Document.of(field.getName(), value);
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
