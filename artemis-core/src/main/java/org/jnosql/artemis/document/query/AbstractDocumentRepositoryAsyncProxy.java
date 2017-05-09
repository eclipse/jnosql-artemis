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


import org.jnosql.artemis.DynamicQueryException;
import org.jnosql.artemis.RepositoryAsync;
import org.jnosql.artemis.document.DocumentTemplateAsync;
import org.jnosql.artemis.reflection.ClassRepresentation;
import org.jnosql.diana.api.document.DocumentDeleteQuery;
import org.jnosql.diana.api.document.DocumentQuery;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.function.Consumer;

import static org.jnosql.artemis.document.query.DocumentRepositoryType.getDeleteQuery;
import static org.jnosql.artemis.document.query.DocumentRepositoryType.getQuery;

/**
 * The template method to {@link RepositoryAsync} to Document
 *
 * @param <T> the class type
 */
public abstract class AbstractDocumentRepositoryAsyncProxy<T> implements InvocationHandler {


    protected abstract RepositoryAsync getRepository();

    protected abstract DocumentQueryParser getQueryParser();

    protected abstract DocumentTemplateAsync getTemplate();

    protected abstract DocumentQueryDeleteParser getDeleteParser();

    protected abstract ClassRepresentation getClassRepresentation();

    @Override
    public Object invoke(Object instance, Method method, Object[] args) throws Throwable {


        String methodName = method.getName();
        DocumentRepositoryType type = DocumentRepositoryType.of(method, args);

        switch (type) {
            case DEFAULT:
                return method.invoke(getRepository(), args);
            case FIND_BY:
                DocumentQuery query = getQueryParser().parse(methodName, args, getClassRepresentation());
                return executeQuery(getCallBack(args), query);
            case DELETE_BY:
                DocumentDeleteQuery deleteQuery = getDeleteParser().parse(methodName, args, getClassRepresentation());
                return executeDelete(args, deleteQuery);
            case QUERY:
                DocumentQuery documentQuery = getQuery(args).get();
                return executeQuery(getCallBack(args), documentQuery);
            case QUERY_DELETE:
                return executeDelete(args, getDeleteQuery(args).get());
            default:
                return Void.class;
        }

    }

    private Object executeDelete(Object[] args, DocumentDeleteQuery query1) {
        Object callBack = getCallBack(args);
        if (Consumer.class.isInstance(callBack)) {
            getTemplate().delete(query1, Consumer.class.cast(callBack));
        } else {
            getTemplate().delete(query1);
        }
        return Void.class;
    }

    private Object getCallBack(Object[] args) {
        return args[args.length - 1];
    }

    private Object executeQuery(Object arg, DocumentQuery query) {
        if (Consumer.class.isInstance(arg)) {
            getTemplate().select(query, Consumer.class.cast(arg));
        } else {
            throw new DynamicQueryException("On select async method you must put a java.util.function.Consumer" +
                    " as end parameter as callback");
        }
        return Void.class;
    }



}
