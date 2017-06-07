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
package org.jnosql.artemis.reflection;

import org.jnosql.artemis.Convert;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.List;

import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toList;

@ApplicationScoped
class ClassConverter {


    private Reflections reflections;


    @Inject
    ClassConverter(Reflections reflections) {
        this.reflections = reflections;
    }

    ClassConverter() {
    }

    public ClassRepresentation create(Class entityClass) {
        Constructor constructor = reflections.makeAccessible(entityClass);
        String entityName = reflections.getEntityName(entityClass);
        List<FieldRepresentation> fields = reflections.getFields(entityClass)
                .stream().map(this::to).collect(toList());
        List<String> fieldsName = fields.stream().map(FieldRepresentation::getName).collect(toList());
        return DefaultClassRepresentation.builder().withName(entityName)
                .withClassInstance(entityClass)
                .withFields(fields)
                .withFieldsName(fieldsName)
                .withConstructor(constructor)
                .build();
    }


    private FieldRepresentation to(Field field) {
        FieldType fieldType = FieldType.of(field);
        reflections.makeAccessible(field);
        Convert convert = field.getAnnotation(Convert.class);
        boolean id = reflections.isIdField(field);
        String columnName = id ? reflections.getIdName(field) : reflections.getColumnName(field);

        FieldRepresentationBuilder builder = FieldRepresentation.builder().withName(columnName)
                .withField(field).withType(fieldType).withId(id);
        if (nonNull(convert)) {
            builder.withConverter(convert.value());
        }
        switch (fieldType) {
            case COLLECTION:
            case MAP:
                builder.withTypeSupplier(field::getGenericType);
                return builder.buildGeneric();
            case EMBEDDED:
                return builder.withEntityName(reflections.getEntityName(field.getType())).buildEmedded();
            default:
                return builder.buildDefault();


        }
    }


}
