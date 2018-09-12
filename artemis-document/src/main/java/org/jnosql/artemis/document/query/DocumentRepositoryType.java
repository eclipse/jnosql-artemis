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


import org.jnosql.artemis.Query;
import org.jnosql.diana.api.document.DocumentDeleteQuery;
import org.jnosql.diana.api.document.DocumentQuery;

import java.lang.reflect.Method;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

enum DocumentRepositoryType {

    DEFAULT, FIND_BY, FIND_ALL, DELETE_BY, UNKNOWN, OBJECT_METHOD, JNOSQL_QUERY;

    static DocumentRepositoryType of(Method method, Object[] args) {


        Class<?> declaringClass = method.getDeclaringClass();
        if (Object.class.equals(declaringClass)) {
            return OBJECT_METHOD;
        }
        if()
        if (Objects.nonNull(method.getAnnotation(Query.class))) {
            return JNOSQL_QUERY;
        }

        String methodName = method.getName();
        switch (methodName) {
            case "save":
            case "deleteById":
            case "delete":
            case "findById":
            case "existsById":
                return DEFAULT;
            case "findAll":
                return FIND_ALL;
            default:
        }


        if (methodName.startsWith("findBy")) {
            return FIND_BY;
        } else if (methodName.startsWith("deleteBy")) {
            return DELETE_BY;
        }
        return UNKNOWN;
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
