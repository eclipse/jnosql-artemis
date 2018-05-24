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

import static org.jnosql.artemis.graph.query.TokenProcessorType.isBetweenToken;
import static org.jnosql.artemis.graph.query.TokenProcessorType.isGraphToken;

final class GraphQueryParserUtil {

    static final String AND = "And";
    static final String EMPTY = "";


    private GraphQueryParserUtil() {
    }

    static void feedTraversal(String token,
                              int index,
                              Object[] args,
                              String methodName,
                              ClassRepresentation representation,
                              GraphTraversal<?, ?> traversal) {

        TokenProcessor tokenProcessor = TokenProcessorType.of(token);
        tokenProcessor.process(token, index, args, methodName, representation, traversal);
    }


    static int and(Object[] args,
                   int index,
                   String token,
                   String methodName,
                   ClassRepresentation representation,
                   GraphTraversal<?, ?> traversal) {
        String field = token.replace(AND, EMPTY);
        feedTraversal(field, index, args, methodName, representation, traversal);

        if (isBetweenToken(token)) {
            return index + 2;
        } else if (isGraphToken(token)) {
            return index;
        } else {
            return ++index;
        }
    }




}
