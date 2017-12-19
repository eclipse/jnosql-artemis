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

import org.jnosql.artemis.document.query.DocumentQueryParserUtil.ConditionResult;
import org.jnosql.artemis.reflection.ClassRepresentation;
import org.jnosql.diana.api.document.DocumentCondition;
import org.jnosql.diana.api.document.DocumentDeleteQuery;

import static org.jnosql.artemis.document.query.DocumentQueryParserUtil.and;
import static org.jnosql.artemis.document.query.DocumentQueryParserUtil.or;
import static org.jnosql.artemis.document.query.DocumentQueryParserUtil.toCondition;

/**
 * Class the returns a {@link DocumentDeleteQuery}
 * on {@link DocumentRepositoryProxy}
 */
public class DocumentQueryDeleteParser {

    private static final String PREFIX = "deleteBy";
    private static final String TOKENIZER = "(?=And|OrderBy|Or)";


    public DocumentDeleteQuery parse(String methodName, Object[] args, ClassRepresentation representation) {
        String[] tokens = methodName.replace(PREFIX, DocumentQueryParserUtil.EMPTY).split(TOKENIZER);

        DocumentCondition condition = null;

        int index = 0;
        for (String token : tokens) {
            if (token.startsWith(DocumentQueryParserUtil.AND)) {
                ConditionResult result = and(args, index, token, methodName, representation, condition);
                condition = result.getCondition();
                index = result.getIndex();
            } else if (token.startsWith(DocumentQueryParserUtil.OR)) {
                ConditionResult result = or(args, index, token, methodName, representation, condition);
                condition = result.getCondition();
                index = result.getIndex();
            } else {
                condition = toCondition(token, index, args, methodName, representation);
                index++;
            }
        }
        return new ArtemisDocumentDeleteQuery(representation.getName(), condition);
    }

}
