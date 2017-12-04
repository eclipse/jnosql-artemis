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


import org.jnosql.artemis.Repository;
import org.jnosql.artemis.document.DocumentTemplate;
import org.jnosql.artemis.reflection.ClassRepresentation;
import org.jnosql.artemis.reflection.ClassRepresentations;
import org.jnosql.artemis.reflection.Reflections;

import java.lang.reflect.ParameterizedType;


/**
 * Proxy handle to generate {@link org.jnosql.artemis.Repository}
 *
 * @param <T> the type
 */
class DocumentRepositoryProxy<T> extends AbstractDocumentRepositoryProxy<T> {

    private final DocumentTemplate template;

    private final DocumentRepository repository;

    private final ClassRepresentation classRepresentation;

    private final DocumentQueryParser queryParser;

    private final DocumentQueryDeleteParser deleteQueryParser;


    DocumentRepositoryProxy(DocumentTemplate template, ClassRepresentations classRepresentations,
                            Class<?> repositoryType, Reflections reflections) {
        this.template = template;
        Class<T> typeClass = Class.class.cast(ParameterizedType.class.cast(repositoryType.getGenericInterfaces()[0])
                .getActualTypeArguments()[0]);
        this.classRepresentation = classRepresentations.get(typeClass);
        this.repository = new DocumentRepository(template, classRepresentation, reflections);
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

        private final ClassRepresentation classRepresentation;

        private final Reflections reflections;

        DocumentRepository(DocumentTemplate template, ClassRepresentation classRepresentation, Reflections reflections) {
            this.template = template;
            this.classRepresentation = classRepresentation;
            this.reflections = reflections;
        }

        @Override
        protected DocumentTemplate getTemplate() {
            return template;
        }

        @Override
        protected ClassRepresentation getClassRepresentation() {
            return classRepresentation;
        }

        @Override
        protected Reflections getReflections() {
            return reflections;
        }


    }
}
