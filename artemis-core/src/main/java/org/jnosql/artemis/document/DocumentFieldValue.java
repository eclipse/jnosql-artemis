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
package org.jnosql.artemis.document;

import org.jnosql.artemis.AttributeConverter;
import org.jnosql.artemis.Converters;
import org.jnosql.artemis.column.ColumnEntityConverter;
import org.jnosql.artemis.reflection.FieldRepresentation;
import org.jnosql.artemis.reflection.FieldType;
import org.jnosql.artemis.reflection.FieldValue;
import org.jnosql.diana.api.column.Column;
import org.jnosql.diana.api.document.Document;

import java.util.Optional;

/**
 * Implementation of document
 */
class DocumentFieldValue implements FieldValue {

    private final FieldValue fieldValue;

    private DocumentFieldValue(FieldValue fieldValue) {
        this.fieldValue = fieldValue;
    }

    @Override
    public Object getValue() {
        return fieldValue.getValue();
    }

    @Override
    public FieldRepresentation getField() {
        return fieldValue.getField();
    }


    public Document toDocument(DocumentEntityConverter converter, Converters converters) {
        if (FieldType.EMBEDDED.equals(getField().getType())) {
            return Document.of(getField().getName(), converter.toDocument(getValue()).getDocuments());
        }
        Optional<Class<? extends AttributeConverter>> optionalConverter = getField().getConverter();
        if (optionalConverter.isPresent()) {
            AttributeConverter attributeConverter = converters.get(optionalConverter.get());
            return Document.of(getField().getName(), attributeConverter.convertToDatabaseColumn(getValue()));
        }
        return Document.of(getField().getName(), getValue());
    }

    @Override
    public boolean isNotEmpty() {
        return fieldValue.isNotEmpty();
    }

    @Override
    public Column toColumn(ColumnEntityConverter converter, Converters converters) {
        return null;
    }

    static DocumentFieldValue of(Object value, FieldRepresentation field) {
        return new DocumentFieldValue(FieldValue.of(value, field));
    }


    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("DocumentFieldValue{");
        sb.append("fieldValue=").append(fieldValue);
        sb.append('}');
        return sb.toString();
    }
}
