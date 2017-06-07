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
import java.util.ArrayList;
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
        return FieldValue.of(value, field);
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
            Object value = subColumn.get();
            if (Map.class.isInstance(value)) {
                Map map = Map.class.cast(value);
                List<Column> embeddedColumns = new ArrayList<>();
                for (Object key : map.keySet()) {
                    embeddedColumns.add(Column.of(key.toString(), map.get(key)));
                }
                reflections.setValue(instance, field.getField(), toEntity(field.getField().getType(), embeddedColumns));
            } else {
                reflections.setValue(instance, field.getField(), toEntity(field.getField().getType(),
                        subColumn.get(new TypeReference<List<Column>>() {
                        })));
            }

        } else {
            reflections.setValue(instance, field.getField(), toEntity(field.getField().getType(), columns));
        }
    }

}
