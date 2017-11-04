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

import org.jnosql.artemis.Pagination;
import org.jnosql.artemis.reflection.ClassRepresentation;
import org.jnosql.diana.api.Sort;
import org.jnosql.diana.api.column.ColumnCondition;
import org.jnosql.diana.api.column.ColumnQuery;
import org.jnosql.diana.api.column.query.ColumnFrom;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static org.jnosql.artemis.column.query.ColumnQueryParserUtil.and;
import static org.jnosql.artemis.column.query.ColumnQueryParserUtil.or;
import static org.jnosql.artemis.column.query.ColumnQueryParserUtil.toCondition;
import static org.jnosql.diana.api.column.query.ColumnQueryBuilder.select;

/**
 * Class the returns a {@link ColumnQuery}
 * on {@link ColumnRepositoryProxy}
 */
public interface ColumnQueryParser {


    /**
     * Converts the method to {@link ColumnQuery}
     *
     * @param method         the method
     * @param args           the args
     * @param representation the representation class
     * @return a {@link ColumnQuery} instance
     * @throws NullPointerException when there is a null param
     */
    ColumnQuery parse(Method method, Object[] args, ClassRepresentation representation) throws NullPointerException;
}
