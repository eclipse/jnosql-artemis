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
import org.jnosql.diana.api.Condition;
import org.jnosql.diana.api.column.ColumnCondition;
import org.jnosql.diana.api.column.ColumnDeleteQuery;

import static org.jnosql.artemis.column.query.ColumnQueryParserUtil.toCondition;

/**
 * Class the returns a {@link ColumnDeleteQuery}
 * on {@link ColumnRepositoryProxy}
 */
public class ColumnQueryDeleteParser {

    private static final String PREFIX = "deleteBy";
    private static final String TOKENIZER = "(?=And|OrderBy|Or)";


    public ColumnDeleteQuery parse(String methodName, Object[] args, ClassRepresentation representation) {
        ColumnDeleteQuery columnDeleteQuery = ColumnDeleteQuery.of(representation.getName());
        String[] tokens = methodName.replace(PREFIX, ColumnQueryParserUtil.EMPTY).split(TOKENIZER);
        String className = representation.getClassInstance().getName();

        int index = 0;
        for (String token : tokens) {
            if (token.startsWith(ColumnQueryParserUtil.AND)) {
                index = and(args, columnDeleteQuery, index, token, methodName, representation);
            } else if (token.startsWith(ColumnQueryParserUtil.OR)) {
                index = or(args, columnDeleteQuery, index, token, methodName, representation);
            } else {
                ColumnCondition condition = toCondition(token, index, args, methodName, representation);
                columnDeleteQuery.and(condition);
                index++;
            }
        }

        return columnDeleteQuery;
    }


    private void checkContents(int index, int argSize, int required, String method) {
        if ((index + required) <= argSize) {
            return;
        }
        throw new DynamicQueryException(String.format("There is a missed argument in the method %s",
                method));
    }

    private int or(Object[] args, ColumnDeleteQuery columnQuery, int index, String token,
                   String methodName,
                   ClassRepresentation representation) {
        String field = token.replace(ColumnQueryParserUtil.OR, ColumnQueryParserUtil.EMPTY);
        ColumnCondition condition = toCondition(field, index, args, methodName, representation);
        columnQuery.or(condition);
        if (Condition.BETWEEN.equals(condition.getCondition())) {
            return index + 2;
        } else {
            return ++index;
        }
    }

    private int and(Object[] args, ColumnDeleteQuery columnQuery, int index, String token,
                    String methodName, ClassRepresentation representation) {
        String field = token.replace(ColumnQueryParserUtil.AND, ColumnQueryParserUtil.EMPTY);
        ColumnCondition condition = toCondition(field, index, args, methodName, representation);
        columnQuery.and(condition);
        if (Condition.BETWEEN.equals(condition.getCondition())) {
            return index + 2;
        } else {
            return ++index;
        }

    }


}
