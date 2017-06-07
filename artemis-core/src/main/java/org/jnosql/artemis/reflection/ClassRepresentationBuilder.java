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

import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.List;

class ClassRepresentationBuilder {

    private String name;

    private List<String> fieldsName = Collections.emptyList();

    private Class<?> classInstance;

    private Constructor constructor;

    private List<FieldRepresentation> fields = Collections.emptyList();

    public ClassRepresentationBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public ClassRepresentationBuilder withFieldsName(List<String> fieldsName) {
        this.fieldsName = fieldsName;
        return this;
    }

    public ClassRepresentationBuilder withClassInstance(Class<?> classInstance) {
        this.classInstance = classInstance;
        return this;
    }

    public ClassRepresentationBuilder withFields(List<FieldRepresentation> fields) {
        this.fields = fields;
        return this;
    }

    public ClassRepresentationBuilder withConstructor(Constructor constructor) {
        this.constructor = constructor;
        return this;
    }

    public ClassRepresentation build() {
        return new DefaultClassRepresentation(name, fieldsName, classInstance, fields, constructor);
    }
}