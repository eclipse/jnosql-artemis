/*
 * Copyright 2017 Otavio Santana and others
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
package org.jnosql.artemis.document;

import org.jnosql.artemis.reflection.ClassRepresentation;
import org.jnosql.artemis.reflection.ClassRepresentations;
import org.jnosql.artemis.reflection.FieldRepresentation;
import org.jnosql.artemis.reflection.FieldValue;
import org.jnosql.artemis.reflection.Reflections;
import org.jnosql.diana.api.Value;
import org.jnosql.diana.api.document.Document;
import org.jnosql.diana.api.document.DocumentEntity;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static org.jnosql.artemis.reflection.FieldType.EMBEDDED;

/**
 * The default implementation of {@link DocumentEntityConverter}
 */
@ApplicationScoped
class DefaultDocumentEntityConverter implements DocumentEntityConverter {

    @Inject
    private ClassRepresentations classRepresentations;

    @Inject
    private Reflections reflections;


    @Override
    public DocumentEntity toDocument(Object entityInstance) {
        Objects.requireNonNull(entityInstance, "Object is required");
        ClassRepresentation representation = classRepresentations.get(entityInstance.getClass());
        DocumentEntity entity = DocumentEntity.of(representation.getName());
        representation.getFields().stream()
                .map(f -> to(f, entityInstance))
                .filter(FieldValue::isNotEmpty)
                .map(f -> f.toDocument(this))
                .forEach(entity::add);
        return entity;

    }

    @Override
    public <T> T toEntity(Class<T> entityClass, DocumentEntity entity) {
        ClassRepresentation representation = classRepresentations.get(entityClass);
        T instance = reflections.newInstance(entityClass);
        return convertEntity(entity, representation, instance);


    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T toEntity(DocumentEntity entity) {
        ClassRepresentation representation = classRepresentations.findByName(entity.getName());
        T instance = reflections.newInstance((Class<T>) representation.getClassInstance());
        return convertEntity(entity, representation, instance);
    }

    private <T> T convertEntity(DocumentEntity entity, ClassRepresentation representation, T instance) {
        Map<String, FieldRepresentation> fieldsGroupByName = representation.getFieldsGroupByName();
        Predicate<String> existField = k -> entity.find(k).isPresent();

        fieldsGroupByName.keySet().stream()
                .filter(existField.or(k -> EMBEDDED.equals(fieldsGroupByName.get(k).getType())))
                .forEach(feedObject(instance, entity, fieldsGroupByName));

        return instance;
    }

    private <T> Consumer<String> feedObject(T instance, DocumentEntity entity, Map<String, FieldRepresentation> fieldsGroupByName) {
        return k -> {
            Optional<Document> document = entity.find(k);

            FieldRepresentation field = fieldsGroupByName.get(k);
            if (EMBEDDED.equals(field.getType())) {
                setEmbeddedField(instance, entity, document, field);
            } else {
                setSingleField(instance, document, field);
            }
        };
    }

    private <T> void setSingleField(T instance, Optional<Document> document, FieldRepresentation field) {
        Value value = document.get().getValue();
        reflections.setValue(instance, field.getField(), field.getValue(value));
    }

    private <T> void setEmbeddedField(T instance, DocumentEntity entity, Optional<Document> document, FieldRepresentation field) {
        if (document.isPresent()) {
            Value value = document.get().getValue();
            DocumentEntity columnEntity = value.get(DocumentEntity.class);
            reflections.setValue(instance, field.getField(), toEntity(columnEntity));
        } else {
            reflections.setValue(instance, field.getField(), toEntity(field.getField().getType(), entity));
        }
    }

    private FieldValue to(FieldRepresentation field, Object entityInstance) {
        Object value = reflections.getValue(entityInstance, field.getField());
        return new FieldValue(value, field);
    }

}
