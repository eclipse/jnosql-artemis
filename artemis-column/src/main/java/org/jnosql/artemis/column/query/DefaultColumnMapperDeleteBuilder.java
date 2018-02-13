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
import org.jnosql.diana.api.column.ColumnDeleteQuery;
import org.jnosql.diana.api.column.query.ColumnDeleteFrom;
import org.jnosql.diana.api.column.query.ColumnDeleteNameCondition;
import org.jnosql.diana.api.column.query.ColumnDeleteNotCondition;
import org.jnosql.diana.api.column.query.ColumnDeleteWhere;

import static java.util.Objects.requireNonNull;

class DefaultColumnMapperDeleteBuilder extends AbstractMapperQuery implements ColumnDeleteFrom,
        ColumnDeleteWhere, ColumnDeleteNameCondition, ColumnDeleteNotCondition {


    DefaultColumnMapperDeleteBuilder(ClassRepresentation representation, Converters converters) {
        super(representation, converters);
    }

    @Override
    public ColumnDeleteNameCondition where(String name) {
        requireNonNull(name, "name is required");
        this.name = name;
        return this;
    }


    @Override
    public ColumnDeleteNameCondition and(String name) {
        requireNonNull(name, "name is required");
        this.name = name;
        this.and = true;
        return this;
    }

    @Override
    public ColumnDeleteNameCondition or(String name) {
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
    public <T> ColumnDeleteWhere eq(T value) {
        eqImpl(value);
        return this;
    }

    @Override
    public ColumnDeleteWhere like(String value) {
        likeImpl(value);
        return this;
    }

    @Override
    public ColumnDeleteWhere gt(Number value) {
        gtImpl(value);
        return this;
    }

    @Override
    public ColumnDeleteWhere gte(Number value) {
        gteImpl(value);
        return this;
    }

    @Override
    public ColumnDeleteWhere lt(Number value) {
        ltImpl(value);
        return this;
    }

    @Override
    public ColumnDeleteWhere lte(Number value) {
        lteImpl(value);
        return this;
    }

    @Override
    public ColumnDeleteWhere between(Number valueA, Number valueB) {
        betweenImpl(valueA, valueB);
        return this;
    }


    @Override
    public <T> ColumnDeleteWhere in(Iterable<T> values) {
        inImpl(values);
        return this;
    }


    @Override
    public ColumnDeleteQuery build() {
        return new ArtemisColumnDeleteQuery(columnFamily, condition);
    }

}
