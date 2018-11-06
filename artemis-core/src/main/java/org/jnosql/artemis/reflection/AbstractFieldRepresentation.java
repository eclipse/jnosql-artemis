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

import org.jnosql.artemis.AttributeConverter;
import org.jnosql.diana.api.Value;

import java.lang.reflect.Field;
import java.util.Optional;

/**
 * Base class to all {@link FieldRepresentation}
 *
 * @see FieldRepresentation
 */
abstract class AbstractFieldRepresentation implements FieldRepresentation {

    protected final FieldType type;

    protected final Field field;

    protected final String name;

    protected final String fieldName;

    protected final Class<? extends AttributeConverter> converter;

    protected final FieldReader fieldReader;

    protected final SetterAcessor setterAcessor;

    AbstractFieldRepresentation(FieldType type, Field field, String name,
                                Class<? extends AttributeConverter> converter, Reflections reflections) {
        this.type = type;
        this.field = field;
        this.name = name;
        this.fieldName = field.getName();
        this.converter = converter;
        this.fieldReader = b -> reflections.getValue(b, field);
        this.setterAcessor = (b, v) -> reflections.setValue(b, field, v);
    }

    @Override
    public FieldType getType() {
        return type;
    }

    @Override
    public Field getNativeField() {
        return field;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getFieldName() {
        return fieldName;
    }

    @Override
    public FieldReader getFieldReader() {
        return this.fieldReader;
    }

    @Override
    public SetterAcessor getSetterAcessor() {
        return this.setterAcessor;
    }

    @Override
    public <T extends AttributeConverter> Optional<Class<? extends AttributeConverter>> getConverter() {
        return Optional.ofNullable(converter);
    }


    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("AbstractFieldRepresentation{");
        sb.append("type=").append(type);
        sb.append(", field=").append(field);
        sb.append(", name='").append(name).append('\'');
        sb.append(", fieldName='").append(fieldName).append('\'');
        sb.append(", converter=").append(converter);
        sb.append('}');
        return sb.toString();
    }

    public Object getValue(Value value) {
        return value.get(field.getType());
    }
}
