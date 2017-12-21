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

import org.jnosql.artemis.Converters;
import org.jnosql.artemis.DynamicQueryException;
import org.jnosql.artemis.document.util.ConverterUtil;
import org.jnosql.artemis.reflection.ClassRepresentation;
import org.jnosql.diana.api.document.Document;
import org.jnosql.diana.api.document.DocumentCondition;

import java.util.Arrays;
import java.util.stream.Stream;

import static org.jnosql.artemis.document.query.DocumentQueryParserUtil.EMPTY;

enum DocumentTokenProcessorType implements DocumentTokenProcessor {

    BETWEEN("Between", 2) {
        @Override
        public DocumentCondition process(String token, int index, Object[] args, String methodName,
                                         ClassRepresentation representation, Converters converters) {
            checkContents(index, args.length, this.getFieldsRequired(), methodName);
            String name = getName(token, representation).replace(this.getType(), EMPTY);
            Object valueA = getValue(token, args[index], representation, converters);
            Object valueB = getValue(token, args[++index], representation, converters);
            return DocumentCondition.between(Document.of(name, Arrays.asList(valueA, valueB)));
        }
    },
    LESS_THAN_EQUAL("LessThanEqual", 1) {
        @Override
        public DocumentCondition process(String token, int index, Object[] args, String methodName,
                                         ClassRepresentation representation, Converters converters) {
            checkContents(index, args.length, this.getFieldsRequired(), methodName);
            String name = getName(token, representation).replace(this.getType(), EMPTY);
            Object value = getValue(token, args[index], representation, converters);
            return DocumentCondition.lte(Document.of(name, value));
        }
    },
    GREATER_THAN_EQUAL("GreaterThanEqual", 1) {
        @Override
        public DocumentCondition process(String token, int index, Object[] args, String methodName,
                                         ClassRepresentation representation, Converters converters) {
            checkContents(index, args.length, this.getFieldsRequired(), methodName);
            String name = getName(token, representation).replace(this.getType(), EMPTY);
            Object value = getValue(token, args[index], representation, converters);
            return DocumentCondition.gte(Document.of(name, value));
        }
    },
    LESS_THAN("LessThan", 1) {
        @Override
        public DocumentCondition process(String token, int index, Object[] args, String methodName,
                                         ClassRepresentation representation, Converters converters) {
            checkContents(index, args.length, this.getFieldsRequired(), methodName);
            String name = getName(token, representation).replace(this.getType(), EMPTY);
            Object value = getValue(token, args[index], representation, converters);
            return DocumentCondition.lt(Document.of(name, value));
        }
    },
    GREATER_THAN("GreaterThan", 1) {
        @Override
        public DocumentCondition process(String token, int index, Object[] args, String methodName,
                                         ClassRepresentation representation, Converters converters) {
            checkContents(index, args.length, this.getFieldsRequired(), methodName);
            String name = getName(token, representation).replace(this.getType(), EMPTY);
            Object value = getValue(token, args[index], representation, converters);
            return DocumentCondition.gt(Document.of(name, value));
        }
    },
    LIKE("Like", 1) {
        @Override
        public DocumentCondition process(String token, int index, Object[] args, String methodName,
                                         ClassRepresentation representation, Converters converters) {
            checkContents(index, args.length, this.getFieldsRequired(), methodName);
            String name = getName(token, representation).replace(this.getType(), EMPTY);
            Object value = getValue(token, args[index], representation, converters);
            return DocumentCondition.like(Document.of(name, value));
        }
    },
    DEFAULT("", 1) {
        @Override
        public DocumentCondition process(String token, int index, Object[] args, String methodName,
                                         ClassRepresentation representation, Converters converters) {
            checkContents(index, args.length, this.getFieldsRequired(), methodName);
            String name = getName(token, representation);
            Object value = getValue(token, args[index], representation, converters);
            return DocumentCondition.eq(Document.of(name, value));
        }
    };


    private final String type;

    private final int fieldsRequired;

    DocumentTokenProcessorType(String type, int fieldsRequired) {
        this.type = type;
        this.fieldsRequired = fieldsRequired;
    }

    public String getType() {
        return type;
    }

    public int getFieldsRequired() {
        return fieldsRequired;
    }

    private static void checkContents(int index, int argSize, int required, String method) {
        if ((index + required) <= argSize) {
            return;
        }
        throw new DynamicQueryException(String.format("There is a missed argument in the method %s",
                method));
    }


    private static String getName(String token, ClassRepresentation representation) {
        return representation.getColumnField(getJavaField(token));
    }

    private static Object getValue(String token, Object value, ClassRepresentation representation,
                                   Converters converters) {

        String javaField = getJavaField(token);
        return ConverterUtil.getValue(value, representation, javaField, converters);
    }

    private static String getJavaField(String token) {
        return String.valueOf(Character.toLowerCase(token.charAt(0)))
                .concat(token.substring(1));
    }

    static DocumentTokenProcessorType of(String token) {
        return Stream.of(DocumentTokenProcessorType.values())
                .filter(t -> token.contains(t.getType()))
                .findFirst().orElse(DEFAULT);
    }
}
