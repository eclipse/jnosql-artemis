/*
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


import org.jnosql.artemis.reflection.*;
import org.jnosql.diana.api.Value;
import org.jnosql.diana.api.column.ColumnEntity;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * The default implementation to {@link ColumnEntityConverter}
 */
@ApplicationScoped
class DefaultColumnEntityConverter implements ColumnEntityConverter {

    @Inject
    private ClassRepresentations classRepresentations;

    @Inject
    private Reflections reflections;

    @Override
    public ColumnEntity toColumn(Object entityInstance) {
        Objects.requireNonNull(entityInstance, "Object is required");
        ClassRepresentation representation = classRepresentations.get(entityInstance.getClass());
        ColumnEntity entity = ColumnEntity.of(representation.getName());
        representation.getFields().stream()
                .map(f -> to(f, entityInstance))
                .filter(FieldValue::isNotEmpty)
                .map(FieldValue::toColumn)
                .forEach(entity::add);
        return entity;
    }

    @Override
    public <T> T toEntity(Class<T> entityClass, ColumnEntity entity) {
        ClassRepresentation representation = classRepresentations.get(entityClass);
        T instance = reflections.newInstance(entityClass);
        return convertEntity(entity, representation, instance);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T toEntity(ColumnEntity entity) {
        ClassRepresentation representation = classRepresentations.findByName(entity.getName());
        T instance = reflections.newInstance((Class<T>) representation.getClassInstance());
        return convertEntity(entity, representation, instance);
    }

    private FieldValue to(FieldRepresentation field, Object entityInstance) {
        Object value = reflections.getValue(entityInstance, field.getField());
        return new FieldValue(value, field);
    }

    private <T> Consumer<String> feedObject(T instance, ColumnEntity entity, Map<String, FieldRepresentation> fieldsGroupByName) {
        return k -> {
            Value value = entity.find(k).get().getValue();
            FieldRepresentation field = fieldsGroupByName.get(k);
            reflections.setValue(instance, field.getField(), field.getValue(value, reflections));
        };
    }

    private <T> T convertEntity(ColumnEntity entity, ClassRepresentation representation, T instance) {
        Map<String, FieldRepresentation> fieldsGroupByName = representation.getFieldsGroupByName();
        fieldsGroupByName.keySet().stream()
                .filter(k -> entity.find(k).isPresent())
                .forEach(feedObject(instance, entity, fieldsGroupByName));

        return instance;
    }
}
