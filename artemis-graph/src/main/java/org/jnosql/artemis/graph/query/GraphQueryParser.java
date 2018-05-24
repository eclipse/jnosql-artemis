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

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.jnosql.artemis.reflection.ClassRepresentation;

import static org.jnosql.artemis.graph.query.GraphQueryParserUtil.feedTraversal;
import static org.jnosql.artemis.graph.query.TokenProcessorType.isNotGraphToken;


class GraphQueryParser {


    private static final String PREFIX_FIND_BY = "findBy";
    private static final String PREFIX_DELETE_BY = "deleteBy";
    private static final String TOKENIZER = "(?=And)";

    private static final String EMPTY = "";


    public void findByParse(String methodName, Object[] args, ClassRepresentation representation,
                            GraphTraversal<?, ?> traversal) {


        parse(methodName, args, representation, traversal, PREFIX_FIND_BY);
    }

    public void deleteByParse(String methodName, Object[] args, ClassRepresentation representation,
                              GraphTraversal<?, ?> traversal) {

        parse(methodName, args, representation, traversal, PREFIX_DELETE_BY);
    }


    private void parse(String methodName, Object[] args, ClassRepresentation representation,
                       GraphTraversal<?, ?> traversal, String parse) {


        String[] tokens = methodName.replace(parse, EMPTY).split(TOKENIZER);
        int index = 0;
        for (String token : tokens) {
            if (token.startsWith(GraphQueryParserUtil.AND)) {
                index = GraphQueryParserUtil.and(args, index, token, methodName, representation, traversal);
            } else {
                feedTraversal(token, index, args, methodName, representation, traversal);
                if (isNotGraphToken(token)) {
                    index++;
                }
            }
        }
    }


}
