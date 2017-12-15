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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

@ApplicationScoped
class ClassConverter {


    private Reflections reflections;


    @Inject
    ClassConverter(Reflections reflections) {
        this.reflections = reflections;
    }

    ClassConverter() {
    }

    public ClassRepresentation create(Class<?> entityClass) {

        Constructor constructor = reflections.makeAccessible(entityClass);

        String entityName = reflections.getEntityName(entityClass);

        List<FieldRepresentation> fields = reflections.getFields(entityClass)
                .stream().map(this::to).collect(toList());

        List<String> fieldsName = fields.stream().map(FieldRepresentation::getName).collect(toList());

        Map<String, String> nativeFieldGroupByJavaField =
                getNativeFieldGroupByJavaField(fields, "", "");

        Map<String, FieldRepresentation> fieldsGroupedByName = fields.stream()
                .collect(collectingAndThen(toMap(FieldRepresentation::getName,
                        Function.identity()), Collections::unmodifiableMap));

        return DefaultClassRepresentation.builder().withName(entityName)
                .withClassInstance(entityClass)
                .withFields(fields)
                .withFieldsName(fieldsName)
                .withConstructor(constructor)
                .withJavaFieldGroupedByColumn(nativeFieldGroupByJavaField)
                .withFieldsGroupedByName(fieldsGroupedByName)
                .build();
    }

    private Map<String, String> getNativeFieldGroupByJavaField(List<FieldRepresentation> fields,
                                                               String javaField, String nativeField) {

        Map<String, String> nativeFieldGrouopByJavaField = new HashMap<>();

        for (FieldRepresentation field : fields) {
            appendValue(nativeFieldGrouopByJavaField, field, javaField, nativeField);
        }

        return nativeFieldGrouopByJavaField;
    }

    private void appendValue(Map<String, String> nativeFieldGrouopByJavaField, FieldRepresentation field,
                             String javaField, String nativeField) {


        switch (field.getType()) {
            case SUBENTITY:
                Class<?> subentityClass = field.getNativeField().getType();
                Map<String, String> subenityMap = getNativeFieldGroupByJavaField(
                        reflections.getFields(subentityClass)
                                .stream().map(this::to).collect(toList()),
                        appendPreparePrefix(javaField, field.getFieldName()), nativeField);

                String subentityNative = subenityMap.values().stream().collect(Collectors.joining(","));
                nativeFieldGrouopByJavaField.put(appendPrefix(javaField, field.getFieldName()), subentityNative);
                nativeFieldGrouopByJavaField.putAll(subenityMap);
                return;
            case EMBEDDED:
                Class<?> embeddedEntityClass = field.getNativeField().getType();
                Map<String, String> embeddedMap = getNativeFieldGroupByJavaField(
                        reflections.getFields(embeddedEntityClass)
                                .stream().map(this::to).collect(toList()),
                        appendPreparePrefix(javaField, field.getFieldName()),
                        appendPreparePrefix(nativeField, field.getName()));

                String embeddedNative = embeddedMap.values().stream().collect(Collectors.joining(","));
                nativeFieldGrouopByJavaField.put(appendPrefix(javaField, field.getFieldName()), embeddedNative);
                nativeFieldGrouopByJavaField.putAll(embeddedMap);
                return;
            case COLLECTION:
            default:
                nativeFieldGrouopByJavaField.put(javaField.concat(field.getFieldName()),
                        nativeField.concat(field.getName()));
                return;
        }
        
    }

    private String appendPreparePrefix(String prefix, String field) {
        return appendPrefix(prefix, field).concat(".");
    }

    private String appendPrefix(String prefix, String field) {
        if (prefix.isEmpty()) {
            return field;
        } else {
            return prefix.concat(field);
        }
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
