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

import org.jnosql.artemis.Converters;
import org.jnosql.artemis.Pagination;
import org.jnosql.artemis.reflection.ClassRepresentation;
import org.jnosql.diana.api.Sort;
import org.jnosql.diana.api.document.DocumentCondition;
import org.jnosql.diana.api.document.DocumentQuery;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static org.jnosql.artemis.document.query.DocumentQueryParserUtil.AND;
import static org.jnosql.artemis.document.query.DocumentQueryParserUtil.EMPTY;
import static org.jnosql.artemis.document.query.DocumentQueryParserUtil.ORDER_BY;
import static org.jnosql.artemis.document.query.DocumentQueryParserUtil.and;
import static org.jnosql.artemis.document.query.DocumentQueryParserUtil.or;
import static org.jnosql.artemis.document.query.DocumentQueryParserUtil.toCondition;
import static org.jnosql.diana.api.Sort.SortType.ASC;
import static org.jnosql.diana.api.Sort.SortType.DESC;

/**
 * Class the returns a {@link org.jnosql.diana.api.document.DocumentQuery}
 * on {@link DocumentRepositoryProxy}
 */
public class DocumentQueryParser {

    private static final Logger LOGGER = Logger.getLogger(DocumentQueryParser.class.getName());

    private static final String PREFIX = "findBy";
    private static final String TOKENIZER = "(?=And|OrderBy|Or)";


    public DocumentQuery parse(String methodName, Object[] args, ClassRepresentation representation, Converters converters) {


        String[] tokens = methodName.replace(PREFIX, EMPTY).split(TOKENIZER);
        String className = representation.getClassInstance().getName();
        DocumentCondition condition = null;
        List<Sort> sorts = new ArrayList<>();
        long limit = 0;
        long start = 0;


        int index = 0;
        for (String token : tokens) {

            if (token.startsWith(AND)) {
                DocumentQueryParserUtil.ConditionResult result = and(args, index, token, methodName,
                        representation, condition, converters);
                condition = result.getCondition();
                index = result.getIndex();
            } else {
                if (token.startsWith(ORDER_BY)) {
                    sort(sorts, token, representation);
                } else if (token.startsWith(DocumentQueryParserUtil.OR)) {
                    DocumentQueryParserUtil.ConditionResult result = or(args, index, token, methodName,
                            representation, condition, converters);
                    condition = result.getCondition();
                    index = result.getIndex();
                } else {
                    condition = toCondition(token, index, args, methodName, representation, converters);
                    index++;
                }
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

        return new ArtemisDocumentQuery(sorts, limit, start, condition, representation.getName());
    }


    private void sort(List<Sort> sorts, String token, ClassRepresentation representation) {
        String field = token.replace(ORDER_BY, EMPTY);
        if (field.contains("Desc")) {
            sorts.add(Sort.of(getName(field.replace("Desc", EMPTY), representation)
                    , DESC));
        } else {
            sorts.add(Sort.of(getName(field.replace("Asc", EMPTY), representation)
                    , ASC));
        }
    }

    private String getName(String token, ClassRepresentation representation) {
        return representation.getColumnField(String.valueOf(Character.toLowerCase(token.charAt(0)))
                .concat(token.substring(1)));
    }

}
