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

import java.lang.reflect.Field;

class FieldRepresentationBuilder {

    private FieldType type;

    private Field field;

    private String name;

    private Class valueClass;

    private Class keyClass;

    public FieldRepresentationBuilder withType(FieldType type) {
        this.type = type;
        return this;
    }

    public FieldRepresentationBuilder withField(Field field) {
        this.field = field;
        return this;
    }

    public FieldRepresentationBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public FieldRepresentationBuilder withValueClass(Class valueClass) {
        this.valueClass = valueClass;
        return this;
    }

    public FieldRepresentationBuilder withKeyClass(Class keyClass) {
        this.keyClass = keyClass;
        return this;
    }

    public DefaultFieldRepresentation buildDefault() {
        return new DefaultFieldRepresentation(type, field, name);
    }

    public MapFieldRepresentation buildMap() {
        return new MapFieldRepresentation(type, field, name, valueClass, keyClass);
    }

    public CollectionFieldRepresentation buildCollection() {
        return new CollectionFieldRepresentation(type, field, name, valueClass);
    }
}
