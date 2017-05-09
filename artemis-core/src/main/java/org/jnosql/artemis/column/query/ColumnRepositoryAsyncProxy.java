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


import org.jnosql.artemis.RepositoryAsync;
import org.jnosql.artemis.column.ColumnTemplateAsync;
import org.jnosql.artemis.reflection.ClassRepresentation;
import org.jnosql.artemis.reflection.ClassRepresentations;
import org.jnosql.artemis.reflection.Reflections;

import java.lang.reflect.ParameterizedType;

/**
 * Proxy handle to generate {@link RepositoryAsync}
 *
 * @param <T> the type
 */
class ColumnRepositoryAsyncProxy<T> extends AbstractColumnRepositoryAsyncProxy<T> {

    private final Class<T> typeClass;

    private final ColumnTemplateAsync template;

    private final ColumnRepositoryAsync repository;

    private final ClassRepresentation classRepresentation;

    private final ColumnQueryParser queryParser;

    private final ColumnQueryDeleteParser deleteParser;


    ColumnRepositoryAsyncProxy(ColumnTemplateAsync template, ClassRepresentations classRepresentations,
                               Class<?> repositoryType, Reflections reflections) {
        this.template = template;
        this.typeClass = Class.class.cast(ParameterizedType.class.cast(repositoryType.getGenericInterfaces()[0])
                .getActualTypeArguments()[0]);
        this.classRepresentation = classRepresentations.get(typeClass);
        this.repository = new ColumnRepositoryAsync(template, reflections, classRepresentation);
        this.queryParser = new ColumnQueryParser();
        this.deleteParser = new ColumnQueryDeleteParser();
    }

    @Override
    protected RepositoryAsync getRepository() {
        return repository;
    }

    @Override
    protected ClassRepresentation getClassRepresentation() {
        return classRepresentation;
    }

    @Override
    protected ColumnQueryParser getQueryParser() {
        return queryParser;
    }

    @Override
    protected ColumnQueryDeleteParser getDeleteParser() {
        return deleteParser;
    }

    @Override
    protected ColumnTemplateAsync getTemplate() {
        return template;
    }


    class ColumnRepositoryAsync extends AbstractColumnRepositoryAsync implements RepositoryAsync {


        private final ColumnTemplateAsync template;

        private final Reflections reflections;

        private final ClassRepresentation classRepresentation;

        ColumnRepositoryAsync(ColumnTemplateAsync repository, Reflections reflections, ClassRepresentation classRepresentation) {
            this.template = repository;
            this.reflections = reflections;
            this.classRepresentation = classRepresentation;
        }

        @Override
        protected ColumnTemplateAsync getTemplate() {
            return template;
        }

        @Override
        protected Reflections getReflections() {
            return reflections;
        }

        @Override
        protected ClassRepresentation getClassRepresentation() {
            return classRepresentation;
        }


    }
}
