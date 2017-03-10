package org.jnosql.artemis.column;

import org.jnosql.diana.api.column.ColumnDeleteQuery;

import java.util.Objects;


class DefaultColumnDeleteQueryExecute implements ColumnDeleteQueryExecute {

    private final ColumnDeleteQuery query;

    public DefaultColumnDeleteQueryExecute(ColumnDeleteQuery query) {
        this.query = Objects.requireNonNull(query, "query is required");
    }

    @Override
    public ColumnDeleteQuery getQuery() {
        return query;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ColumnDeleteQueryExecute)) {
            return false;
        }
        ColumnDeleteQueryExecute that = (ColumnDeleteQueryExecute) o;
        return Objects.equals(query, that.getQuery());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(query);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("DefaultColumnDeleteQueryExecute{");
        sb.append("query=").append(query);
        sb.append('}');
        return sb.toString();
    }
}
