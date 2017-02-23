
/*
 * Copyright 2017 Otavio Santana and others
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jnosql.artemis.document;

import org.jnosql.artemis.reflection.ClassRepresentation;
import org.jnosql.diana.api.document.Document;
import org.jnosql.diana.api.document.DocumentCondition;
import org.jnosql.diana.api.document.DocumentQuery;

/**
 * Class the returns a {@link org.jnosql.diana.api.document.DocumentQuery}
 * on {@link DocumentCrudRepositoryProxy}
 */
class DocumentQueryParser {


    private static final String PREFIX = "findBy";
    private static final String AND = "AND";
    private static final String OR = "OR";
    private static final String ORDER_BY = "OrderBy";
    private static final String BETWEEN = "Between";
    private static final String LESS_THAN = "LessThan";
    private static final String GREATER_THAN = "GreaterThan";
    private static final String LESS_THAN_EQUAL = "LessThanEqual";
    private static final String GREATER_THAN_EQUAL = "GreaterThanEqual";
    private static final String LIKE = "Like";

    DocumentQuery parse(String query, Object[] args, ClassRepresentation classRepresentation) {
        DocumentQuery documentQuery = DocumentQuery.of(classRepresentation.getName());
        String[] tokens = query.replace(PREFIX, "").split("(?=AND|OR|OrderBy)");
        int index = 0;
        for (String token : tokens) {
            if (token.startsWith(AND)) {
                String field = token.replace(AND, "");
                DocumentCondition condition = toCondition(field, index, args);
                documentQuery.and(condition);
            } else if (token.startsWith(OR)) {
                String field = token.replace(OR, "");
                DocumentCondition condition = toCondition(field, index, args);
                documentQuery.or(condition);
            } else if (token.startsWith(ORDER_BY)) {

            } else {
                DocumentCondition condition = toCondition(token, index, args);
                documentQuery.and(condition);
            }
            index++;
        }
        return documentQuery;
    }
    //AND
    //OR
    //Between
    //LessThan
    //GreaterThan
    //LessThanEqual
    //GreaterThanEqual
    //Like

    private DocumentCondition toCondition(String token, int index, Object[] args) {

        if (token.contains(BETWEEN)) {

        } else if (token.contains(LESS_THAN)) {
            String name = String.valueOf(Character.toLowerCase(token.charAt(0)))
                    .concat(token.substring(1)).replace(LESS_THAN, "");
            return DocumentCondition.lt(Document.of(name, args[index]));
        } else if (token.contains(GREATER_THAN)) {
            String name = String.valueOf(Character.toLowerCase(token.charAt(0)))
                    .concat(token.substring(1)).replace(GREATER_THAN, "");
            return DocumentCondition.gt(Document.of(name, args[index]));
        } else if (token.contains(LESS_THAN_EQUAL)) {
            String name = String.valueOf(Character.toLowerCase(token.charAt(0)))
                    .concat(token.substring(1)).replace(LESS_THAN_EQUAL, "");
            return DocumentCondition.lte(Document.of(name, args[index]));
        } else if (token.contains(GREATER_THAN_EQUAL)) {
            String name = String.valueOf(Character.toLowerCase(token.charAt(0)))
                    .concat(token.substring(1)).replace(GREATER_THAN_EQUAL, "");
            return DocumentCondition.gte(Document.of(name, args[index]));
        } else if (token.contains(LIKE)) {
            String name = String.valueOf(Character.toLowerCase(token.charAt(0)))
                    .concat(token.substring(1)).replace(LIKE, "");
            return DocumentCondition.like(Document.of(name, args[index]));
        }
        String name = String.valueOf(Character.toLowerCase(token.charAt(0)))
                .concat(token.substring(1));
        return DocumentCondition.eq(Document.of(name, args[index]));

    }
}
