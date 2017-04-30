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
import org.jnosql.artemis.reflection.ClassRepresentations;
import org.jnosql.diana.api.document.DocumentDeleteQuery;
import org.jnosql.diana.api.document.DocumentQuery;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.function.Consumer;

/**
 * Proxy handle to generate {@link RepositoryAsync}
 *
 * @param <T> the type
 */
class DocumentCrudRepositoryAsyncProxy<T> implements InvocationHandler {

    private final Class<T> typeClass;

    private final DocumentTemplateAsync repository;


    private final DocumentRepositoryAsync crudRepository;

    private final ClassRepresentation classRepresentation;

    private final DocumentQueryParser queryParser;

    private final DocumentQueryDeleteParser queryDeleteParser;


    DocumentCrudRepositoryAsyncProxy(DocumentTemplateAsync repository, ClassRepresentations classRepresentations, Class<?> repositoryType) {
        this.repository = repository;
        this.crudRepository = new DocumentRepositoryAsync(repository);
        this.typeClass = Class.class.cast(ParameterizedType.class.cast(repositoryType.getGenericInterfaces()[0])
                .getActualTypeArguments()[0]);
        this.classRepresentation = classRepresentations.get(typeClass);
        this.queryParser = new DocumentQueryParser();
        this.queryDeleteParser = new DocumentQueryDeleteParser();
    }


    @Override
    public Object invoke(Object instance, Method method, Object[] args) throws Throwable {


        String methodName = method.getName();
        DocumentRepositoryType type = DocumentRepositoryType.of(method, args);

        switch (type) {
            case DEFAULT:
                return method.invoke(crudRepository, args);
            case FIND_BY:
                DocumentQuery query = queryParser.parse(methodName, args, classRepresentation);
                return executeQuery(getCallBack(args), query);
            case DELETE_BY:
                return executeDeleteBy(args, methodName);
            case DOCUMENT_QUERY:
                DocumentQuery documentQuery = DocumentRepositoryType.getDocumentQuery(args).get();
                return executeQuery(getCallBack(args), documentQuery);
            default:
                return null;
        }

    }

    private Object getCallBack(Object[] args) {
        return args[args.length - 1];
    }

    private Object executeQuery(Object arg, DocumentQuery query) {
        Object callBack = arg;
        if (Consumer.class.isInstance(callBack)) {
            repository.find(query, Consumer.class.cast(callBack));
        } else {
            throw new DynamicQueryException("On find async method you must put a java.util.function.Consumer" +
                    " as end parameter as callback");
        }
        return Void.class;
    }

    private Object executeDeleteBy(Object[] args, String methodName) {
        Object callBack = getCallBack(args);
        DocumentDeleteQuery query = queryDeleteParser.parse(methodName, args, classRepresentation);
        if (Consumer.class.isInstance(callBack)) {
            repository.delete(query, Consumer.class.cast(callBack));
        } else {
            repository.delete(query);
        }
        return Void.class;
    }


    class DocumentRepositoryAsync extends AbstractDocumentRepositoryAsync implements RepositoryAsync {

        private final DocumentTemplateAsync repository;

        DocumentRepositoryAsync(DocumentTemplateAsync repository) {
            this.repository = repository;
        }

        @Override
        protected DocumentTemplateAsync getDocumentRepository() {
            return repository;
        }
    }
}
