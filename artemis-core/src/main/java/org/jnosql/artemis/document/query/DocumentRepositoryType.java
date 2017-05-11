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


import org.jnosql.diana.api.document.DocumentDeleteQuery;
import org.jnosql.diana.api.document.DocumentQuery;

import java.lang.reflect.Method;
import java.util.Optional;
import java.util.stream.Stream;

enum DocumentRepositoryType {

    DEFAULT, FIND_BY, DELETE_BY, QUERY, QUERY_DELETE, UNKNOWN;


    static DocumentRepositoryType of(Method method, Object[] args) {
        String methodName = method.getName();
        switch (methodName) {
            case "save":
            case "deleteById":
            case "delete":
            case "findById":
            case "existsById":
                return DEFAULT;
            default:
        }

        if (isQuery(args)) {
            return QUERY;
        }

        if (isQueryDelete(args)) {
            return QUERY_DELETE;
        }

        if (methodName.startsWith("findBy")) {
            return FIND_BY;
        } else if (methodName.startsWith("deleteBy")) {
            return DELETE_BY;
        }
        return UNKNOWN;
    }

    private static boolean isQuery(Object[] args) {
        return getQuery(args).isPresent();
    }

    private static boolean isQueryDelete(Object[] args) {
        return getDeleteQuery(args).isPresent();
    }

    static Optional<DocumentQuery> getQuery(Object[] args) {
        return Stream.of(args)
                .filter(DocumentQuery.class::isInstance).map(DocumentQuery.class::cast)
                .findFirst();
    }

    static Optional<DocumentDeleteQuery> getDeleteQuery(Object[] args) {
        return Stream.of(args)
                .filter(DocumentDeleteQuery.class::isInstance)
                .map(DocumentDeleteQuery.class::cast)
                .findFirst();
    }

}
