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


import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The default implementation of {@link ClassRepresentation}.
 * It's storage the class information in a {@link ConcurrentHashMap}
 */
@ApplicationScoped
class DefaultClassRepresentations implements ClassRepresentations {

    private Map<String, ClassRepresentation> representations;

    private Map<Class<?>, ClassRepresentation> classes;

    private Map<String, ClassRepresentation> findBySimpleName;

    private Map<String, ClassRepresentation> findByClassName;


    @Inject
    private ClassConverter classConverter;

    @Inject
    private ClassRepresentationsExtension extension;

    @PostConstruct
    public void init() {
        representations = new ConcurrentHashMap<>();
        classes = new ConcurrentHashMap<>();
        findBySimpleName = new ConcurrentHashMap<>();
        findByClassName = new ConcurrentHashMap<>();

        classes.putAll(extension.getClasses());
        representations.putAll(extension.getRepresentations());
        representations.values().stream().forEach(r -> {
            Class<?> entityClass = r.getClassInstance();
            findBySimpleName.put(entityClass.getSimpleName(), r);
            findByClassName.put(entityClass.getName(), r);
        });
    }

    void load(Class classEntity) {
        ClassRepresentation classRepresentation = classConverter.create(classEntity);
        representations.put(classEntity.getName(), classRepresentation);
        findBySimpleName.put(classEntity.getSimpleName(), classRepresentation)
        findByClassName.put(classEntity.getName(), classRepresentation)
    }

    @Override
    public ClassRepresentation get(Class classEntity) {
        ClassRepresentation classRepresentation = classes.get(classEntity);
        if (classRepresentation == null) {
            classRepresentation = classConverter.create(classEntity);
            classes.put(classEntity, classRepresentation);
            return this.get(classEntity);
        }
        return classRepresentation;
    }

    @Override
    public ClassRepresentation findByName(String name) {
        return representations.keySet().stream()
                .map(k -> representations.get(k))
                .filter(r -> r.getName().equalsIgnoreCase(name)).findFirst()
                .orElseThrow(() -> new ClassInformationNotFoundException("There is not entity found with the name: " + name));
    }

    @Override
    public Optional<ClassRepresentation> findBySimpleName(String name) {
        Objects.requireNonNull(name, "name is required");
        return Optional.ofNullable(findBySimpleName.get(name));
    }

    @Override
    public Optional<ClassRepresentation> findByClassName(String name) {
        Objects.requireNonNull(name, "name is required");
        return Optional.ofNullable(findByClassName.get(name));
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("DefaultClassRepresentations{");
        sb.append("representations-size=").append(representations.size());
        sb.append(", classes=").append(classes);
        sb.append(", classConverter=").append(classConverter);
        sb.append(", extension=").append(extension);
        sb.append('}');
        return sb.toString();
    }
}
