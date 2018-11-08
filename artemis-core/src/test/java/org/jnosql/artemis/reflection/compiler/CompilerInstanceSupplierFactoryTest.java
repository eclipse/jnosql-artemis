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
package org.jnosql.artemis.reflection.compiler;

import org.jnosql.artemis.reflection.DefaultReflections;
import org.jnosql.artemis.reflection.InstanceSupplier;
import org.jnosql.artemis.reflection.Reflections;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;

class CompilerInstanceSupplierFactoryTest {

    private final JavaCompilerFacade compilerFacade = new JavaCompilerFacade(
            JavaCompilerBeanPropertyReaderFactory.class.getClassLoader());

    private final Reflections reflections = new DefaultReflections();

    @Test
    public void shouldCreateInstanceSupplier() {
        CompilerInstanceSupplierFactory factory = new CompilerInstanceSupplierFactory(compilerFacade, reflections);
        InstanceSupplier instanceSupplier = factory.apply(Foo.class.getConstructors()[0]);
        Assertions.assertNotNull(instanceSupplier);
        Object value = instanceSupplier.get();
        Assertions.assertTrue( value instanceof Foo);
    }

    @Test
    public void shouldCreateInstanceSupplier2() {
        CompilerInstanceSupplierFactory factory = new CompilerInstanceSupplierFactory(compilerFacade, reflections);
        Constructor<?> constructor = Faa.class.getDeclaredConstructors()[0];
        constructor.setAccessible(true);

        InstanceSupplier instanceSupplier = factory.apply(constructor);
        Assertions.assertNotNull(instanceSupplier);
        Object value = instanceSupplier.get();
        Assertions.assertTrue( value instanceof Foo);
    }

}