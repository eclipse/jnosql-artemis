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
package org.jnosql.artemis.document.query;


import org.jnosql.artemis.Repository;
import org.jnosql.artemis.document.DocumentTemplate;
import org.jnosql.artemis.reflection.ClassRepresentation;
import org.jnosql.diana.api.document.DocumentDeleteQuery;
import org.jnosql.diana.api.document.DocumentQuery;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import static org.jnosql.artemis.document.query.DocumentRepositoryType.getDeleteQuery;
import static org.jnosql.artemis.document.query.DocumentRepositoryType.getQuery;
import static org.jnosql.artemis.document.query.ReturnTypeConverterUtil.returnObject;

/**
 * The template method to {@link Repository} to Document
 *
 * @param <T> the class type
 */
public abstract class AbstractDocumentRepositoryProxy<T> implements InvocationHandler {


    protected abstract Repository getRepository();

    protected abstract DocumentQueryParser getQueryParser();

    protected abstract DocumentTemplate getTemplate();

    protected abstract DocumentQueryDeleteParser getDeleteParser();

    protected abstract ClassRepresentation getClassRepresentation();


    @Override
    public Object invoke(Object instance, Method method, Object[] args) throws Throwable {

        String methodName = method.getName();
        DocumentRepositoryType type = DocumentRepositoryType.of(method, args);
        Class<?> typeClass = getClassRepresentation().getClassInstance();

        switch (type) {
            case DEFAULT:
                return method.invoke(getRepository(), args);
            case FIND_BY:
                DocumentQuery query = getQueryParser().parse(methodName, args, getClassRepresentation());
                return returnObject(query, getTemplate(), typeClass, method);
            case DELETE_BY:
                getTemplate().delete(getDeleteParser().parse(methodName, args, getClassRepresentation()));
                return null;
            case QUERY:
                DocumentQuery documentQuery = getQuery(args).get();
                return returnObject(documentQuery, getTemplate(), typeClass, method);
            case QUERY_DELETE:
                DocumentDeleteQuery deleteQuery = getDeleteQuery(args).get();
                getTemplate().delete(deleteQuery);
                return Void.class;
            default:
                return Void.class;
        }
    }
}
