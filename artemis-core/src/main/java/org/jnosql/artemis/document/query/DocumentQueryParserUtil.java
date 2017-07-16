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

import org.jnosql.artemis.DynamicQueryException;
import org.jnosql.artemis.reflection.ClassRepresentation;
import org.jnosql.diana.api.Condition;
import org.jnosql.diana.api.document.Document;
import org.jnosql.diana.api.document.DocumentCondition;

import java.util.Arrays;

/**
 * Utilitarian class to dynamic query from method on interface
 * {@link DocumentQueryDeleteParser} and {@link DocumentQueryParser}
 */
public final class DocumentQueryParserUtil {

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

    private DocumentQueryParserUtil() {
    }

    public static DocumentCondition toCondition(String token,
                                                int index,
                                                Object[] args,
                                                String methodName,
                                                ClassRepresentation representation) {

        boolean containsBetween = token.contains(BETWEEN);

        if (containsBetween) {
            checkContents(index, args.length, 2, methodName);
        } else {
            checkContents(index, args.length, 1, methodName);
        }

        if (containsBetween) {

            String name = getName(token, representation).replace(BETWEEN, EMPTY);
            return DocumentCondition.between(Document.of(name, Arrays.asList(args[index], args[++index])));

        } else if (token.contains(LESS_THAN)) {
            String name = getName(token, representation).replace(LESS_THAN, EMPTY);
            return DocumentCondition.lt(Document.of(name, args[index]));
        } else if (token.contains(GREATER_THAN)) {
            String name = getName(token, representation).replace(GREATER_THAN, EMPTY);
            return DocumentCondition.gt(Document.of(name, args[index]));
        } else if (token.contains(LESS_THAN_EQUAL)) {
            String name = getName(token, representation).replace(LESS_THAN_EQUAL, EMPTY);
            return DocumentCondition.lte(Document.of(name, args[index]));
        } else if (token.contains(GREATER_THAN_EQUAL)) {
            String name = getName(token, representation).replace(GREATER_THAN_EQUAL, EMPTY);
            return DocumentCondition.gte(Document.of(name, args[index]));
        } else if (token.contains(LIKE)) {
            String name = getName(token, representation).replace(LIKE, EMPTY);
            return DocumentCondition.like(Document.of(name, args[index]));
        }
        String name = getName(token, representation);
        return DocumentCondition.eq(Document.of(name, args[index]));

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
