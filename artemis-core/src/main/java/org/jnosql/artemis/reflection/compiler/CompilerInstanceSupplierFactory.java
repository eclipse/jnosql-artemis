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

import org.jnosql.artemis.reflection.InstanceSupplier;
import org.jnosql.artemis.reflection.InstanceSupplierFactory;
import org.jnosql.artemis.reflection.Reflections;

import java.lang.reflect.Constructor;

/**
 * An {@link InstanceSupplierFactory} implementation that uses compiler code
 */
final class CompilerInstanceSupplierFactory implements InstanceSupplierFactory {

    private static final String TEMPLATE_FILE = "InstanceSupplier.template";

    private static final String TEMPLATE = TemplateReader.INSTANCE.apply(TEMPLATE_FILE);

    private final JavaCompilerFacade compilerFacade;

    private final Reflections reflections;

    CompilerInstanceSupplierFactory(JavaCompilerFacade compilerFacade, Reflections reflections) {
        this.compilerFacade = compilerFacade;
        this.reflections = reflections;
    }

    @Override
    public InstanceSupplier apply(Constructor<?> constructor) {
        Class<?> declaringClass = constructor.getDeclaringClass();
        String packageName = declaringClass.getPackage().getName();
        String simpleName = declaringClass.getSimpleName() + "$InstanceSupplier";
        String newInsance = declaringClass.getName();
        String name = declaringClass.getName() + "$InstanceSupplier";
        String javaSource = StringFormatter.INSTANCE.format(TEMPLATE, packageName, simpleName, newInsance);
        System.out.println(javaSource);
        InstanceJavaSource source = new InstanceJavaSource(name, simpleName, javaSource);
        Class<? extends InstanceSupplier> supplier = compilerFacade.apply(source);
        return reflections.newInstance(supplier);
    }

    static final class InstanceJavaSource implements JavaSource<InstanceSupplier> {

        private final String name;

        private final String simpleName;

        private final String javaSource;


        InstanceJavaSource(String name, String simpleName, String javaSource) {
            this.name = name;
            this.simpleName = simpleName;
            this.javaSource = javaSource;
        }

        @Override
        public String getSimpleName() {
            return simpleName;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getJavaSource() {
            return javaSource;
        }

        @Override
        public Class<InstanceSupplier> getType() {
            return InstanceSupplier.class;
        }
    }
}
