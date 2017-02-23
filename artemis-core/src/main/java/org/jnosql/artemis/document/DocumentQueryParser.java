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
import org.jnosql.diana.api.Condition;
import org.jnosql.diana.api.Sort;
import org.jnosql.diana.api.document.Document;
import org.jnosql.diana.api.document.DocumentCondition;
import org.jnosql.diana.api.document.DocumentQuery;

import java.util.Arrays;

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
    private static final String LESS_THAN_EQUAL = "LessEqualThan";
    private static final String GREATER_THAN_EQUAL = "GreaterEqualThan";
    private static final String LIKE = "Like";
    private static final String EMPTY = "";

    DocumentQuery parse(String query, Object[] args, ClassRepresentation classRepresentation) {
        DocumentQuery documentQuery = DocumentQuery.of(classRepresentation.getName());
        String[] tokens = query.replace(PREFIX, EMPTY).split("(?=AND|OR|OrderBy)");
        Integer index = 0;
        for (String token : tokens) {
            if (token.startsWith(AND)) {
                index = and(args, documentQuery, index, token);
            } else if (token.startsWith(OR)) {
                index = or(args, documentQuery, index, token);
            } else if (token.startsWith(ORDER_BY)) {
                sort(documentQuery, token);
            } else {
                DocumentCondition condition = toCondition(token, index, args);
                documentQuery.and(condition);
                index++;
            }

        }
        return documentQuery;
    }

    private DocumentCondition toCondition(String token, Integer index, Object[] args) {

        if (token.contains(BETWEEN)) {
            String name = getName(token).replace(BETWEEN, EMPTY);
            return DocumentCondition.between(Document.of(name, Arrays.asList(args[index], args[++index])));

        } else if (token.contains(LESS_THAN)) {
            String name = getName(token).replace(LESS_THAN, EMPTY);
            return DocumentCondition.lt(Document.of(name, args[index]));
        } else if (token.contains(GREATER_THAN)) {
            String name = getName(token).replace(GREATER_THAN, EMPTY);
            return DocumentCondition.gt(Document.of(name, args[index]));
        } else if (token.contains(LESS_THAN_EQUAL)) {
            String name = getName(token).replace(LESS_THAN_EQUAL, EMPTY);
            return DocumentCondition.lte(Document.of(name, args[index]));
        } else if (token.contains(GREATER_THAN_EQUAL)) {
            String name = getName(token).replace(GREATER_THAN_EQUAL, EMPTY);
            return DocumentCondition.gte(Document.of(name, args[index]));
        } else if (token.contains(LIKE)) {
            String name = getName(token).replace(LIKE, EMPTY);
            return DocumentCondition.like(Document.of(name, args[index]));
        }
        String name = getName(token);
        return DocumentCondition.eq(Document.of(name, args[index]));

    }

    private int or(Object[] args, DocumentQuery documentQuery, Integer index, String token) {
        String field = token.replace(OR, EMPTY);
        DocumentCondition condition = toCondition(field, index, args);
        documentQuery.or(condition);
        if (Condition.BETWEEN.equals(condition.getCondition())) {
            return index + 2;
        } else {
            return index++;
        }
    }

    private int and(Object[] args, DocumentQuery documentQuery, Integer index, String token) {
        String field = token.replace(AND, EMPTY);
        DocumentCondition condition = toCondition(field, index, args);
        documentQuery.and(condition);
        if (Condition.BETWEEN.equals(condition.getCondition())) {
            return index + 2;
        } else {
            return index++;
        }

    }

    private void sort(DocumentQuery documentQuery, String token) {
        String field = token.replace(ORDER_BY, EMPTY);
        if (field.contains("Desc")) {
            documentQuery.addSort(Sort.of(getName(field.replace("Desc", EMPTY)), Sort.SortType.DESC));
        } else {
            documentQuery.addSort(Sort.of(getName(field.replace("Asc", EMPTY)), Sort.SortType.ASC));
        }
    }

    private String getName(String token) {
        return String.valueOf(Character.toLowerCase(token.charAt(0)))
                .concat(token.substring(1));
    }

}
