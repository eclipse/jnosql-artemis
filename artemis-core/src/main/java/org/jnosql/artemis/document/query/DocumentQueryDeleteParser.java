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
package org.jnosql.artemis.document.query;

import org.jnosql.artemis.DynamicQueryException;
import org.jnosql.artemis.reflection.ClassRepresentation;
import org.jnosql.diana.api.Condition;
import org.jnosql.diana.api.document.DocumentCondition;
import org.jnosql.diana.api.document.DocumentDeleteQuery;

/**
 * Class the returns a {@link DocumentDeleteQuery}
 * on {@link DocumentCrudRepositoryProxy}
 */
public class DocumentQueryDeleteParser {

    private static final String PREFIX = "deleteBy";
    private static final String TOKENIZER = "(?=And|OrderBy|Or)";


    public DocumentDeleteQuery parse(String methodName, Object[] args, ClassRepresentation classRepresentation) {
        DocumentDeleteQuery documentQuery = DocumentDeleteQuery.of(classRepresentation.getName());
        String[] tokens = methodName.replace(PREFIX, DocumentQueryParserUtil.EMPTY).split(TOKENIZER);
        String className = classRepresentation.getClassInstance().getName();

        int index = 0;
        for (String token : tokens) {
            if (token.startsWith(DocumentQueryParserUtil.AND)) {
                index = and(args, documentQuery, index, token, methodName);
            } else if (token.startsWith(DocumentQueryParserUtil.OR)) {
                index = or(args, documentQuery, index, token, methodName);
            } else {
                DocumentCondition condition = DocumentQueryParserUtil.toCondition(token, index, args, methodName);
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

    private int or(Object[] args, DocumentDeleteQuery documentQuery, int index, String token, String methodName) {
        String field = token.replace(DocumentQueryParserUtil.OR, DocumentQueryParserUtil.EMPTY);
        DocumentCondition condition = DocumentQueryParserUtil.toCondition(field, index, args, methodName);
        documentQuery.or(condition);
        if (Condition.BETWEEN.equals(condition.getCondition())) {
            return index + 2;
        } else {
            return ++index;
        }
    }

    private int and(Object[] args, DocumentDeleteQuery documentQuery, int index, String token,
                    String methodName) {
        String field = token.replace(DocumentQueryParserUtil.AND, DocumentQueryParserUtil.EMPTY);
        DocumentCondition condition = DocumentQueryParserUtil.toCondition(field, index, args, methodName);
        documentQuery.and(condition);
        if (Condition.BETWEEN.equals(condition.getCondition())) {
            return index + 2;
        } else {
            return ++index;
        }

    }


}
