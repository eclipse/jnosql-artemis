
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

    DocumentQuery parse(String query, Object[] args, ClassRepresentation classRepresentation) {
        DocumentQuery documentQuery = DocumentQuery.of(classRepresentation.getName());
        String[] tokens = query.replace(PREFIX, "").split("(?=AND|OR|OrderBy)");
        int index = 0;
        for (String token : tokens) {
            if (token.startsWith("AND")) {
                String field = token.replace("AND", "");
                String name = String.valueOf(Character.toLowerCase(field.charAt(0)))
                        .concat(field.substring(1));
                documentQuery.and(DocumentCondition.eq(Document.of(name, args[index])));
            } else if (token.startsWith("OR")) {
                String field = token.replace("AND", "");
                String name = String.valueOf(Character.toLowerCase(field.charAt(0)))
                        .concat(field.substring(1));
                documentQuery.or(DocumentCondition.eq(Document.of(name, args[index])));
            } else if (token.startsWith("OrderBy")) {

            } else {
                String name = String.valueOf(Character.toLowerCase(token.charAt(0)))
                        .concat(token.substring(1));
                documentQuery.and(DocumentCondition.eq(Document.of(name, args[index])));
            }
            index++;
        }
        return documentQuery;
    }
}
