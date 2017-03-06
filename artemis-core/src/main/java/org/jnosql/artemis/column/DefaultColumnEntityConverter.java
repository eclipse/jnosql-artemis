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
package org.jnosql.artemis.column;


import org.jnosql.artemis.AttributeConverter;
import org.jnosql.artemis.Converters;
import org.jnosql.artemis.reflection.ClassRepresentation;
import org.jnosql.artemis.reflection.ClassRepresentations;
import org.jnosql.artemis.reflection.FieldRepresentation;
import org.jnosql.artemis.reflection.FieldValue;
import org.jnosql.artemis.reflection.Reflections;
import org.jnosql.diana.api.TypeReference;
import org.jnosql.diana.api.Value;
import org.jnosql.diana.api.column.Column;
import org.jnosql.diana.api.column.ColumnEntity;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.jnosql.artemis.reflection.FieldType.EMBEDDED;

/**
 * The default implementation to {@link ColumnEntityConverter}
 */
@ApplicationScoped
class DefaultColumnEntityConverter implements ColumnEntityConverter {

    @Inject
    private ClassRepresentations classRepresentations;

    @Inject
    private Reflections reflections;

    @Inject
    private Converters converters;

    @Override
    public ColumnEntity toColumn(Object entityInstance) {
        Objects.requireNonNull(entityInstance, "Object is required");
        ClassRepresentation representation = classRepresentations.get(entityInstance.getClass());
        ColumnEntity entity = ColumnEntity.of(representation.getName());
        representation.getFields().stream()
                .map(f -> to(f, entityInstance))
                .filter(FieldValue::isNotEmpty)
                .map(f -> f.toColumn(this, converters))
                .forEach(entity::add);
        return entity;
    }

    @Override
    public <T> T toEntity(Class<T> entityClass, ColumnEntity entity) {
        return toEntity(entityClass, entity.getColumns());
    }

    private <T> T toEntity(Class<T> entityClass, List<Column> columns) {
        ClassRepresentation representation = classRepresentations.get(entityClass);
        T instance = reflections.newInstance(representation.getConstructor());
        return convertEntity(columns, representation, instance);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T toEntity(ColumnEntity entity) {
        ClassRepresentation representation = classRepresentations.findByName(entity.getName());
        T instance = reflections.newInstance(representation.getConstructor());
        return convertEntity(entity.getColumns(), representation, instance);
    }

    private FieldValue to(FieldRepresentation field, Object entityInstance) {
        Object value = reflections.getValue(entityInstance, field.getField());
        return new FieldValue(value, field);
    }

    private <T> Consumer<String> feedObject(T instance, List<Column> columns, Map<String, FieldRepresentation> fieldsGroupByName) {
        return k -> {
            Optional<Column> column = columns.stream().filter(c -> c.getName().equals(k)).findFirst();
            FieldRepresentation field = fieldsGroupByName.get(k);
            if (EMBEDDED.equals(field.getType())) {
                setEmbeddedField(instance, columns, column, field);
            } else {
                setSingleField(instance, column, field);
            }
        };
    }

    private <T> void setSingleField(T instance, Optional<Column> column, FieldRepresentation field) {
        Value value = column.get().getValue();
        Optional<Class<? extends AttributeConverter>> converter = field.getConverter();
        if (converter.isPresent()) {
            AttributeConverter attributeConverter = converters.get(converter.get());
            Object attributeConverted = attributeConverter.convertToEntityAttribute(value.get());
            reflections.setValue(instance, field.getField(), field.getValue(Value.of(attributeConverted)));
        } else {
            reflections.setValue(instance, field.getField(), field.getValue(value));
        }
    }

    private <T> T convertEntity(List<Column> columns, ClassRepresentation representation, T instance) {
        Map<String, FieldRepresentation> fieldsGroupByName = representation.getFieldsGroupByName();
        List<String> names = columns.stream().map(Column::getName).sorted().collect(Collectors.toList());
        Predicate<String> existField = k -> Collections.binarySearch(names, k) >= 0;
        fieldsGroupByName.keySet().stream()
                .filter(existField.or(k -> EMBEDDED.equals(fieldsGroupByName.get(k).getType())))
                .forEach(feedObject(instance, columns, fieldsGroupByName));

        return instance;
    }

    private <T> void setEmbeddedField(T instance, List<Column> columns, Optional<Column> column, FieldRepresentation field) {
        if (column.isPresent()) {
            Column subColumn = column.get();
            reflections.setValue(instance, field.getField(), toEntity(field.getField().getType(), subColumn.get(new TypeReference<List<Column>>() {
            })));
        } else {
            reflections.setValue(instance, field.getField(), toEntity(field.getField().getType(), columns));
        }
    }

}
