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
import org.jnosql.diana.api.Sort;
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

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.requireNonNull;

class DefaultColumnMapperSelectBuilder extends AbstractMapperQuery implements ColumnFrom, ColumnLimit, ColumnStart,
        ColumnOrder, ColumnWhereName, ColumnNameCondition, ColumnNotCondition, ColumnNameOrder, ColumnWhere {


    private final List<Sort> sorts = new ArrayList<>();


    DefaultColumnMapperSelectBuilder(ClassRepresentation representation, Converters converters) {
        super(representation, converters);
    }


    @Override
    public ColumnNameCondition and(String name) {
        requireNonNull(name, "name is required");
        this.name = name;
        this.and = true;
        return this;
    }

    @Override
    public ColumnNameCondition or(String name) {
        requireNonNull(name, "name is required");
        this.name = name;
        this.and = false;
        return this;
    }

    @Override
    public ColumnWhereName where(String name) {
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
    public ColumnOrder orderBy(String name) {
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
    public <T> ColumnWhere eq(T value) {
        eqImpl(value);
        return this;
    }



    @Override
    public ColumnWhere like(String value) {
        likeImpl(value);
        return this;
    }


    @Override
    public ColumnWhere gt(Number value) {
        gtImpl(value);
        return this;
    }



    @Override
    public ColumnWhere gte(Number value) {
        gteImpl(value);
        return this;
    }

    @Override
    public ColumnWhere lt(Number value) {
        ltImpl(value);
        return this;
    }



    @Override
    public ColumnWhere lte(Number value) {
        lteImpl(value);
        return this;
    }



    @Override
    public ColumnWhere between(Number valueA, Number valueB) {
        betweenImpl(valueA, valueB);
         return this;
    }

    @Override
    public <T> ColumnWhere in(Iterable<T> values) {
        inImpl(values);
        return this;
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

}
