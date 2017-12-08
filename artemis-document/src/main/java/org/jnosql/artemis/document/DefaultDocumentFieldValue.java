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
import org.jnosql.artemis.reflection.FieldRepresentation;
import org.jnosql.artemis.reflection.FieldType;
import org.jnosql.artemis.reflection.FieldValue;
import org.jnosql.artemis.reflection.GenericFieldRepresentation;
import org.jnosql.diana.api.document.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.singletonList;
import static org.jnosql.artemis.reflection.FieldType.COLLECTION;

class DefaultDocumentFieldValue implements DocumentFieldValue {

    private final FieldValue fieldValue;

    private DefaultDocumentFieldValue(FieldValue fieldValue) {
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


    public List<Document> toDocument(DocumentEntityConverter converter, Converters converters) {
        if (FieldType.EMBEDDED.equals(getType())) {
            return singletonList(Document.of(getName(), converter.toDocument(getValue()).getDocuments()));
        } else if (COLLECTION.equals(getType()) && isEmbeddableElement()) {
            List<List<Document>> documents = new ArrayList<>();
            for (Object element : Iterable.class.cast(getValue())) {
                documents.add(converter.toDocument(element).getDocuments());
            }
            return singletonList(Document.of(getName(), documents));
        }
        Optional<Class<? extends AttributeConverter>> optionalConverter = getField().getConverter();
        if (optionalConverter.isPresent()) {
            AttributeConverter attributeConverter = converters.get(optionalConverter.get());
            return singletonList(Document.of(getName(), attributeConverter.convertToDatabaseColumn(getValue())));
        }
        return singletonList(Document.of(getName(), getValue()));
    }

    @Override
    public boolean isNotEmpty() {
        return fieldValue.isNotEmpty();
    }

    private FieldType getType() {
        return getField().getType();
    }

    private boolean isEmbeddableElement() {
        return GenericFieldRepresentation.class.cast(getField()).isEmbeddable();
    }

    private String getName() {
        return getField().getName();
    }

    static DocumentFieldValue of(Object value, FieldRepresentation field) {
        return new DefaultDocumentFieldValue(FieldValue.of(value, field));
    }


    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("DocumentFieldValue{");
        sb.append("fieldValue=").append(fieldValue);
        sb.append('}');
        return sb.toString();
    }
}
