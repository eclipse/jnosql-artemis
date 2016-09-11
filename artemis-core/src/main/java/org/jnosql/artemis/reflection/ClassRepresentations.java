/*
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


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * This class contains all the class in cached way to be used inside artemis.
 */
@ApplicationScoped
public class ClassRepresentations {

    private Map<String, ClassRepresentation> representations = new ConcurrentHashMap<>();


    private ClassConverter classConverter;

    @Inject
    ClassRepresentations(ClassConverter classConverter, ClassRepresentationsExtension extension) {
        this.classConverter = classConverter;
        representations.putAll(extension.getRepresentations());
    }

    ClassRepresentations() {
    }

    void load(Class classEntity) {
        ClassRepresentation classRepresentation = classConverter.create(classEntity);
        representations.put(classEntity.getName(), classRepresentation);
    }

    /**
     * Find a class in the cached way and return in a class representation,
     * if it's not found the class will be both, loaded and cached, when this method is called
     *
     * @param classEntity the class of entity
     * @return the class representation
     */
    public ClassRepresentation get(Class classEntity) {
        ClassRepresentation classRepresentation = representations.get(classEntity.getName());
        if (classRepresentation == null) {
            load(classEntity);
            return this.get(classEntity);
        }
        return classRepresentation;
    }

    /**
     * Returns the {@link ClassRepresentation} instance from {@link ClassRepresentation#getName()} in ignore case
     *
     * @param name the name to find ah {@link ClassRepresentation} instance
     * @return the {@link ClassRepresentation} from name
     * @throws ClassInformationNotFoundException when the class is not loaded
     */
    public ClassRepresentation findByName(String name) throws ClassInformationNotFoundException {
        return representations.keySet().stream()
                .map(k -> representations.get(k))
                .filter(r -> r.getName().equalsIgnoreCase(name)).findFirst()
                .orElseThrow(() -> new ClassInformationNotFoundException("There is not entity found with the name: " + name));
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SIMPLE_STYLE)
                .append("REPRESENTATIONS", representations)
                .toString();
    }


}
