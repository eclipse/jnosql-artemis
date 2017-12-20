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
