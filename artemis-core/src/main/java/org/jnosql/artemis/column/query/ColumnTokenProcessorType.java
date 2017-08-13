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

import org.jnosql.artemis.DynamicQueryException;
import org.jnosql.artemis.reflection.ClassRepresentation;
import org.jnosql.diana.api.column.Column;
import org.jnosql.diana.api.column.ColumnCondition;

import java.util.Arrays;
import java.util.stream.Stream;

import static org.jnosql.artemis.column.query.ColumnQueryParserUtil.EMPTY;

enum ColumnTokenProcessorType implements ColumnTokenProcessor {

    BETWEEN("Between") {
        @Override
        public ColumnCondition toCondition(String token, int index, Object[] args, String methodName, ClassRepresentation representation) {
            checkContents(index, args.length, 2, methodName);
            String name = getName(token, representation).replace(this.type, EMPTY);
            return ColumnCondition.between(Column.of(name, Arrays.asList(args[index], args[++index])));
        }
    },
    LESS_THAN("LessThan") {
        @Override
        public ColumnCondition toCondition(String token, int index, Object[] args, String methodName, ClassRepresentation representation) {
            checkContents(index, args.length, 1, methodName);
            String name = getName(token, representation).replace(this.type, EMPTY);
            return ColumnCondition.lt(Column.of(name, args[index]));
        }
    },
    GREATER_THAN("GreaterThan") {
        @Override
        public ColumnCondition toCondition(String token, int index, Object[] args, String methodName, ClassRepresentation representation) {
            checkContents(index, args.length, 1, methodName);
            String name = getName(token, representation).replace(this.type, EMPTY);
            return ColumnCondition.gt(Column.of(name, args[index]));
        }
    },
    LESS_THAN_EQUAL("LessEqualThan") {
        @Override
        public ColumnCondition toCondition(String token, int index, Object[] args, String methodName, ClassRepresentation representation) {
            checkContents(index, args.length, 1, methodName);
            String name = getName(token, representation).replace(this.type, EMPTY);
            return ColumnCondition.lte(Column.of(name, args[index]));
        }
    },
    GREATER_THAN_EQUAL("GreaterEqualThan") {
        @Override
        public ColumnCondition toCondition(String token, int index, Object[] args, String methodName, ClassRepresentation representation) {
            checkContents(index, args.length, 1, methodName);
            String name = getName(token, representation).replace(this.type, EMPTY);
            return ColumnCondition.gte(Column.of(name, args[index]));
        }
    },
    LIKE("Like") {
        @Override
        public ColumnCondition toCondition(String token, int index, Object[] args, String methodName, ClassRepresentation representation) {
            checkContents(index, args.length, 1, methodName);
            String name = getName(token, representation).replace(this.type, EMPTY);
            return ColumnCondition.like(Column.of(name, args[index]));
        }
    },DEFAULT("") {
        @Override
        public ColumnCondition toCondition(String token, int index, Object[] args, String methodName, ClassRepresentation representation) {
            checkContents(index, args.length, 1, methodName);
            String name = getName(token, representation);
            return ColumnCondition.eq(Column.of(name, args[index]));
        }
    };

    private final String type;

    ColumnTokenProcessorType(String type){
        this.type = type;
    }

    public String getType() {
        return type;
    }


    static ColumnTokenProcessorType of(String token) {
        return Stream.of(ColumnTokenProcessorType.values())
                .filter(t -> token.contains(t.getType()))
                .findFirst().orElse(DEFAULT);
    }

    private static void checkContents(int index, int argSize, int required, String method) {
        if ((index + required) <= argSize) {
            return;
        }
        throw new DynamicQueryException(String.format("There is a missed argument in the method %s",
                method));
    }

    private static String getName(String token, ClassRepresentation representation) {
        return representation.getColumnField(String.valueOf(Character.toLowerCase(token.charAt(0)))
                .concat(token.substring(1)));
    }

}
