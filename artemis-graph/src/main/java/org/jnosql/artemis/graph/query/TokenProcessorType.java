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
import org.jnosql.artemis.DynamicQueryException;
import org.jnosql.artemis.reflection.ClassRepresentation;

import java.util.stream.Stream;

import static org.jnosql.artemis.graph.query.GraphQueryParserUtil.EMPTY;

enum TokenProcessorType implements TokenProcessor {

    BETWEEN("Between") {
        @Override
        public GraphTraversal<?, ?> process(String token, int index, Object[] args, String methodName, ClassRepresentation representation, GraphTraversal<?, ?> traversal) {
            checkContents(index, args.length, 2, methodName);
            String name = getName(token, representation).replace(this.getType(), EMPTY);
            return traversal.has(name, P.between(args[index], args[++index]));
        }
    },
    OUT_V("OutV") {
        @Override
        public GraphTraversal<?, ?> process(String token, int index, Object[] args, String methodName, ClassRepresentation representation, GraphTraversal<?, ?> traversal) {

            if (this.getType().equals(token)) {
                checkContents(index, args.length, 1, methodName);
                return traversal.out(args[index].toString());
            }

            String label = getName(token).replace(this.getType(), EMPTY);
            return traversal.out(label);
        }
    }, IN_V("InV") {
        @Override
        public GraphTraversal<?, ?> process(String token, int index, Object[] args, String methodName, ClassRepresentation representation, GraphTraversal<?, ?> traversal) {

            if (this.getType().equals(token)) {
                checkContents(index, args.length, 1, methodName);
                return traversal.in(args[index].toString());
            }
            String label = getName(token).replace(this.getType(), EMPTY);
            return traversal.in(label);
        }
    }, BOTH_V("BothV") {
        @Override
        public GraphTraversal<?, ?> process(String token, int index, Object[] args, String methodName, ClassRepresentation representation, GraphTraversal<?, ?> traversal) {
            if (this.getType().equals(token)) {
                checkContents(index, args.length, 1, methodName);
                return traversal.both(args[index].toString());
            }
            String label = getName(token).replace(this.getType(), EMPTY);
            return traversal.both(label);
        }
    }, LESSA_THAN_EQUAL("LessThanEqual") {
        @Override
        public GraphTraversal<?, ?> process(String token, int index, Object[] args, String methodName, ClassRepresentation representation, GraphTraversal<?, ?> traversal) {
            checkContents(index, args.length, 1, methodName);
            String name = getName(token, representation).replace(this.getType(), EMPTY);
            return traversal.has(name, P.lte(args[index]));
        }
    }, GREATER_THAN_EQUAL("GreaterThanEqual") {
        @Override
        public GraphTraversal<?, ?> process(String token, int index, Object[] args, String methodName, ClassRepresentation representation, GraphTraversal<?, ?> traversal) {
            checkContents(index, args.length, 1, methodName);
            String name = getName(token, representation).replace(this.getType(), EMPTY);
            return traversal.has(name, P.gte(args[index]));
        }
    }, LESS_THAN("LessThan") {
        @Override
        public GraphTraversal<?, ?> process(String token, int index, Object[] args, String methodName, ClassRepresentation representation, GraphTraversal<?, ?> traversal) {
            checkContents(index, args.length, 1, methodName);
            String name = getName(token, representation).replace(this.getType(), EMPTY);
            return traversal.has(name, P.lt(args[index]));
        }
    }, GREATER_THAN("GreaterThan") {
        @Override
        public GraphTraversal<?, ?> process(String token, int index, Object[] args, String methodName, ClassRepresentation representation, GraphTraversal<?, ?> traversal) {
            checkContents(index, args.length, 1, methodName);
            String name = getName(token, representation).replace(this.getType(), EMPTY);
            return traversal.has(name, P.gt(args[index]));
        }
    }, DEFAULT("") {
        @Override
        public GraphTraversal<?, ?> process(String token, int index, Object[] args, String methodName, ClassRepresentation representation, GraphTraversal<?, ?> traversal) {
            checkContents(index, args.length, 1, methodName);
            String name = getName(token, representation);
            return traversal.has(name, args[index]);
        }
    };

    private final String type;

    TokenProcessorType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }


    private static void checkContents(int index, int argSize, int required, String method) {
        if ((index + required) <= argSize) {
            return;
        }
        throw new DynamicQueryException(String.format("There is a missed argument in the method %s",
                method));
    }

    private static String getName(String token, ClassRepresentation representation) {
        return representation.getColumnField(getName(token));
    }

    private static String getName(String token) {
        return String.valueOf(Character.toLowerCase(token.charAt(0))).concat(token.substring(1));
    }

    static TokenProcessor of(String token) {

        return Stream.of(TokenProcessorType.values())
                .filter(t -> token.contains(t.getType()))
                .findFirst().orElse(DEFAULT);
    }


    static boolean isGraphToken(String token) {
        return Stream.of(OUT_V, IN_V, BOTH_V)
                .anyMatch(t -> token.contains(t.getType()));
    }


    static boolean isNotGraphToken(String token) {
        return !isGraphToken(token);
    }

    static boolean isBetweenToken(String token) {
        return BETWEEN.getType().contains(token);
    }


}
