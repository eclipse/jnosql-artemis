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

import org.jnosql.artemis.Converters;
import org.jnosql.artemis.document.DocumentFieldConverters.DocumentFieldConverterFactory;
import org.jnosql.artemis.reflection.ClassRepresentation;
import org.jnosql.artemis.reflection.ClassRepresentations;
import org.jnosql.artemis.reflection.FieldRepresentation;
import org.jnosql.artemis.reflection.FieldType;
import org.jnosql.artemis.reflection.FieldValue;
import org.jnosql.artemis.reflection.Reflections;
import org.jnosql.diana.api.document.Document;
import org.jnosql.diana.api.document.DocumentEntity;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;
import static org.jnosql.artemis.reflection.FieldType.EMBEDDED;
import static org.jnosql.artemis.reflection.FieldType.SUBENTITY;

/**
 * Template method to {@link DocumentEntityConverter}
 */
public abstract class AbstractDocumentEntityConverter implements DocumentEntityConverter {

    protected abstract ClassRepresentations getClassRepresentations();

    protected abstract Reflections getReflections();

    protected abstract Converters getConverters();

    private final DocumentFieldConverterFactory converterFactory = new DocumentFieldConverterFactory();


    @Override
    public DocumentEntity toDocument(Object entityInstance) {
        requireNonNull(entityInstance, "Object is required");
        ClassRepresentation representation = getClassRepresentations().get(entityInstance.getClass());
        DocumentEntity entity = DocumentEntity.of(representation.getName());
        representation.getFields().stream()
                .map(f -> to(f, entityInstance))
                .filter(FieldValue::isNotEmpty)
                .map(f -> f.toDocument(this, getConverters()))
                .flatMap(List::stream)
                .forEach(entity::add);
        return entity;

    }

    @Override
    public <T> T toEntity(Class<T> entityClass, DocumentEntity entity) {
        requireNonNull(entity, "entity is required");
        requireNonNull(entityClass, "entityClass is required");
        return toEntity(entityClass, entity.getDocuments());

    }

    protected <T> T toEntity(Class<T> entityClass, List<Document> documents) {
        ClassRepresentation representation = getClassRepresentations().get(entityClass);
        T instance = getReflections().newInstance(representation.getConstructor());
        return convertEntity(documents, representation, instance);
    }


    @SuppressWarnings("unchecked")
    @Override
    public <T> T toEntity(DocumentEntity entity) {
        requireNonNull(entity, "entity is required");
        ClassRepresentation representation = getClassRepresentations().findByName(entity.getName());
        T instance = getReflections().newInstance(representation.getConstructor());
        return convertEntity(entity.getDocuments(), representation, instance);
    }

    private <T> T convertEntity(List<Document> documents, ClassRepresentation representation, T instance) {
        final Map<String, FieldRepresentation> fieldsGroupByName = representation.getFieldsGroupByName();
        final List<String> names = documents.stream().map(Document::getName).sorted().collect(Collectors.toList());
        final Predicate<String> existField = k -> Collections.binarySearch(names, k) >= 0;
        final Predicate<String> isElementType = k -> {
            FieldType type = fieldsGroupByName.get(k).getType();
            return EMBEDDED.equals(type) || SUBENTITY.equals(type);
        };

        fieldsGroupByName.keySet().stream()
                .filter(existField.or(isElementType))
                .forEach(feedObject(instance, documents, fieldsGroupByName));

        return instance;
    }

    protected <T> Consumer<String> feedObject(T instance, List<Document> documents, Map<String, FieldRepresentation> fieldsGroupByName) {
        return k -> {
            Optional<Document> document = documents.stream().filter(c -> c.getName().equals(k)).findFirst();

            FieldRepresentation field = fieldsGroupByName.get(k);
            DocumentFieldConverter fieldConverter = converterFactory.get(field);
            fieldConverter.convert(instance, documents, document, field, this);
        };
    }


    private DocumentFieldValue to(FieldRepresentation field, Object entityInstance) {
        Object value = getReflections().getValue(entityInstance, field.getNativeField());
        return DefaultDocumentFieldValue.of(value, field);
    }


}
