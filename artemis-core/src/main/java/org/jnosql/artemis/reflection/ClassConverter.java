/*
 * Copyright 2017 Eclipse Foundation
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
package org.jnosql.artemis.reflection;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;

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
        String entityName = reflections.getEntityName(entityClass);
        List<FieldRepresentation> fields = reflections.getFields(entityClass)
                .stream().map(this::to).collect(Collectors.toList());
        List<String> fieldsName = fields.stream().map(FieldRepresentation::getName).collect(Collectors.toList());
        return ClassRepresentation.builder().withName(entityName)
                .withClassInstance(entityClass)
                .withFields(fields)
                .withFieldsName(fieldsName)
                .build();
    }

    private FieldRepresentation to(Field field) {
        FieldType fieldType = FieldType.of(field);
        reflections.makeAccessible(field);
        String columnName = reflections.getColumnName(field);
        FieldRepresentationBuilder builder = FieldRepresentation.builder().withName(columnName)
                .withField(field).withType(fieldType);
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
