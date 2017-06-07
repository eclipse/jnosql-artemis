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

import org.jnosql.artemis.Pagination;
import org.jnosql.artemis.reflection.ClassRepresentation;
import org.jnosql.diana.api.Sort;
import org.jnosql.diana.api.document.DocumentCondition;
import org.jnosql.diana.api.document.DocumentQuery;

import java.util.logging.Logger;

import static org.jnosql.artemis.document.query.DocumentQueryParserUtil.AND;
import static org.jnosql.artemis.document.query.DocumentQueryParserUtil.EMPTY;
import static org.jnosql.artemis.document.query.DocumentQueryParserUtil.ORDER_BY;
import static org.jnosql.artemis.document.query.DocumentQueryParserUtil.toCondition;
import static org.jnosql.diana.api.Condition.BETWEEN;
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


    public DocumentQuery parse(String methodName, Object[] args, ClassRepresentation representation) {
        DocumentQuery documentQuery = DocumentQuery.of(representation.getName());
        String[] tokens = methodName.replace(PREFIX, EMPTY).split(TOKENIZER);
        String className = representation.getClassInstance().getName();

        int index = 0;
        for (String token : tokens) {

            if (token.startsWith(AND)) {
                index = and(args, documentQuery, index, token, methodName, representation);
            } else {
                if (token.startsWith(ORDER_BY)) {
                    sort(documentQuery, token, representation);
                } else if (token.startsWith(DocumentQueryParserUtil.OR)) {
                    index = or(args, documentQuery, index, token, methodName, representation);
                } else {
                    DocumentCondition condition = toCondition(token, index, args,
                            methodName, representation);

                    documentQuery.and(condition);
                    index++;
                }
            }
        }

        while (index < args.length) {
            Object value = args[index];
            if (Sort.class.isInstance(value)) {
                documentQuery.addSort(Sort.class.cast(value));
            } else if (Pagination.class.isInstance(value)) {
                Pagination pagination = Pagination.class.cast(value);
                documentQuery.withMaxResults(pagination.getMaxResults());
                documentQuery.withFirstResult(pagination.getFirstResult());
            } else {
                LOGGER.info(String.format("Ignoring parameter %s on  methodName %s class name %s arg-number: %d",
                        String.valueOf(value), methodName, className, index));
            }
            index++;
        }

        return documentQuery;
    }

    private int or(Object[] args, DocumentQuery documentQuery, int index, String token, String methodName,
                   ClassRepresentation representation) {

        String field = token.replace(DocumentQueryParserUtil.OR, EMPTY);

        DocumentCondition condition = toCondition(field, index, args, methodName, representation);
        documentQuery.or(condition);
        if (BETWEEN.equals(condition.getCondition())) {
            return index + 2;
        } else {
            return ++index;
        }
    }

    private int and(Object[] args, DocumentQuery documentQuery, int index, String token,
                    String methodName,  ClassRepresentation representation) {
        String field = token.replace(AND, EMPTY);
        DocumentCondition condition = toCondition(field, index, args, methodName, representation);

        documentQuery.and(condition);
        if (BETWEEN.equals(condition.getCondition())) {
            return index + 2;
        } else {
            return ++index;
        }

    }

    private void sort(DocumentQuery documentQuery, String token, ClassRepresentation representation) {
        String field = token.replace(ORDER_BY, EMPTY);
        if (field.contains("Desc")) {
            documentQuery.addSort(Sort.of(getName(field.replace("Desc", EMPTY), representation)
                    , DESC));
        } else {
            documentQuery.addSort(Sort.of(getName(field.replace("Asc", EMPTY), representation)
                    , ASC));
        }
    }

    private String getName(String token, ClassRepresentation representation) {
        return representation.getColumnField(String.valueOf(Character.toLowerCase(token.charAt(0)))
                .concat(token.substring(1)));
    }

}
