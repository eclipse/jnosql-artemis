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
import org.jnosql.diana.api.document.DocumentDeleteQuery;
import org.jnosql.diana.api.document.DocumentQuery;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;

import static org.jnosql.artemis.document.query.DocumentRepositoryType.getDocumentDeleteQuery;
import static org.jnosql.artemis.document.query.DocumentRepositoryType.getDocumentQuery;
import static org.jnosql.artemis.document.query.ReturnTypeConverterUtil.returnObject;


/**
 * Proxy handle to generate {@link Repository}
 *
 * @param <T> the type
 */
class DocumentRepositoryProxy<T> implements InvocationHandler {

    private final Class<T> typeClass;

    private final DocumentTemplate template;


    private final DocumentRepository crudRepository;

    private final ClassRepresentation classRepresentation;

    private final DocumentQueryParser queryParser;

    private final DocumentQueryDeleteParser deleteQueryParser;


    DocumentRepositoryProxy(DocumentTemplate template, ClassRepresentations classRepresentations, Class<?> repositoryType) {
        this.template = template;
        this.crudRepository = new DocumentRepository(template);
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
                return returnObject(query, template, typeClass, method);
            case DELETE_BY:
                template.delete(deleteQueryParser.parse(methodName, args, classRepresentation));
                return null;
            case QUERY:
                DocumentQuery documentQuery = getDocumentQuery(args).get();
                return returnObject(documentQuery, template, typeClass, method);
            case QUERY_DELETE:
                DocumentDeleteQuery deleteQuery = getDocumentDeleteQuery(args).get();
                template.delete(deleteQuery);
                return null;
            default:
                return null;
        }
    }


    class DocumentRepository extends AbstractDocumentRepository implements Repository {

        private final DocumentTemplate template;

        DocumentRepository(DocumentTemplate template) {
            this.template = template;
        }

        @Override
        protected DocumentTemplate getTemplate() {
            return template;
        }
    }
}
