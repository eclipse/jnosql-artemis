/*
 * Copyright 2017 Otavio Santana and others
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jnosql.artemis.column.query;

import org.jnosql.artemis.column.ColumnTemplate;
import org.jnosql.diana.api.column.ColumnQuery;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Utilitarian class to return on dynamic query
 */
public final class ReturnTypeConverterUtil {

    private ReturnTypeConverterUtil() {
    }


    public static Object returnObject(ColumnQuery query, ColumnTemplate template, Class typeClass, Method method) {
        Class<?> returnType = method.getReturnType();

        if (typeClass.equals(returnType)) {
            Optional<Object> optional = template.singleResult(query);
            return optional.orElse(null);

        } else if (Optional.class.equals(returnType)) {
            return template.singleResult(query);
        } else if (List.class.equals(returnType)
                || Iterable.class.equals(returnType)
                || Collection.class.equals(returnType)) {
            return template.select(query);
        } else if (Set.class.equals(returnType)) {
            return new HashSet<>(template.select(query));
        } else if (Queue.class.equals(returnType)) {
            return new PriorityQueue<>(template.select(query));
        } else if (Stream.class.equals(returnType)) {
            return template.select(query).stream();
        }

        return template.select(query);
    }

}
