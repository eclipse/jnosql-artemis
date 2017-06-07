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
import org.jnosql.diana.api.document.DocumentCondition;
import org.jnosql.diana.api.document.DocumentDeleteQuery;

import static org.jnosql.artemis.document.query.DocumentQueryParserUtil.toCondition;

/**
 * Class the returns a {@link DocumentDeleteQuery}
 * on {@link DocumentRepositoryProxy}
 */
public class DocumentQueryDeleteParser {

    private static final String PREFIX = "deleteBy";
    private static final String TOKENIZER = "(?=And|OrderBy|Or)";


    public DocumentDeleteQuery parse(String methodName, Object[] args, ClassRepresentation representation) {
        DocumentDeleteQuery documentQuery = DocumentDeleteQuery.of(representation.getName());
        String[] tokens = methodName.replace(PREFIX, DocumentQueryParserUtil.EMPTY).split(TOKENIZER);
        String className = representation.getClassInstance().getName();

        int index = 0;
        for (String token : tokens) {
            if (token.startsWith(DocumentQueryParserUtil.AND)) {
                index = and(args, documentQuery, index, token, methodName, representation);
            } else if (token.startsWith(DocumentQueryParserUtil.OR)) {
                index = or(args, documentQuery, index, token, methodName, representation);
            } else {
                DocumentCondition condition = toCondition(token, index, args, methodName, representation);
                documentQuery.and(condition);
                index++;
            }
        }

        return documentQuery;
    }


    private void checkContents(int index, int argSize, int required, String method) {
        if ((index + required) <= argSize) {
            return;
        }
        throw new DynamicQueryException(String.format("There is a missed argument in the method %s",
                method));
    }

    private int or(Object[] args, DocumentDeleteQuery documentQuery, int index, String token,
                   String methodName, ClassRepresentation representation) {

        String field = token.replace(DocumentQueryParserUtil.OR, DocumentQueryParserUtil.EMPTY);
        DocumentCondition condition = toCondition(field, index, args, methodName, representation);
        documentQuery.or(condition);
        if (Condition.BETWEEN.equals(condition.getCondition())) {
            return index + 2;
        } else {
            return ++index;
        }
    }

    private int and(Object[] args, DocumentDeleteQuery documentQuery, int index, String token,
                    String methodName, ClassRepresentation representation) {
        String field = token.replace(DocumentQueryParserUtil.AND, DocumentQueryParserUtil.EMPTY);
        DocumentCondition condition = toCondition(field, index, args, methodName, representation);
        documentQuery.and(condition);
        if (Condition.BETWEEN.equals(condition.getCondition())) {
            return index + 2;
        } else {
            return ++index;
        }

    }


}
