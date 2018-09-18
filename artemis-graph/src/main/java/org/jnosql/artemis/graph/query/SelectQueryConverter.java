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
package org.jnosql.artemis.graph.query;

import org.apache.tinkerpop.gremlin.process.traversal.P;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.jnosql.aphrodite.antlr.method.SelectMethodFactory;
import org.jnosql.artemis.reflection.ClassRepresentation;
import org.jnosql.query.Condition;
import org.jnosql.query.ConditionValue;
import org.jnosql.query.Operator;
import org.jnosql.query.SelectQuery;
import org.jnosql.query.Sort;
import org.jnosql.query.Where;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.apache.tinkerpop.gremlin.process.traversal.Order.decr;
import static org.apache.tinkerpop.gremlin.process.traversal.Order.incr;

class SelectQueryConverter implements Function<GraphQueryMethod, List<Vertex>> {


    @Override
    public List<Vertex> apply(GraphQueryMethod graphQuery) {

        SelectMethodFactory selectMethodFactory = SelectMethodFactory.get();
        SelectQuery query = selectMethodFactory.apply(graphQuery.getMethod(), graphQuery.getEntityName());
        ClassRepresentation representation = graphQuery.getRepresentation();

        GraphTraversal<Vertex, Vertex> traversal = graphQuery.getTraversal();
        if (query.getWhere().isPresent()) {
            Where where = query.getWhere().get();

            Condition condition = where.getCondition();
            traversal.filter(getPredicate(graphQuery, condition, representation));
        }

        if (query.getSkip() > 0) {
            traversal.skip(query.getSkip());
        }

        if (query.getLimit() > 0) {
            return traversal.next((int) query.getLimit());
        }
        query.getOrderBy().forEach(getSort(traversal, representation));
        return traversal.toList();
    }

    private Consumer<Sort> getSort(GraphTraversal<Vertex, Vertex> traversal, ClassRepresentation representation) {
        return o -> {
            if (Sort.SortType.ASC.equals(o.getType())) {
                traversal.order().by(representation.getColumnField(o.getName()), incr);
            } else {
                traversal.order().by(representation.getColumnField(o.getName()), decr);
            }
        };
    }

    private GraphTraversal<Vertex, Vertex> getPredicate(GraphQueryMethod graphQuery, Condition condition,
                                                        ClassRepresentation representation) {
        Operator operator = condition.getOperator();
        String name = condition.getName();
        String nativeName = representation.getColumnField(name);
        switch (operator) {
            case EQUALS:
                return __.has(nativeName, P.eq(graphQuery.getValue(name)));
            case GREATER_THAN:
                return __.has(nativeName, P.gt(graphQuery.getValue(name)));
            case GREATER_EQUALS_THAN:
                return __.has(nativeName, P.gte(graphQuery.getValue(name)));
            case LESSER_THAN:
                return __.has(nativeName, P.lt(graphQuery.getValue(name)));
            case LESSER_EQUALS_THAN:
                return __.has(nativeName, P.lte(graphQuery.getValue(name)));
            case BETWEEN:
                return __.has(nativeName, P.between(graphQuery.getValue(name), graphQuery.getValue(name)));
            case IN:
                return __.has(nativeName, P.eq(graphQuery.getInValue(name)));
            case NOT:
                Condition notCondition = ConditionValue.class.cast(condition.getValue()).get().get(0);
                return __.not(getPredicate(graphQuery, notCondition, representation));
            case AND:
                return ConditionValue.class.cast(condition.getValue()).get().stream()
                        .map(c -> getPredicate(graphQuery, c, representation)).reduce((a, b) -> a.and(b))
                        .orElseThrow(() -> new UnsupportedOperationException("There is an inconsistency at the AND operator"));
            case OR:
                return ConditionValue.class.cast(condition.getValue()).get().stream()
                        .map(c -> getPredicate(graphQuery, c, representation)).reduce((a, b) -> a.or(b))
                        .orElseThrow(() -> new UnsupportedOperationException("There is an inconsistency at the OR operator"));
            default:
                throw new UnsupportedOperationException("There is not support to the type " + operator + " in graph");


        }
    }
}
