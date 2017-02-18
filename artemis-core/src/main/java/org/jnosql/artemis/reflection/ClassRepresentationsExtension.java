/*
 * Copyright 2017 Otavio Santana and others
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
import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.jnosql.artemis.Entity;

/**
 * This class is a CDI extension to load all class that has {@link Entity} annotation.
 * This extension will load all Classes and put in a map.
 * Where the key is {@link Class#getName()} and the value is {@link ClassRepresentation}
 */
@ApplicationScoped
public class ClassRepresentationsExtension implements Extension {

    private static final Logger LOGGER = Logger.getLogger(ClassRepresentationsExtension.class.getName());

    private final ClassConverter classConverter = new ClassConverter(new Reflections());

    private final Map<String, ClassRepresentation> representations = new ConcurrentHashMap<>();


    /**
     * Event observer
     *
     * @param target the target
     * @param <T>    the type
     */
    public <T> void initializePropertyLoading(final @Observes ProcessAnnotatedType<T> target) {

        AnnotatedType<T> at = target.getAnnotatedType();
        if (!at.isAnnotationPresent(Entity.class)) {
            return;
        }
        Class<T> javaClass = target.getAnnotatedType().getJavaClass();
        LOGGER.info("scanning type: " + javaClass.getName());
        ClassRepresentation classRepresentation = classConverter.create(javaClass);
        representations.put(classRepresentation.getName(), classRepresentation);
    }


    /**
     * Returns the representations loaded in CDI startup
     *
     * @return the class loaded
     */
    public Map<String, ClassRepresentation> getRepresentations() {
        return representations;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("representations-size", representations.size())
                .append("representations", representations)
                .toString();
    }
}
