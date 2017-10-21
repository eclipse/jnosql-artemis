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
package org.jnosql.artemis.key;

import org.jnosql.artemis.IdNotFoundException;
import org.jnosql.artemis.reflection.ClassRepresentation;
import org.jnosql.artemis.reflection.ClassRepresentations;
import org.jnosql.artemis.reflection.FieldRepresentation;
import org.jnosql.artemis.reflection.Reflections;
import org.jnosql.diana.api.Value;
import org.jnosql.diana.api.key.KeyValueEntity;

import java.util.List;
import java.util.Objects;

import static java.util.Objects.requireNonNull;

/**
 * Template method to {@link KeyValueEntityConverter}
 */
public abstract class AbstractKeyValueEntityConverter implements KeyValueEntityConverter {

    protected abstract ClassRepresentations getClassRepresentations();

    protected abstract Reflections getReflections();

    @Override
    public KeyValueEntity<?> toKeyValue(Object entityInstance) {
        requireNonNull(entityInstance, "Object is required");
        Class<?> clazz = entityInstance.getClass();
        ClassRepresentation representation = getClassRepresentations().get(clazz);

        FieldRepresentation key = getId(clazz, representation);
        Object value = getReflections().getValue(entityInstance, key.getNativeField());
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
        FieldRepresentation key = getId(entityClass, getClassRepresentations().get(entityClass));
        Object keyValue = getReflections().getValue(t, key.getNativeField());
        if (Objects.isNull(keyValue) || !keyValue.equals(entity.getKey())) {
            getReflections().setValue(t, key.getNativeField(), entity.getKey());
        }
        return t;
    }

    @Override
    public <T> T toEntity(Class<T> entityClass, Value value) throws IdNotFoundException, NullPointerException {
        T t = value.get(entityClass);
        if (Objects.isNull(t)) {
            return null;
        }
        return t;
    }

    private FieldRepresentation getId(Class<?> clazz, ClassRepresentation representation) {
        List<FieldRepresentation> fields = representation.getFields();
        return fields.stream().filter(FieldRepresentation::isId)
                .findFirst().orElseThrow(() -> IdNotFoundException.newInstance(clazz));
    }
}
