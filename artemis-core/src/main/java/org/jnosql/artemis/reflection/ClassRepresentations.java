package org.jnosql.artemis.reflection;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.stream.Collectors;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class ClassRepresentations {

    @Inject
    private Reflections reflections;

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
        String columnName = reflections.getColumnName(field);
        FieldRepresentationBuilder builder = FieldRepresentation.builder().withName(columnName)
                .withField(field).withType(fieldType);
        switch (fieldType) {
            case COLLECTION:
                ParameterizedType genericType = (ParameterizedType) field.getGenericType();
                return builder.withValueClass((Class<?>) genericType.getActualTypeArguments()[0]).buildCollection();
            case MAP:
                ParameterizedType type = (ParameterizedType) field.getGenericType();
                return builder.withValueClass((Class<?>) type.getActualTypeArguments()[1])
                        .withKeyClass((Class<?>) type.getActualTypeArguments()[0]).buildMap();
            default:
                return builder.buildDefault();


        }
    }


}
