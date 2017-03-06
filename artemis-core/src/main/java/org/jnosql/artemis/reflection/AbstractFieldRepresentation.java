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

import java.lang.reflect.Field;
import java.util.Optional;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.jnosql.artemis.AttributeConverter;
import org.jnosql.diana.api.Value;

/**
 * Base class to all {@link FieldRepresentation}
 *
 * @see FieldRepresentation
 */
abstract class AbstractFieldRepresentation implements FieldRepresentation {

    protected final FieldType type;

    protected final Field field;

    protected final String name;

    protected final Optional<Class<? extends AttributeConverter>> converter;

    AbstractFieldRepresentation(FieldType type, Field field, String name, Class<? extends AttributeConverter> converter) {
        this.type = type;
        this.field = field;
        this.name = name;
        this.converter = Optional.ofNullable(converter);
    }

    @Override
    public FieldType getType() {
        return type;
    }

    @Override
    public Field getField() {
        return field;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public <T extends AttributeConverter> Optional<Class<? extends AttributeConverter>> getConverter() {
        return converter;
    }


    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("type", type)
                .append("field", field)
                .append("name", name)
                .toString();
    }

    public Object getValue(Value value) {
        return value.get(field.getType());
    }
}
