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

import org.jnosql.diana.api.column.ColumnDeleteQuery;
import org.jnosql.diana.api.column.ColumnQuery;

import java.lang.reflect.Method;
import java.util.Optional;
import java.util.stream.Stream;

enum ColumnRepositoryType {
    DEFAULT, FIND_BY, DELETE_BY, QUERY, QUERY_DELETE, UNKNOWN, FIND_ALL;


    static ColumnRepositoryType of(Method method, Object[] args) {
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

    static Optional<ColumnQuery> getQuery(Object[] args) {
        return Stream.of(args)
                .filter(ColumnQuery.class::isInstance).map(ColumnQuery.class::cast)
                .findFirst();
    }

    static Optional<ColumnDeleteQuery> getDeleteQuery(Object[] args) {
        return Stream.of(args)
                .filter(ColumnDeleteQuery.class::isInstance)
                .map(ColumnDeleteQuery.class::cast)
                .findFirst();
    }

}
