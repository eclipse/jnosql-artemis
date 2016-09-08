package org.jnosql.artemis.document;

import java.util.Objects;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.jnosql.artemis.reflection.ClassRepresentation;
import org.jnosql.artemis.reflection.ClassRepresentations;
import org.jnosql.artemis.reflection.FieldRepresentation;
import org.jnosql.artemis.reflection.FieldValue;
import org.jnosql.artemis.reflection.Reflections;
import org.jnosql.diana.api.document.DocumentCollectionEntity;

@ApplicationScoped
public class DocumentEntityConverter {

    @Inject
    private ClassRepresentations classRepresentations;

    @Inject
    private Reflections reflections;


    public DocumentCollectionEntity toDocument(Object entityInstance) {
        Objects.requireNonNull(entityInstance, "Object is required");
        ClassRepresentation representation = classRepresentations.get(entityInstance.getClass());
        DocumentCollectionEntity entity = DocumentCollectionEntity.of(representation.getName());
        representation.getFields().stream()
                .map(f -> to(f, entityInstance))
                .filter(FieldValue::isNotEmpty)
                .map(FieldValue::toDocument)
                .forEach(entity::add);
        return entity;

    }

    private FieldValue to(FieldRepresentation field, Object entityInstance) {
        Object value = reflections.getValue(entityInstance, field.getField());
        return new FieldValue(value, field);
    }

}
