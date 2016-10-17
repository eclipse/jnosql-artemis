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


import org.jnosql.artemis.column.ColumnEntityConverter;
import org.jnosql.diana.api.column.Column;
import org.jnosql.diana.api.document.Document;

/**
 * The tuple between the instance value and {@link FieldRepresentation}
 */
public final class FieldValue {

    private final Object value;

    private final FieldRepresentation field;


    /**
     * Creates a {@link FieldValue} instance
     *
     * @param value the value
     * @param field the field
     */
    public FieldValue(Object value, FieldRepresentation field) {
        this.value = value;
        this.field = field;
    }

    /**
     * Returns the instance
     *
     * @return the instance
     */
    public Object getValue() {
        return value;
    }

    /**
     * returns the Field representation
     *
     * @return the {@link FieldRepresentation} instance
     */
    public FieldRepresentation getField() {
        return field;
    }

    /**
     * Returns true if {@link FieldValue#getValue()} is different of null
     *
     * @return if {@link FieldValue#getValue()} is different of null
     */
    public boolean isNotEmpty() {
        return value != null;
    }

    /**
     * Convert this instance to {@link Document}
     *
     * @return a {@link Document} instance
     */
    public Document toDocument() {
        return Document.of(field.getName(), value);
    }

    /**
     * Convert this instance to {@link Column}
     *
     * @param converter the converter
     * @return a {@link Column} instance
     */
    public Column toColumn(ColumnEntityConverter converter) {

        if (FieldType.EMBEDDED.equals(getField().getType())) {
            return Column.of(field.getName(), converter.toColumn(value));
        }
        return Column.of(field.getName(), value);
    }

    @Override
    public String toString() {
        return "FieldValue{" +
                "value=" + value +
                ", field=" + field +
                '}';
    }
}
