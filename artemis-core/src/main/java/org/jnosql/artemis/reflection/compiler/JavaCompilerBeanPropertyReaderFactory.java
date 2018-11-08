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

class JavaCompilerBeanPropertyReaderFactory {

    private final JavaCompilerFacade compilerFacade = new JavaCompilerFacade(
            JavaCompilerBeanPropertyReaderFactory.class.getClassLoader());

    public BeanPropertyReader generate(Class<?> beanClass, String propertyName) {

        String getterName = "get" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
        String packageName = JavaCompilerBeanPropertyReaderFactory.class.getPackage().getName()
                + ".generated." + beanClass.getPackage().getName();
        String simpleClassName = beanClass.getSimpleName() + "$" + propertyName;
        String fullClassName = packageName + "." + simpleClassName;
        final String source = "package " + packageName + ";\n"
                + "public class " + simpleClassName + " implements " + BeanPropertyReader.class.getName() + " {\n"
                + "    public Object executeGetter(Object bean) {\n"
                + "        return ((" + beanClass.getName() + ") bean)." + getterName + "();\n"
                + "    }\n"
                + "}";

        JavaSource<BeanPropertyReader> javaSource = new JavaSource() {
            @Override
            public String getClassName() {
                return fullClassName;
            }

            @Override
            public String getJavaSource() {
                return source;
            }

            @Override
            public Class<BeanPropertyReader> getType() {
                return BeanPropertyReader.class;
            }
        };
        Class<? extends BeanPropertyReader> compiledClass = compilerFacade.apply(javaSource);
        try {
            return compiledClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new IllegalStateException("The generated class (" + fullClassName + ") failed to instantiate.", e);
        }
    }

}
