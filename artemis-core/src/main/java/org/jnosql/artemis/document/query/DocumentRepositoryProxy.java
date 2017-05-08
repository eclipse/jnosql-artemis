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

import java.lang.reflect.ParameterizedType;
import java.util.Optional;


/**
 * Proxy handle to generate {@link Repository}
 *
 * @param <T> the type
 */
class DocumentRepositoryProxy<T> extends AbstractDocumentRepositoryProxy<T> {

    private final Class<T> typeClass;

    private final DocumentTemplate template;

    private final DocumentRepository repository;

    private final ClassRepresentation classRepresentation;

    private final DocumentQueryParser queryParser;

    private final DocumentQueryDeleteParser deleteQueryParser;


    DocumentRepositoryProxy(DocumentTemplate template, ClassRepresentations classRepresentations, Class<?> repositoryType) {
        this.template = template;
        this.repository = new DocumentRepository(template);
        this.typeClass = Class.class.cast(ParameterizedType.class.cast(repositoryType.getGenericInterfaces()[0])
                .getActualTypeArguments()[0]);
        this.classRepresentation = classRepresentations.get(typeClass);
        this.queryParser = new DocumentQueryParser();
        this.deleteQueryParser = new DocumentQueryDeleteParser();
    }


    @Override
    protected Repository getRepository() {
        return repository;
    }

    @Override
    protected DocumentQueryParser getQueryParser() {
        return queryParser;
    }

    @Override
    protected DocumentTemplate getTemplate() {
        return template;
    }

    @Override
    protected DocumentQueryDeleteParser getDeleteParser() {
        return deleteQueryParser;
    }

    @Override
    protected ClassRepresentation getClassRepresentation() {
        return classRepresentation;
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

        @Override
        public void deleteById(Object o) throws NullPointerException {

        }

        @Override
        public void deleteById(Iterable iterable) throws NullPointerException {

        }

        @Override
        public void delete(Iterable entities) throws NullPointerException {

        }

        @Override
        public void delete(Object entity) throws NullPointerException {

        }

        @Override
        public Optional findById(Object o) throws NullPointerException {
            return null;
        }

        @Override
        public Iterable findById(Iterable iterable) throws NullPointerException {
            return null;
        }

        @Override
        public boolean existsById(Object o) throws NullPointerException {
            return false;
        }
    }
}
