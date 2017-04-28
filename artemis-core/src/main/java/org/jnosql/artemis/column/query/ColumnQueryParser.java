/*
 * Copyright 2017 Otavio Santana and others
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jnosql.artemis.column.query;

import org.jnosql.artemis.Pagination;
import org.jnosql.artemis.reflection.ClassRepresentation;
import org.jnosql.diana.api.Condition;
import org.jnosql.diana.api.Sort;
import org.jnosql.diana.api.column.ColumnCondition;
import org.jnosql.diana.api.column.ColumnQuery;

import java.util.logging.Logger;

import static org.jnosql.artemis.column.query.ColumnQueryParserUtil.toCondition;

/**
 * Class the returns a {@link ColumnQuery}
 * on {@link ColumnCrudRepositoryProxy}
 */
public class ColumnQueryParser {

    private static final Logger LOGGER = Logger.getLogger(ColumnQueryParser.class.getName());

    private static final String PREFIX = "findBy";
    private static final String TOKENIZER = "(?=And|OrderBy|Or)";


    public ColumnQuery parse(String methodName, Object[] args, ClassRepresentation classRepresentation) {
        ColumnQuery columnQuery = ColumnQuery.of(classRepresentation.getName());
        String[] tokens = methodName.replace(PREFIX, ColumnQueryParserUtil.EMPTY).split(TOKENIZER);
        String className = classRepresentation.getClassInstance().getName();

        int index = 0;
        for (String token : tokens) {
            if (token.startsWith(ColumnQueryParserUtil.AND)) {
                index = and(args, columnQuery, index, token, methodName, classRepresentation);
            } else if (token.startsWith(ColumnQueryParserUtil.ORDER_BY)) {
                sort(columnQuery, token, classRepresentation);
            } else if (token.startsWith(ColumnQueryParserUtil.OR)) {
                index = or(args, columnQuery, index, token, methodName, classRepresentation);
            } else {
                ColumnCondition condition = toCondition(token, index, args, methodName, classRepresentation);
                columnQuery.and(condition);
                index++;
            }
        }

        while (index < args.length) {
            Object value = args[index];
            if (Sort.class.isInstance(value)) {
                columnQuery.addSort(Sort.class.cast(value));
            } else if (Pagination.class.isInstance(value)) {
                Pagination pagination = Pagination.class.cast(value);
                columnQuery.withMaxResults(pagination.getMaxResults());
                columnQuery.withFirstResult(pagination.getFirstResult());
            } else {
                LOGGER.info(String.format("Ignoring parameter %s on  methodName %s class name %s arg-number: %d",
                        String.valueOf(value), methodName, className, index));
            }
            index++;
        }
        return columnQuery;
    }


    private int or(Object[] args, ColumnQuery columnQuery, int index,
                   String token, String methodName,
                   ClassRepresentation classRepresentation) {
        String field = token.replace(ColumnQueryParserUtil.OR, ColumnQueryParserUtil.EMPTY);
        ColumnCondition condition = toCondition(field, index, args, methodName, classRepresentation);
        columnQuery.or(condition);
        if (Condition.BETWEEN.equals(condition.getCondition())) {
            return index + 2;
        } else {
            return ++index;
        }
    }

    private int and(Object[] args, ColumnQuery columnQuery, int index, String token,
                    String methodName, ClassRepresentation classRepresentation) {
        String field = token.replace(ColumnQueryParserUtil.AND, ColumnQueryParserUtil.EMPTY);
        ColumnCondition condition = toCondition(field, index, args, methodName, classRepresentation);
        columnQuery.and(condition);
        if (Condition.BETWEEN.equals(condition.getCondition())) {
            return index + 2;
        } else {
            return ++index;
        }

    }

    private void sort(ColumnQuery columnQuery, String token, ClassRepresentation representation) {
        String field = token.replace(ColumnQueryParserUtil.ORDER_BY, ColumnQueryParserUtil.EMPTY);
        if (field.contains("Desc")) {
            columnQuery.addSort(Sort.of(getName(field.replace("Desc", ColumnQueryParserUtil.EMPTY),
                    representation), Sort.SortType.DESC));
        } else {
            columnQuery.addSort(Sort.of(getName(field.replace("Asc", ColumnQueryParserUtil.EMPTY),
                    representation), Sort.SortType.ASC));
        }
    }

    private String getName(String token, ClassRepresentation representation) {
        return representation.getColumnField(String.valueOf(Character.toLowerCase(token.charAt(0)))
                .concat(token.substring(1)));

    }

}
