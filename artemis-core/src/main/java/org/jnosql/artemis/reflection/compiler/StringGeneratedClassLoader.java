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

import java.util.HashMap;
import java.util.Map;

public final class StringGeneratedClassLoader extends ClassLoader {

    private final Map<String, StringGeneratedClassFileObject> fileObjectMap = new HashMap<>();

    public StringGeneratedClassLoader(ClassLoader parent) {
        super(parent);
    }

    @Override
    protected Class<?> findClass(String fullClassName) throws ClassNotFoundException {
        StringGeneratedClassFileObject fileObject = fileObjectMap.get(fullClassName);
        if (fileObject != null) {
            byte[] classBytes = fileObject.getClassBytes();
            return defineClass(fullClassName, classBytes, 0, classBytes.length);
        }
        return super.findClass(fullClassName);
    }

    public void addJavaFileObject(String qualifiedName, StringGeneratedClassFileObject fileObject) {
        fileObjectMap.put(qualifiedName, fileObject);
    }

}