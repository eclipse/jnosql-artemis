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
package org.jnosql.artemis;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;

import javax.enterprise.inject.se.SeContainer;
import javax.enterprise.inject.se.SeContainerInitializer;
import javax.inject.Inject;
import java.lang.reflect.Field;

public class CDIExtension implements TestInstancePostProcessor {


    private static final SeContainer CONTAINER = SeContainerInitializer.newInstance().initialize();
    @Override
    public void postProcessTestInstance(Object testInstance, ExtensionContext context) throws IllegalAccessException {

        for (Field field : testInstance.getClass().getDeclaredFields()) {
            if(field.getAnnotation(Inject.class) != null) {
                Object injected = CONTAINER.select(field.getType()).get();
                field.setAccessible(true);
                field.set(testInstance, injected);
            }

        }


    }



}