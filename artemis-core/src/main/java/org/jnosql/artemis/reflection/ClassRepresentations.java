package org.jnosql.artemis.reflection;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class ClassRepresentations {

    @Inject
    private Reflections reflections;

    public ClassRepresentation create(Class entityClass) {
        String entityName = reflections.getEntityName(entityClass);

        return ClassRepresentation.builder().withName(entityName)
                .withClassInstance(entityClass)
                .build();
    }
}
