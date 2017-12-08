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
import org.jnosql.artemis.reflection.FieldRepresentation;
import org.jnosql.artemis.reflection.GenericFieldRepresentation;
import org.jnosql.diana.api.TypeReference;
import org.jnosql.diana.api.Value;
import org.jnosql.diana.api.document.Document;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import static org.jnosql.artemis.reflection.FieldType.COLLECTION;
import static org.jnosql.artemis.reflection.FieldType.EMBEDDED;
import static org.jnosql.artemis.reflection.FieldType.SUBENTITY;

class DocumentFieldConverters {

    static class DocumentFieldConverterFactory {

        private final EmbeddedFieldConverter embeddedFieldConverter = new EmbeddedFieldConverter();
        private final DefaultConverter defaultConverter = new DefaultConverter();
        private final CollectionEmbeddableConverter embeddableConverter = new CollectionEmbeddableConverter();
        private final SubEntityConverter subEntityConverter = new SubEntityConverter();

        DocumentFieldConverter get(FieldRepresentation field) {
            if (EMBEDDED.equals(field.getType())) {
                return embeddedFieldConverter;
            } else if (SUBENTITY.equals(field.getType())) {
                return subEntityConverter;
            } else if (isCollectionEmbeddable(field)) {
                return embeddableConverter;
            } else {
                return defaultConverter;
            }
        }

        private boolean isCollectionEmbeddable(FieldRepresentation field) {
            return COLLECTION.equals(field.getType()) && GenericFieldRepresentation.class.cast(field).isEmbeddable();
        }
    }

    private static class EmbeddedFieldConverter implements DocumentFieldConverter {

        @Override
        public <T> void convert(T instance, List<Document> documents, Optional<Document> document,
                                FieldRepresentation field, AbstractDocumentEntityConverter converter) {

            if (document.isPresent()) {
                Document sudDocument = document.get();
                Object value = sudDocument.get();
                if (Map.class.isInstance(value)) {
                    Map map = Map.class.cast(value);
                    List<Document> embeddedDocument = new ArrayList<>();
                    for (Object key : map.keySet()) {
                        embeddedDocument.add(Document.of(key.toString(), map.get(key)));
                    }
                    converter.getReflections().setValue(instance, field.getNativeField(), converter.toEntity(field.getNativeField().getType(), embeddedDocument));
                } else {
                    converter.getReflections().setValue(instance, field.getNativeField(), converter.toEntity(field.getNativeField().getType(),
                            sudDocument.get(new TypeReference<List<Document>>() {
                            })));
                }

            } else {
                converter.getReflections().setValue(instance, field.getNativeField(), converter.toEntity(field.getNativeField().getType(), documents));
            }
        }
    }

    private static class DefaultConverter implements DocumentFieldConverter {

        @Override
        public <T> void convert(T instance, List<Document> documents, Optional<Document> document,
                                FieldRepresentation field, AbstractDocumentEntityConverter converter) {
            Value value = document.get().getValue();

            Optional<Class<? extends AttributeConverter>> optionalConverter = field.getConverter();
            if (optionalConverter.isPresent()) {
                AttributeConverter attributeConverter = converter.getConverters().get(optionalConverter.get());
                Object attributeConverted = attributeConverter.convertToEntityAttribute(value.get());
                converter.getReflections().setValue(instance, field.getNativeField(), field.getValue(Value.of(attributeConverted)));
            } else {
                converter.getReflections().setValue(instance, field.getNativeField(), field.getValue(value));
            }
        }
    }

    private static class CollectionEmbeddableConverter implements DocumentFieldConverter {

        @Override
        public <T> void convert(T instance, List<Document> documents, Optional<Document> document,
                                FieldRepresentation field, AbstractDocumentEntityConverter converter) {
            document.ifPresent(convertDocument(instance, field, converter));
        }

        private <T> Consumer<Document> convertDocument(T instance, FieldRepresentation field, AbstractDocumentEntityConverter converter) {
            return document -> {
                GenericFieldRepresentation genericField = GenericFieldRepresentation.class.cast(field);
                Collection collection = genericField.getCollectionInstance();
                List<List<Document>> embeddable = (List<List<Document>>) document.get();
                for (List<Document> documentList : embeddable) {
                    Object element = converter.toEntity(genericField.getElementType(), documentList);
                    collection.add(element);
                }
                converter.getReflections().setValue(instance, field.getNativeField(), collection);
            };
        }
    }

    private static class SubEntityConverter implements DocumentFieldConverter {


        @Override
        public <T> void convert(T instance, List<Document> documents, Optional<Document> document,
                                FieldRepresentation field, AbstractDocumentEntityConverter converter) {

            Field nativeField = field.getNativeField();
            Object subEntity = converter.toEntity(nativeField.getType(), documents);
            converter.getReflections().setValue(instance, nativeField, subEntity);

        }
    }
}
