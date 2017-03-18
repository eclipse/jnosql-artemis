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


import org.jnosql.artemis.Converters;
import org.jnosql.artemis.column.ColumnEntityConverter;
import org.jnosql.artemis.document.DocumentEntityConverter;
import org.jnosql.diana.api.column.Column;
import org.jnosql.diana.api.document.Document;

/**
 * The tuple between the instance value and {@link FieldRepresentation}
 */
public interface FieldValue {


    /**
     * Creates a {@link FieldValue} instance
     *
     * @param value the value
     * @param field the field
     */
    static FieldValue of(Object value, FieldRepresentation field) {
        return new DefaultFieldValue(value, field);
    }

    /**
     * Returns the instance
     *
     * @return the instance
     */
    Object getValue();

    /**
     * returns the Field representation
     *
     * @return the {@link FieldRepresentation} instance
     */
    FieldRepresentation getField();

    /**
     * Returns true if {@link FieldValue#getValue()} is different of null
     *
     * @return if {@link FieldValue#getValue()} is different of null
     */
    boolean isNotEmpty();

    /**
     * Convert this instance to {@link Document}
     *
     * @param converter the converter
     * @return a {@link Document} instance
     */
    Document toDocument(DocumentEntityConverter converter, Converters converters);

    /**
     * Convert this instance to {@link Column}
     *
     * @param converter the converter
     * @return a {@link Column} instance
     */
    Column toColumn(ColumnEntityConverter converter, Converters converters);
}
