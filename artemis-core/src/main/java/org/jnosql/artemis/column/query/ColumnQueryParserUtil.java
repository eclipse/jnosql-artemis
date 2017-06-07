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

/**
 * Utilitarian class to dynamic query from method on interface
 * {@link ColumnQueryDeleteParser} and {@link ColumnQueryParser}
 */
final class ColumnQueryParserUtil {

    static final String AND = "And";
    static final String OR = "Or";
    static final String EMPTY = "";

    static final String ORDER_BY = "OrderBy";
    private static final String BETWEEN = "Between";
    private static final String LESS_THAN = "LessThan";
    private static final String GREATER_THAN = "GreaterThan";
    private static final String LESS_THAN_EQUAL = "LessEqualThan";
    private static final String GREATER_THAN_EQUAL = "GreaterEqualThan";
    private static final String LIKE = "Like";

    private ColumnQueryParserUtil() {
    }

    static ColumnCondition toCondition(String token,
                                       int index,
                                       Object[] args,
                                       String methodName,
                                       ClassRepresentation representation) {

        boolean containsBetween = token.contains(BETWEEN);

        if (containsBetween) {
            checkContents(index, args.length, 2, methodName);
            String name = getName(token, representation).replace(BETWEEN, EMPTY);
            return ColumnCondition.between(Column.of(name, Arrays.asList(args[index], args[++index])));
        }

        checkContents(index, args.length, 1, methodName);

        if (token.contains(LESS_THAN)) {
            String name = getName(token, representation).replace(LESS_THAN, EMPTY);
            return ColumnCondition.lt(Column.of(name, args[index]));
        }

        if (token.contains(GREATER_THAN)) {
            String name = getName(token, representation).replace(GREATER_THAN, EMPTY);
            return ColumnCondition.gt(Column.of(name, args[index]));
        }

        if (token.contains(LESS_THAN_EQUAL)) {
            String name = getName(token, representation).replace(LESS_THAN_EQUAL, EMPTY);
            return ColumnCondition.lte(Column.of(name, args[index]));
        }

        if (token.contains(GREATER_THAN_EQUAL)) {
            String name = getName(token, representation).replace(GREATER_THAN_EQUAL, EMPTY);
            return ColumnCondition.gte(Column.of(name, args[index]));
        }

        if (token.contains(LIKE)) {
            String name = getName(token, representation).replace(LIKE, EMPTY);
            return ColumnCondition.like(Column.of(name, args[index]));
        }

        String name = getName(token, representation);
        return ColumnCondition.eq(Column.of(name, args[index]));
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
