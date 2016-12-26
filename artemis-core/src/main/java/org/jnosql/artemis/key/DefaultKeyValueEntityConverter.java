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
package org.jnosql.artemis.key;

import org.jnosql.artemis.reflection.ClassRepresentation;
import org.jnosql.artemis.reflection.ClassRepresentations;
import org.jnosql.artemis.reflection.FieldRepresentation;
import org.jnosql.artemis.reflection.Reflections;
import org.jnosql.diana.api.Value;
import org.jnosql.diana.api.key.KeyValueEntity;

import javax.inject.Inject;
import java.util.List;
import java.util.Objects;

import static java.util.Objects.requireNonNull;

/**
 * The default implementation of {@link KeyValueEntityConverter}
 */
class DefaultKeyValueEntityConverter implements KeyValueEntityConverter {

    @Inject
    private ClassRepresentations classRepresentations;

    @Inject
    private Reflections reflections;

    @Override
    public KeyValueEntity<?> toKeyValue(Object entityInstance) {
        requireNonNull(entityInstance, "Object is required");
        Class<?> clazz = entityInstance.getClass();
        ClassRepresentation representation = classRepresentations.get(clazz);

        FieldRepresentation key = getKey(clazz, representation);
        Object value = reflections.getValue(entityInstance, key.getField());
        requireNonNull(value, String.format("The key field %s is required", key.getName()));

        return KeyValueEntity.of(value, entityInstance);
    }

    @Override
    public <T> T toEntity(Class<T> entityClass, KeyValueEntity<?> entity) {

        Value value = entity.getValue();
        T t = value.get(entityClass);
        if (Objects.isNull(t)) {
            return null;
        }
        FieldRepresentation key = getKey(entityClass, classRepresentations.get(entityClass));
        Object keyValue = reflections.getValue(t, key.getField());
        if (Objects.isNull(keyValue)) {
            reflections.setValue(t, key.getField(), entity.getKey());
        }
        return t;
    }

    private FieldRepresentation getKey(Class<?> clazz, ClassRepresentation representation) {
        List<FieldRepresentation> fields = representation.getFields();
        return fields.stream().filter(FieldRepresentation::isKey)
                .findFirst().orElseThrow(() -> KeyNotFoundException.newInstance(clazz));
    }
}
