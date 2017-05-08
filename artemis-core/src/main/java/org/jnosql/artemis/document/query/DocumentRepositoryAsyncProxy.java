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


import org.jnosql.artemis.RepositoryAsync;
import org.jnosql.artemis.document.DocumentTemplateAsync;
import org.jnosql.artemis.reflection.ClassRepresentation;
import org.jnosql.artemis.reflection.ClassRepresentations;

import java.lang.reflect.ParameterizedType;
import java.util.function.Consumer;

/**
 * Proxy handle to generate {@link RepositoryAsync}
 *
 * @param <T> the type
 */
class DocumentRepositoryAsyncProxy<T> extends AbstractDocumentRepositoryAsyncProxy {

    private final Class<T> typeClass;

    private final DocumentTemplateAsync template;


    private final DocumentRepositoryAsync repository;

    private final ClassRepresentation classRepresentation;

    private final DocumentQueryParser queryParser;

    private final DocumentQueryDeleteParser deleteParser;


    DocumentRepositoryAsyncProxy(DocumentTemplateAsync template, ClassRepresentations classRepresentations, Class<?> repositoryType) {
        this.template = template;
        this.repository = new DocumentRepositoryAsync(template);
        this.typeClass = Class.class.cast(ParameterizedType.class.cast(repositoryType.getGenericInterfaces()[0])
                .getActualTypeArguments()[0]);
        this.classRepresentation = classRepresentations.get(typeClass);
        this.queryParser = new DocumentQueryParser();
        this.deleteParser = new DocumentQueryDeleteParser();
    }

    @Override
    protected RepositoryAsync getRepository() {
        return repository;
    }

    @Override
    protected DocumentQueryParser getQueryParser() {
        return queryParser;
    }

    @Override
    protected DocumentTemplateAsync getTemplate() {
        return template;
    }

    @Override
    protected DocumentQueryDeleteParser getDeleteParser() {
        return deleteParser;
    }

    @Override
    protected ClassRepresentation getClassRepresentation() {
        return classRepresentation;
    }

    class DocumentRepositoryAsync extends AbstractDocumentRepositoryAsync implements RepositoryAsync {

        private final DocumentTemplateAsync template;

        DocumentRepositoryAsync(DocumentTemplateAsync template) {
            this.template = template;
        }

        @Override
        protected DocumentTemplateAsync getTemplate() {
            return template;
        }

        @Override
        public void deleteById(Object o) throws NullPointerException {

        }

        @Override
        public void delete(Iterable entities) throws NullPointerException {

        }

        @Override
        public void delete(Object entity) throws NullPointerException {

        }

        @Override
        public void existsById(Object o, Consumer callBack) throws NullPointerException {

        }

        @Override
        public void findById(Iterable iterable, Consumer callBack) throws NullPointerException {

        }

        @Override
        public void findById(Object o, Consumer callBack) throws NullPointerException {

        }
    }
}
