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

import org.jnosql.artemis.Pagination;
import org.jnosql.artemis.reflection.ClassRepresentation;
import org.jnosql.diana.api.Sort;
import org.jnosql.diana.api.column.ColumnCondition;
import org.jnosql.diana.api.column.ColumnQuery;
import org.jnosql.diana.api.column.query.ColumnFrom;

import javax.enterprise.context.ApplicationScoped;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static java.util.Objects.requireNonNull;
import static org.jnosql.artemis.column.query.ColumnQueryParserUtil.and;
import static org.jnosql.artemis.column.query.ColumnQueryParserUtil.or;
import static org.jnosql.artemis.column.query.ColumnQueryParserUtil.toCondition;
import static org.jnosql.diana.api.column.query.ColumnQueryBuilder.select;

@ApplicationScoped
class DefaultColumnQueryParser implements ColumnQueryParser {

    private static final Logger LOGGER = Logger.getLogger(DefaultColumnQueryParser.class.getName());

    private static final String PREFIX = "findBy";
    private static final String TOKENIZER = "(?=And|OrderBy|Or)";


    public ColumnQuery parse(Method method, Object[] args, ClassRepresentation representation) {

        requireNonNull(method, "method is required");
        requireNonNull(args, "args is required");
        requireNonNull(representation, "representation is required");
        
        String methodName = method.getName();
        ColumnCondition condition = null;
        String[] tokens = methodName.replace(PREFIX, ColumnQueryParserUtil.EMPTY).split(TOKENIZER);
        String className = representation.getClassInstance().getName();
        List<Sort> sorts = new ArrayList<>();
        long limit = 0;
        long start = 0;

        int index = 0;
        for (String token : tokens) {
            if (token.startsWith(ColumnQueryParserUtil.AND)) {

                ColumnQueryParserUtil.ConditionResult result = and(args, index, token, methodName, representation, condition);
                condition = result.getCondition();
                index = result.getIndex();
            } else if (token.startsWith(ColumnQueryParserUtil.ORDER_BY)) {
                sort(sorts, token, representation);
            } else if (token.startsWith(ColumnQueryParserUtil.OR)) {
                ColumnQueryParserUtil.ConditionResult result = or(args, index, token, methodName, representation, condition);
                condition = result.getCondition();
                index = result.getIndex();
            } else {
                condition = toCondition(token, index, args, methodName, representation);
                index++;
            }
        }

        while (index < args.length) {
            Object value = args[index];
            if (Sort.class.isInstance(value)) {
                sorts.add(Sort.class.cast(value));
            } else if (Pagination.class.isInstance(value)) {
                Pagination pagination = Pagination.class.cast(value);
                limit = pagination.getMaxResults();
                start = pagination.getFirstResult();
            } else {
                LOGGER.info(String.format("Ignoring parameter %s on  methodName %s class name %s arg-number: %d",
                        String.valueOf(value), methodName, className, index));
            }
            index++;
        }

        ColumnFrom from = select().from(representation.getName());
        if (condition == null) {
            sorts.forEach(from::orderBy);
            return from.limit(limit).start(start).build();
        } else {
            from.where(condition);
            sorts.forEach(from::orderBy);
            return from.start(start).limit(limit).build();
        }
    }


    private void sort(List<Sort> sorts, String token, ClassRepresentation representation) {
        String field = token.replace(ColumnQueryParserUtil.ORDER_BY, ColumnQueryParserUtil.EMPTY);
        if (field.contains("Desc")) {
            sorts.add(Sort.of(getName(field.replace("Desc", ColumnQueryParserUtil.EMPTY),
                    representation), Sort.SortType.DESC));
        } else {
            sorts.add(Sort.of(getName(field.replace("Asc", ColumnQueryParserUtil.EMPTY),
                    representation), Sort.SortType.ASC));
        }
    }

    private String getName(String token, ClassRepresentation representation) {
        return representation.getColumnField(String.valueOf(Character.toLowerCase(token.charAt(0)))
                .concat(token.substring(1)));

    }
}
