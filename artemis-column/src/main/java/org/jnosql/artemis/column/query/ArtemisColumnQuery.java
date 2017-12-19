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

import org.jnosql.diana.api.Sort;
import org.jnosql.diana.api.column.ColumnCondition;
import org.jnosql.diana.api.column.ColumnQuery;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

class ArtemisColumnQuery implements ColumnQuery {

    private final List<Sort> sorts;
    private final long limit;
    private final long start;
    private final ColumnCondition condition;
    private final String columnFamily;

    public ArtemisColumnQuery(List<Sort> sorts, long limit, long start, ColumnCondition condition, String columnFamily) {
        this.sorts = sorts;
        this.limit = limit;
        this.start = start;
        this.condition = condition;
        this.columnFamily = columnFamily;
    }

    @Override
    public long getMaxResults() {
        return limit;
    }

    @Override
    public long getFirstResult() {
        return start;
    }

    @Override
    public String getColumnFamily() {
        return columnFamily;
    }

    @Override
    public Optional<ColumnCondition> getCondition() {
        return Optional.ofNullable(condition);
    }

    @Override
    public List<String> getColumns() {
        return Collections.emptyList();
    }

    @Override
    public List<Sort> getSorts() {
        return sorts;
    }
}
