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

import org.jnosql.artemis.column.query.ColumnQueryParserUtil.ConditionResult;
import org.jnosql.artemis.reflection.ClassRepresentation;
import org.jnosql.diana.api.column.ColumnCondition;
import org.jnosql.diana.api.column.ColumnDeleteQuery;

import static org.jnosql.artemis.column.query.ColumnQueryParserUtil.and;
import static org.jnosql.artemis.column.query.ColumnQueryParserUtil.or;
import static org.jnosql.artemis.column.query.ColumnQueryParserUtil.toCondition;

/**
 * Class the returns a {@link ColumnDeleteQuery}
 * on {@link ColumnRepositoryProxy}
 */
public class ColumnQueryDeleteParser {

    private static final String PREFIX = "deleteBy";
    private static final String TOKENIZER = "(?=And|OrderBy|Or)";


    public ColumnDeleteQuery parse(String methodName, Object[] args, ClassRepresentation representation) {


        ColumnCondition condition = null;
        String[] tokens = methodName.replace(PREFIX, ColumnQueryParserUtil.EMPTY).split(TOKENIZER);

        int index = 0;
        for (String token : tokens) {
            if (token.startsWith(ColumnQueryParserUtil.AND)) {
                ConditionResult result = and(args, index, token, methodName, representation, condition);
                condition = result.getCondition();
                index = result.getIndex();

            } else if (token.startsWith(ColumnQueryParserUtil.OR)) {
                ConditionResult result = or(args, index, token, methodName, representation, condition);
                condition = result.getCondition();
                index = result.getIndex();
            } else {
                condition = toCondition(token, index, args, methodName, representation);
                index++;
            }
        }

        return new ArtemisColumnDeleteQuery(representation.getName(), condition);

    }


}
