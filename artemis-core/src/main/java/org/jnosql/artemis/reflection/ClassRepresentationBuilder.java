/*
 * Copyright 2017 Otavio Santana and others
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
        return new ClassRepresentation(name, fieldsName, classInstance, fields, constructor);
    }
}