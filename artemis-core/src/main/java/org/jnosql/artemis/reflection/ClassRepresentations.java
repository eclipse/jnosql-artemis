package org.jnosql.artemis.reflection;

import java.lang.reflect.Field;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class ClassRepresentations {

    @Inject
    private Reflections reflections;

    public ClassRepresentation create(Class entityClass) {
        String entityName = reflections.getEntityName(entityClass);
        List<Field> fields = reflections.getFields(entityClass);
        for (Field field : fields) {

        }

        return ClassRepresentation.builder().withName(entityName)
                .withClassInstance(entityClass)
                .build();
    }
}
