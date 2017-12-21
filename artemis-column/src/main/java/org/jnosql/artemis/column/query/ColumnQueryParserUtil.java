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
import org.jnosql.diana.api.Condition;
import org.jnosql.diana.api.column.ColumnCondition;

/**
 * Utilitarian class to dynamic query from method on interface
 * {@link ColumnQueryDeleteParser} and {@link ColumnQueryParser}
 */
final class ColumnQueryParserUtil {

    static final String AND = "And";
    static final String OR = "Or";
    static final String EMPTY = "";

    static final String ORDER_BY = "OrderBy";

    private ColumnQueryParserUtil() {
    }

    static ColumnCondition toCondition(String token,
                                       int index,
                                       Object[] args,
                                       String methodName,
                                       ClassRepresentation representation, Converters converters) {

        ColumnTokenProcessor processor = ColumnTokenProcessorType.of(token);
        return processor.process(token, index, args, methodName, representation, converters);

    }


    static ConditionResult or(Object[] args, int index,
                              String token,
                              String methodName,
                              ClassRepresentation representation,
                              ColumnCondition queryCondition, Converters converters) {

        String field = token.replace(ColumnQueryParserUtil.OR, ColumnQueryParserUtil.EMPTY);
        ColumnCondition conditionResult = toCondition(field, index, args, methodName, representation, converters);
        ColumnCondition condition = null;

        if (queryCondition == null) {
            condition = conditionResult;
        } else {
            condition = queryCondition.or(conditionResult);
        }
        if (Condition.BETWEEN.equals(condition.getCondition())) {
            return new ConditionResult(index + 2, condition);
        } else {
            return new ConditionResult(++index, condition);
        }
    }

    static ConditionResult and(Object[] args,
                               int index,
                               String token,
                               String methodName,
                               ClassRepresentation representation,
                               ColumnCondition queryCondition, Converters converters) {
        String field = token.replace(ColumnQueryParserUtil.AND, ColumnQueryParserUtil.EMPTY);
        ColumnCondition conditionResult = toCondition(field, index, args, methodName, representation, converters);

        ColumnCondition condition = null;

        if (queryCondition == null) {
            condition = conditionResult;
        } else {
            condition = queryCondition.and(conditionResult);
        }

        if (Condition.BETWEEN.equals(condition.getCondition())) {
            return new ConditionResult(index + 2, condition);
        } else {
            return new ConditionResult(++index, condition);
        }
    }


    static class ConditionResult {

        private final int index;

        private final ColumnCondition condition;

        public ConditionResult(int index, ColumnCondition condition) {
            this.index = index;
            this.condition = condition;
        }

        public int getIndex() {
            return index;
        }

        public ColumnCondition getCondition() {
            return condition;
        }
    }
}
