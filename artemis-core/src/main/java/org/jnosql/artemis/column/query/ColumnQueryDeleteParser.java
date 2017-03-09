/*
 * Copyright 2017 Otavio Santana and others
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jnosql.artemis.column.query;

import org.jnosql.artemis.DynamicQueryException;
import org.jnosql.artemis.reflection.ClassRepresentation;
import org.jnosql.diana.api.Condition;
import org.jnosql.diana.api.column.ColumnCondition;
import org.jnosql.diana.api.column.ColumnDeleteQuery;

/**
 * Class the returns a {@link ColumnDeleteQuery}
 * on {@link ColumnCrudRepositoryProxy}
 */
public class ColumnQueryDeleteParser {

    private static final String PREFIX = "deleteBy";


    public ColumnDeleteQuery parse(String methodName, Object[] args, ClassRepresentation classRepresentation) {
        ColumnDeleteQuery columnDeleteQuery = ColumnDeleteQuery.of(classRepresentation.getName());
        String[] tokens = methodName.replace(PREFIX, ColumnQueryParserUtil.EMPTY).split("(?=AND|OR|OrderBy)");
        String className = classRepresentation.getClassInstance().getName();

        int index = 0;
        for (String token : tokens) {
            if (token.startsWith(ColumnQueryParserUtil.AND)) {
                index = and(args, columnDeleteQuery, index, token, methodName);
            } else if (token.startsWith(ColumnQueryParserUtil.OR)) {
                index = or(args, columnDeleteQuery, index, token, methodName);
            } else {
                ColumnCondition condition = ColumnQueryParserUtil.toCondition(token, index, args, methodName);
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

    private int or(Object[] args, ColumnDeleteQuery columnQuery, int index, String token, String methodName) {
        String field = token.replace(ColumnQueryParserUtil.OR, ColumnQueryParserUtil.EMPTY);
        ColumnCondition condition = ColumnQueryParserUtil.toCondition(field, index, args, methodName);
        columnQuery.or(condition);
        if (Condition.BETWEEN.equals(condition.getCondition())) {
            return index + 2;
        } else {
            return ++index;
        }
    }

    private int and(Object[] args, ColumnDeleteQuery columnQuery, int index, String token,
                    String methodName) {
        String field = token.replace(ColumnQueryParserUtil.AND, ColumnQueryParserUtil.EMPTY);
        ColumnCondition condition = ColumnQueryParserUtil.toCondition(field, index, args, methodName);
        columnQuery.and(condition);
        if (Condition.BETWEEN.equals(condition.getCondition())) {
            return index + 2;
        } else {
            return ++index;
        }

    }


}
