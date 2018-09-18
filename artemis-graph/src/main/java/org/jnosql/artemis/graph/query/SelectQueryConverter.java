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
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.jnosql.query.Condition;
import org.jnosql.query.Operator;
import org.jnosql.query.SelectQuery;
import org.jnosql.query.Where;

import java.util.List;
import java.util.function.Function;

class SelectQueryConverter implements Function<GraphQueryMethod, List<Vertex>> {


    @Override
    public List<Vertex> apply(GraphQueryMethod graphQuery) {

        SelectQuery query = graphQuery.getQuery();
        GraphTraversal<Vertex, Vertex> traversal = graphQuery.getTraversal();
        if (query.getWhere().isPresent()) {
            Where where = query.getWhere().get();

            Condition condition = where.getCondition();
            Operator operator = condition.getOperator();
            String name = condition.getName();
            switch (operator) {
                case EQUALS:
                    traversal.has(name, graphQuery.getValue(name));
                case GREATER_THAN:
                    traversal.has(name, P.gt(graphQuery.getValue(name)));
                case GREATER_EQUALS_THAN:
                    traversal.has(name, P.gte(graphQuery.getValue(name)));
                case LESSER_THAN:
                    traversal.has(name, P.lt(graphQuery.getValue(name)));
                case BETWEEN:
                    traversal.has(name, P.between(graphQuery.getValue(name), graphQuery.getValue(name)));
                case NOT:

                case AND:
                case OR:
                default:
                    throw new UnsupportedOperationException("There is not support to the type " + operator + " in graph");
                case IN:
                    traversal.has(name, P.within(graphQuery.getInValue(name)));
                case LESSER_EQUALS_THAN:
                    traversal.has(name, P.lte(graphQuery.getValue(name)));
            }
        }

        return null;
    }
}
