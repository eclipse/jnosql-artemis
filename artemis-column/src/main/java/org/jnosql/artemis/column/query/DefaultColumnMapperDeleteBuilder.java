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
import org.jnosql.diana.api.column.Column;
import org.jnosql.diana.api.column.ColumnCondition;
import org.jnosql.diana.api.column.ColumnDeleteQuery;
import org.jnosql.diana.api.column.query.ColumnDeleteFrom;
import org.jnosql.diana.api.column.query.ColumnDeleteNameCondition;
import org.jnosql.diana.api.column.query.ColumnDeleteNotCondition;
import org.jnosql.diana.api.column.query.ColumnDeleteWhere;
import org.jnosql.diana.api.column.query.ColumnDeleteWhereName;

import static java.util.Arrays.asList;
import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;

class DefaultColumnMapperDeleteBuilder implements ColumnDeleteFrom,
        ColumnDeleteWhere, ColumnDeleteWhereName, ColumnDeleteNotCondition {

    private final ClassRepresentation representation;
    private final Converters converters;

    private String columnFamily;

    private ColumnCondition condition;

    private String name;

    private boolean negate;

    private boolean and;

    DefaultColumnMapperDeleteBuilder(ClassRepresentation representation, Converters converters) {
        this.representation = representation;
        this.converters = converters;
    }

    @Override
    public ColumnDeleteWhereName where(String name) throws NullPointerException {
        requireNonNull(name, "name is required");
        this.name = name;
        return this;
    }


    @Override
    public ColumnDeleteNameCondition and(String name) throws NullPointerException {
        requireNonNull(name, "name is required");
        this.name = name;
        this.and = true;
        return this;
    }

    @Override
    public ColumnDeleteNameCondition or(String name) throws NullPointerException {
        requireNonNull(name, "name is required");
        this.name = name;
        this.and = false;
        return this;
    }


    @Override
    public ColumnDeleteNotCondition not() {
        this.negate = true;
        return this;
    }

    @Override
    public <T> ColumnDeleteWhere eq(T value) throws NullPointerException {
        requireNonNull(value, "value is required");
        ColumnCondition newCondition = ColumnCondition.eq(Column.of(name, value));
        return appendCondition(newCondition);
    }

    @Override
    public ColumnDeleteWhere like(String value) throws NullPointerException {
        requireNonNull(value, "value is required");
        ColumnCondition newCondition = ColumnCondition.like(Column.of(name, value));
        return appendCondition(newCondition);
    }

    @Override
    public ColumnDeleteWhere gt(Number value) throws NullPointerException {
        requireNonNull(value, "value is required");
        ColumnCondition newCondition = ColumnCondition.gt(Column.of(name, value));
        return appendCondition(newCondition);
    }

    @Override
    public ColumnDeleteWhere gte(Number value) throws NullPointerException {
        requireNonNull(value, "value is required");
        ColumnCondition newCondition = ColumnCondition.gte(Column.of(name, value));
        return appendCondition(newCondition);
    }

    @Override
    public ColumnDeleteWhere lt(Number value) throws NullPointerException {
        requireNonNull(value, "value is required");
        ColumnCondition newCondition = ColumnCondition.lt(Column.of(name, value));
        return appendCondition(newCondition);
    }

    @Override
    public ColumnDeleteWhere lte(Number value) throws NullPointerException {
        requireNonNull(value, "value is required");
        ColumnCondition newCondition = ColumnCondition.lte(Column.of(name, value));
        return appendCondition(newCondition);
    }

    @Override
    public ColumnDeleteWhere between(Number valueA, Number valueB) throws NullPointerException {
        requireNonNull(valueA, "valueA is required");
        requireNonNull(valueB, "valueB is required");
        ColumnCondition newCondition = ColumnCondition.between(Column.of(name, asList(valueA, valueB)));
        return appendCondition(newCondition);
    }

    @Override
    public <T> ColumnDeleteWhere in(Iterable<T> values) throws NullPointerException {
        requireNonNull(values, "values is required");
        ColumnCondition newCondition = ColumnCondition.in(Column.of(name, values));
        return appendCondition(newCondition);
    }

    private DefaultColumnMapperDeleteBuilder appendCondition(ColumnCondition newCondition) {
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

    @Override
    public ColumnDeleteQuery build() {
        return new ArtemisColumnDeleteQuery(columnFamily, condition);
    }
}
