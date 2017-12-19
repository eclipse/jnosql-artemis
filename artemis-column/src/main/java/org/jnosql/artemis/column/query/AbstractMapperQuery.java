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
package org.jnosql.artemis.column.query;

import org.jnosql.artemis.Converters;
import org.jnosql.artemis.reflection.ClassRepresentation;
import org.jnosql.artemis.reflection.FieldRepresentation;
import org.jnosql.diana.api.Value;
import org.jnosql.diana.api.column.Column;
import org.jnosql.diana.api.column.ColumnCondition;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import static java.util.Arrays.asList;
import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;

abstract class AbstractMapperQuery {

    protected final String columnFamily;

    protected boolean negate;

    protected ColumnCondition condition;

    protected boolean and;

    protected String name;

    protected final ClassRepresentation representation;

    protected final Converters converters;

    protected long start;

    protected long limit;


    AbstractMapperQuery(ClassRepresentation representation, Converters converters) {
        this.representation = representation;
        this.converters = converters;
        this.columnFamily = representation.getName();
    }

    protected void appendCondition(ColumnCondition newCondition) {
        if (negate) {
            newCondition = newCondition.negate();
        }
        if (nonNull(condition)) {
            if (and) {
                this.condition = condition.and(newCondition);
            } else {
                this.condition = condition.or(newCondition);
            }
        } else {
            this.condition = newCondition;
        }
        this.negate = false;
        this.name = null;
    }

    protected void betweenImpl(Number valueA, Number valueB) {
        requireNonNull(valueA, "valueA is required");
        requireNonNull(valueB, "valueB is required");
        ColumnCondition newCondition = ColumnCondition
                .between(Column.of(representation.getColumnField(name), asList(getValue(valueA), getValue(valueB))));
        appendCondition(newCondition);
    }


    protected  <T> void inImpl(Iterable<T> values) {

        requireNonNull(values, "values is required");
        List<Object> convertedValues = StreamSupport.stream(values.spliterator(), false)
                .map(this::getValue).collect(toList());
        ColumnCondition newCondition = ColumnCondition
                .in(Column.of(representation.getColumnField(name), convertedValues));
        appendCondition(newCondition);
    }

    protected <T> void eqImpl(T value) {
        requireNonNull(value, "value is required");

        ColumnCondition newCondition = ColumnCondition
                .eq(Column.of(representation.getColumnField(name), getValue(value)));
        appendCondition(newCondition);
    }

    protected void likeImpl(String value) {
        requireNonNull(value, "value is required");
        ColumnCondition newCondition = ColumnCondition
                .like(Column.of(representation.getColumnField(name), getValue(value)));
        appendCondition(newCondition);
    }

    protected void gteImpl(Number value) {
        requireNonNull(value, "value is required");
        ColumnCondition newCondition = ColumnCondition
                .gte(Column.of(representation.getColumnField(name), getValue(value)));
        appendCondition(newCondition);
    }

    protected void gtImpl(Number value) {
        requireNonNull(value, "value is required");
        ColumnCondition newCondition = ColumnCondition
                .gt(Column.of(representation.getColumnField(name), getValue(value)));
        appendCondition(newCondition);
    }

    protected void ltImpl(Number value) {
        requireNonNull(value, "value is required");
        ColumnCondition newCondition = ColumnCondition
                .lt(Column.of(representation.getColumnField(name), getValue(value)));
        appendCondition(newCondition);
    }

    protected void lteImpl(Number value) {
        requireNonNull(value, "value is required");
        ColumnCondition newCondition = ColumnCondition
                .lte(Column.of(representation.getColumnField(name), getValue(value)));
        appendCondition(newCondition);
    }



    protected Object getValue(Object value) {
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
