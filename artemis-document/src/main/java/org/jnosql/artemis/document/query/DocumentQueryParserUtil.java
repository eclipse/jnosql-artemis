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
package org.jnosql.artemis.document.query;

import org.jnosql.artemis.reflection.ClassRepresentation;
import org.jnosql.diana.api.Condition;
import org.jnosql.diana.api.document.DocumentCondition;

/**
 * Utilitarian class to dynamic query from method on interface
 * {@link DocumentQueryDeleteParser} and {@link DocumentQueryParser}
 */
public final class DocumentQueryParserUtil {

    static final String AND = "And";
    static final String OR = "Or";
    static final String EMPTY = "";

    static final String ORDER_BY = "OrderBy";

    private DocumentQueryParserUtil() {
    }

    public static DocumentCondition toCondition(String token,
                                                int index,
                                                Object[] args,
                                                String methodName,
                                                ClassRepresentation representation) {

        DocumentTokenProcessor processor = DocumentTokenProcessorType.of(token);
        return processor.process(token, index, args, methodName, representation);

    }

    static ConditionResult or(Object[] args, int index,
                              String token,
                              String methodName,
                              ClassRepresentation representation,
                              DocumentCondition queryCondition) {
        String field = token.replace(DocumentQueryParserUtil.OR, DocumentQueryParserUtil.EMPTY);
        DocumentCondition conditionResult = toCondition(field, index, args, methodName, representation);
        DocumentCondition condition = null;

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
                               DocumentCondition queryCondition) {
        String field = token.replace(DocumentQueryParserUtil.AND, DocumentQueryParserUtil.EMPTY);
        DocumentCondition conditionResult = toCondition(field, index, args, methodName, representation);

        DocumentCondition condition = null;

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

        private final DocumentCondition condition;

        public ConditionResult(int index, DocumentCondition condition) {
            this.index = index;
            this.condition = condition;
        }

        public int getIndex() {
            return index;
        }

        public DocumentCondition getCondition() {
            return condition;
        }
    }
}
