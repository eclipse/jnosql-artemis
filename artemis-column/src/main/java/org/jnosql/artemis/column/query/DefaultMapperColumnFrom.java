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

import org.jnosql.artemis.AttributeConverter;
import org.jnosql.artemis.Converters;
import org.jnosql.artemis.reflection.ClassRepresentation;
import org.jnosql.artemis.reflection.FieldRepresentation;
import org.jnosql.diana.api.Sort;
import org.jnosql.diana.api.Value;
import org.jnosql.diana.api.column.Column;
import org.jnosql.diana.api.column.ColumnCondition;
import org.jnosql.diana.api.column.ColumnQuery;
import org.jnosql.diana.api.column.query.ColumnFrom;
import org.jnosql.diana.api.column.query.ColumnLimit;
import org.jnosql.diana.api.column.query.ColumnNameCondition;
import org.jnosql.diana.api.column.query.ColumnNameOrder;
import org.jnosql.diana.api.column.query.ColumnNotCondition;
import org.jnosql.diana.api.column.query.ColumnOrder;
import org.jnosql.diana.api.column.query.ColumnStart;
import org.jnosql.diana.api.column.query.ColumnWhere;
import org.jnosql.diana.api.column.query.ColumnWhereName;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static java.util.Arrays.asList;
import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;

class DefaultMapperColumnFrom implements ColumnFrom, ColumnLimit, ColumnStart,
        ColumnOrder, ColumnWhereName, ColumnNameCondition, ColumnNotCondition, ColumnNameOrder, ColumnWhere {

    private final ClassRepresentation representation;

    private final Converters converters;

    private final String columnFamily;

    private ColumnCondition condition;

    private long start;

    private long limit;

    private final List<Sort> sorts = new ArrayList<>();

    private String name;

    private boolean negate;

    private boolean and;

    DefaultMapperColumnFrom(ClassRepresentation representation, Converters converters) {
        this.representation = representation;
        this.converters = converters;
        this.columnFamily = representation.getName();
    }


    @Override
    public ColumnNameCondition and(String name) throws NullPointerException {
        requireNonNull(name, "name is required");
        this.name = name;
        this.and = true;
        return this;
    }

    @Override
    public ColumnNameCondition or(String name) throws NullPointerException {
        requireNonNull(name, "name is required");
        this.name = name;
        this.and = false;
        return this;
    }

    @Override
    public ColumnWhereName where(String name) throws NullPointerException {
        requireNonNull(name, "name is required");
        this.name = name;
        return this;
    }

    @Override
    public ColumnStart start(long start) {
        this.start = start;
        return this;
    }

    @Override
    public ColumnLimit limit(long limit) {
        this.limit = limit;
        return this;
    }

    @Override
    public ColumnOrder orderBy(String name) throws NullPointerException {
        requireNonNull(name, "name is required");
        this.name = name;
        return this;
    }


    @Override
    public ColumnNotCondition not() {
        this.negate = true;
        return this;
    }

    @Override
    public <T> ColumnWhere eq(T value) throws NullPointerException {
        requireNonNull(value, "value is required");

        ColumnCondition newCondition = ColumnCondition
                .eq(Column.of(representation.getColumnField(name), getValue(value)));
        return appendCondition(newCondition);
    }

    @Override
    public ColumnWhere like(String value) throws NullPointerException {
        requireNonNull(value, "value is required");
        ColumnCondition newCondition = ColumnCondition
                .like(Column.of(representation.getColumnField(name), getValue(value)));
        return appendCondition(newCondition);
    }

    @Override
    public ColumnWhere gt(Number value) throws NullPointerException {
        requireNonNull(value, "value is required");
        ColumnCondition newCondition = ColumnCondition
                .gt(Column.of(representation.getColumnField(name), getValue(value)));
        return appendCondition(newCondition);
    }

    @Override
    public ColumnWhere gte(Number value) throws NullPointerException {
        requireNonNull(value, "value is required");
        ColumnCondition newCondition = ColumnCondition
                .gte(Column.of(representation.getColumnField(name), getValue(value)));
        return appendCondition(newCondition);
    }

    @Override
    public ColumnWhere lt(Number value) throws NullPointerException {
        requireNonNull(value, "value is required");
        ColumnCondition newCondition = ColumnCondition
                .lt(Column.of(representation.getColumnField(name), getValue(value)));
        return appendCondition(newCondition);
    }

    @Override
    public ColumnWhere lte(Number value) throws NullPointerException {
        requireNonNull(value, "value is required");
        ColumnCondition newCondition = ColumnCondition
                .lte(Column.of(representation.getColumnField(name), getValue(value)));
        return appendCondition(newCondition);
    }

    @Override
    public ColumnWhere between(Number valueA, Number valueB) throws NullPointerException {
        requireNonNull(valueA, "valueA is required");
        requireNonNull(valueB, "valueB is required");
        ColumnCondition newCondition = ColumnCondition
                .between(Column.of(representation.getColumnField(name), asList(getValue(valueA), getValue(valueB))));
        return appendCondition(newCondition);
    }

    @Override
    public <T> ColumnWhere in(Iterable<T> values) throws NullPointerException {
        requireNonNull(values, "values is required");
        List<Object> convertedValues = StreamSupport.stream(values.spliterator(), false)
                .map(this::getValue).collect(toList());
        ColumnCondition newCondition = ColumnCondition
                .in(Column.of(representation.getColumnField(name), convertedValues));
        return appendCondition(newCondition);
    }

    @Override
    public ColumnNameOrder asc() {
        this.sorts.add(Sort.of(representation.getColumnField(name), Sort.SortType.ASC));
        return this;
    }

    @Override
    public ColumnNameOrder desc() {
        this.sorts.add(Sort.of(representation.getColumnField(name), Sort.SortType.DESC));
        return this;
    }


    @Override
    public ColumnQuery build() {
        return new ArtemisColumnQuery(sorts, limit, start, condition, columnFamily);
    }

    private ColumnWhere appendCondition(ColumnCondition newCondition) {
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
        return this;
    }


    private Object getValue(Object value) {
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
