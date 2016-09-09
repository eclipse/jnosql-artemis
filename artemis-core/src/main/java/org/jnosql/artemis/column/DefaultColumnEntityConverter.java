package org.jnosql.artemis.column;


import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.jnosql.artemis.reflection.ClassRepresentation;
import org.jnosql.artemis.reflection.ClassRepresentations;
import org.jnosql.artemis.reflection.FieldRepresentation;
import org.jnosql.artemis.reflection.FieldValue;
import org.jnosql.artemis.reflection.Reflections;
import org.jnosql.diana.api.Value;
import org.jnosql.diana.api.column.ColumnFamilyEntity;

@ApplicationScoped
class DefaultColumnEntityConverter implements ColumnEntityConverter {

    @Inject
    private ClassRepresentations classRepresentations;

    @Inject
    private Reflections reflections;

    @Override
    public ColumnFamilyEntity toColumn(Object entityInstance) {
        Objects.requireNonNull(entityInstance, "Object is required");
        ClassRepresentation representation = classRepresentations.get(entityInstance.getClass());
        ColumnFamilyEntity entity = ColumnFamilyEntity.of(representation.getName());
        representation.getFields().stream()
                .map(f -> to(f, entityInstance))
                .filter(FieldValue::isNotEmpty)
                .map(FieldValue::toColumn)
                .forEach(entity::add);
        return entity;
    }

    @Override
    public <T> T toEntity(Class<T> entityClass, ColumnFamilyEntity entity) {
        ClassRepresentation representation = classRepresentations.get(entityClass);
        T instance = reflections.newInstance(entityClass);
        return convertEntity(entity, representation, (T) instance);
    }

    @Override
    public <T> T toEntity(ColumnFamilyEntity entity) {
        ClassRepresentation representation = classRepresentations.findByName(entity.getName());
        T instance = reflections.newInstance((Class<T>) representation.getClassInstance());
        return convertEntity(entity, representation, instance);
    }

    private FieldValue to(FieldRepresentation field, Object entityInstance) {
        Object value = reflections.getValue(entityInstance, field.getField());
        return new FieldValue(value, field);
    }

    private <T> Consumer<String> feedObject(T instance, ColumnFamilyEntity entity, Map<String, FieldRepresentation> fieldsGroupByName) {
        return k -> {
            Value value = entity.find(k).get().getValue();
            FieldRepresentation field = fieldsGroupByName.get(k);
            reflections.setValue(instance, field.getField(), field.getValue(value, reflections));
        };
    }

    private <T> T convertEntity(ColumnFamilyEntity entity, ClassRepresentation representation, T instance) {
        Map<String, FieldRepresentation> fieldsGroupByName = representation.getFieldsGroupByName();
        fieldsGroupByName.keySet().stream()
                .filter(k -> entity.find(k).isPresent())
                .forEach(feedObject(instance, entity, fieldsGroupByName));

        return instance;
    }
}
