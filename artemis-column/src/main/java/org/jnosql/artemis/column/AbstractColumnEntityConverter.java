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

import org.jnosql.artemis.Converters;
import org.jnosql.artemis.column.ColumnFieldConverters.ColumnFieldConverterFactory;
import org.jnosql.artemis.reflection.ClassRepresentation;
import org.jnosql.artemis.reflection.ClassRepresentations;
import org.jnosql.artemis.reflection.FieldMapping;
import org.jnosql.artemis.reflection.FieldType;
import org.jnosql.artemis.reflection.FieldValue;
import org.jnosql.diana.api.column.Column;
import org.jnosql.diana.api.column.ColumnEntity;

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
 * Template method to {@link ColumnEntityConverter}
 */
public abstract class AbstractColumnEntityConverter implements ColumnEntityConverter {

    private final ColumnFieldConverterFactory converterFactory = new ColumnFieldConverterFactory();


    protected abstract ClassRepresentations getClassRepresentations();

    protected abstract Converters getConverters();

    @Override
    public ColumnEntity toColumn(Object entityInstance) {
        requireNonNull(entityInstance, "Object is required");
        ClassRepresentation representation = getClassRepresentations().get(entityInstance.getClass());
        ColumnEntity entity = ColumnEntity.of(representation.getName());
        representation.getFields().stream()
                .map(f -> to(f, entityInstance))
                .filter(FieldValue::isNotEmpty)
                .map(f -> f.toColumn(this, getConverters()))
                .flatMap(List::stream)
                .forEach(entity::add);
        return entity;
    }

    @Override
    public <T> T toEntity(Class<T> entityClass, ColumnEntity entity) {
        requireNonNull(entity, "entity is required");
        requireNonNull(entityClass, "entityClass is required");
        return toEntity(entityClass, entity.getColumns());
    }

    @Override
    public <T> T toEntity(T entityInstance, ColumnEntity entity) {
        requireNonNull(entity, "entity is required");
        requireNonNull(entityInstance, "entityInstance is required");
        ClassRepresentation representation = getClassRepresentations().get(entityInstance.getClass());
        return convertEntity(entity.getColumns(), representation, entityInstance);
    }

    @Override
    public <T> T toEntity(ColumnEntity entity) {
        requireNonNull(entity, "entity is required");
        ClassRepresentation representation = getClassRepresentations().findByName(entity.getName());
        T instance = representation.newInstance();
        return convertEntity(entity.getColumns(), representation, instance);
    }

    protected ColumnFieldValue to(FieldMapping field, Object entityInstance) {
        Object value = field.read(entityInstance);
        return DefaultColumnFieldValue.of(value, field);
    }

    protected <T> Consumer<String> feedObject(T instance, List<Column> columns, Map<String, FieldMapping> fieldsGroupByName) {
        return (String k) -> {
            Optional<Column> column = columns.stream().filter(c -> c.getName().equals(k)).findFirst();
            FieldMapping field = fieldsGroupByName.get(k);
            ColumnFieldConverter fieldConverter = converterFactory.get(field);
            fieldConverter.convert(instance, columns, column, field, this);
        };
    }

    protected <T> T toEntity(Class<T> entityClass, List<Column> columns) {
        ClassRepresentation representation = getClassRepresentations().get(entityClass);
        T instance = representation.newInstance();
        return convertEntity(columns, representation, instance);
    }

    private <T> T convertEntity(List<Column> columns, ClassRepresentation representation, T instance) {
        final Map<String, FieldMapping> fieldsGroupByName = representation.getFieldsGroupByName();
        final List<String> names = columns.stream().map(Column::getName).sorted().collect(Collectors.toList());
        final Predicate<String> existField = k -> Collections.binarySearch(names, k) >= 0;
        final Predicate<String> isElementType = k -> {
            FieldType type = fieldsGroupByName.get(k).getType();
            return EMBEDDED.equals(type) || SUBENTITY.equals(type);
        };
        fieldsGroupByName.keySet().stream()
                .filter(existField.or(isElementType))
                .forEach(feedObject(instance, columns, fieldsGroupByName));

        return instance;
    }

}
