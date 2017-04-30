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


import org.jnosql.artemis.Repository;
import org.jnosql.artemis.column.ColumnTemplate;
import org.jnosql.artemis.reflection.ClassRepresentation;
import org.jnosql.artemis.reflection.ClassRepresentations;
import org.jnosql.diana.api.column.ColumnDeleteQuery;
import org.jnosql.diana.api.column.ColumnQuery;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;

import static org.jnosql.artemis.column.query.ColumnRepositoryType.getDeleteQuery;
import static org.jnosql.artemis.column.query.ColumnRepositoryType.getQuery;
import static org.jnosql.artemis.column.query.ReturnTypeConverterUtil.returnObject;


/**
 * Proxy handle to generate {@link Repository}
 *
 * @param <T> the type
 */
class ColumnRepositoryProxy<T> implements InvocationHandler {

    private final Class<T> typeClass;

    private final ColumnTemplate template;

    private final ColumnRepository crudRepository;

    private final ClassRepresentation classRepresentation;

    private final ColumnQueryParser queryParser;

    private final ColumnQueryDeleteParser deleteQueryParser;


    ColumnRepositoryProxy(ColumnTemplate template, ClassRepresentations classRepresentations, Class<?> repositoryType) {
        this.template = template;
        this.crudRepository = new ColumnRepository(template);
        this.typeClass = Class.class.cast(ParameterizedType.class.cast(repositoryType.getGenericInterfaces()[0])
                .getActualTypeArguments()[0]);
        this.classRepresentation = classRepresentations.get(typeClass);
        this.queryParser = new ColumnQueryParser();
        this.deleteQueryParser = new ColumnQueryDeleteParser();
    }


    @Override
    public Object invoke(Object o, Method method, Object[] args) throws Throwable {
        String methodName = method.getName();
        ColumnRepositoryType type = ColumnRepositoryType.of(method, args);
        switch (type) {
            case DEFAULT:
                return method.invoke(crudRepository, args);
            case FIND_BY:
                ColumnQuery query = queryParser.parse(methodName, args, classRepresentation);
                return returnObject(query, template, typeClass, method);
            case DELETE_BY:
                ColumnDeleteQuery deleteQuery = deleteQueryParser.parse(methodName, args, classRepresentation);
                template.delete(deleteQuery);
                return Void.class;
            case QUERY:
                ColumnQuery columnQuery = getQuery(args).get();
                return returnObject(columnQuery, template, typeClass, method);
            case QUERY_DELETE:
                ColumnDeleteQuery columnDeleteQuery = getDeleteQuery(args).get();
                template.delete(columnDeleteQuery);
                return Void.class;
            default:
                return Void.class;

        }
    }


    class ColumnRepository extends AbstractColumnRepository implements Repository {

        private final ColumnTemplate template;

        ColumnRepository(ColumnTemplate template) {
            this.template = template;
        }

        @Override
        protected ColumnTemplate getTemplate() {
            return template;
        }
    }
}
