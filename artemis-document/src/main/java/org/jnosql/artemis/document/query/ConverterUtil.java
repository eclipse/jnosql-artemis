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
package org.jnosql.artemis.document.query;

import org.jnosql.artemis.Converters;
import org.jnosql.artemis.reflection.ClassRepresentation;
import org.jnosql.artemis.reflection.FieldRepresentation;
import org.jnosql.diana.api.Value;

import java.lang.reflect.Field;
import java.util.Optional;

final class ConverterUtil {

    private ConverterUtil() {

    }

    static Object getValue(Object value, ClassRepresentation representation, String name, Converters converters) {
        Optional<FieldRepresentation> fieldOptional = representation.getFieldRepresentation(name);
        if (fieldOptional.isPresent()) {
            FieldRepresentation field = fieldOptional.get();
            Field nativeField = field.getNativeField();
            if (!nativeField.getType().equals(value.getClass())) {
                return field.getConverter()
                        .map(converters::get)
                        .map(a -> a.convertToDatabaseColumn(value))
                        .orElseGet(() -> Value.of(value).get(nativeField.getType()));
            }

            return field.getConverter()
                    .map(converters::get)
                    .map(a -> a.convertToDatabaseColumn(value))
                    .orElse(value);
        }
        return value;
    }
}
