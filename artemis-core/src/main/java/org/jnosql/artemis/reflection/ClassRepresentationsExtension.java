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


import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import org.jnosql.artemis.Entity;

/**
 * This class is a CDI extension to load all class that has {@link Entity} annotation
 */
@ApplicationScoped
public class ClassRepresentationsExtension implements Extension {

    private static final Logger LOGGER = Logger.getLogger(ClassRepresentationsExtension.class.getName());


    private static final ClassRepresentations CLASS_REPRESENTATIONS;


    static {
        ClassConverter classConverter = new ClassConverter(new Reflections());
        CLASS_REPRESENTATIONS = new ClassRepresentations(classConverter);
    }


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
        CLASS_REPRESENTATIONS.load(javaClass);
    }

}
