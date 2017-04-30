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
import org.jnosql.artemis.reflection.ClassRepresentations;
import org.jnosql.diana.api.document.DocumentQuery;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;


/**
 * Proxy handle to generate {@link Repository}
 *
 * @param <T> the type
 */
class DocumentCrudRepositoryProxy<T> implements InvocationHandler {

    private final Class<T> typeClass;

    private final DocumentTemplate repository;


    private final DocumentRepository crudRepository;

    private final ClassRepresentation classRepresentation;

    private final DocumentQueryParser queryParser;

    private final DocumentQueryDeleteParser deleteQueryParser;


    DocumentCrudRepositoryProxy(DocumentTemplate repository, ClassRepresentations classRepresentations, Class<?> repositoryType) {
        this.repository = repository;
        this.crudRepository = new DocumentRepository(repository);
        this.typeClass = Class.class.cast(ParameterizedType.class.cast(repositoryType.getGenericInterfaces()[0])
                .getActualTypeArguments()[0]);
        this.classRepresentation = classRepresentations.get(typeClass);
        this.queryParser = new DocumentQueryParser();
        this.deleteQueryParser = new DocumentQueryDeleteParser();
    }


    @Override
    public Object invoke(Object o, Method method, Object[] args) throws Throwable {

        String methodName = method.getName();
        DocumentRepositoryType type = DocumentRepositoryType.of(method, args);

        switch (type) {
            case DEFAULT:
                return method.invoke(crudRepository, args);
            case FIND_BY:
                DocumentQuery query = queryParser.parse(methodName, args, classRepresentation);
                return ReturnTypeConverterUtil.returnObject(query, repository, typeClass, method);
            case DELETE_BY:
                repository.delete(deleteQueryParser.parse(methodName, args, classRepresentation));
                return null;
            case DOCUMENT_QUERY:
                DocumentQuery documentQuery = DocumentRepositoryType.getDocumentQuery(args).get();
                return ReturnTypeConverterUtil.returnObject(documentQuery, repository, typeClass, method);
            default:
                return null;
        }
    }


    class DocumentRepository extends AbstractDocumentRepository implements Repository {

        private final DocumentTemplate repository;

        DocumentRepository(DocumentTemplate repository) {
            this.repository = repository;
        }

        @Override
        protected DocumentTemplate getDocumentRepository() {
            return repository;
        }
    }
}
