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
import org.jnosql.diana.api.ExecuteAsyncQueryException;

import java.time.Duration;
import java.util.function.Consumer;

/**
 * The template method to {@link RepositoryAsync}
 */
public abstract class AbstractColumnRepositoryAsync<T> implements RepositoryAsync<T> {

    protected abstract ColumnTemplateAsync getTemplate();

    @Override
    public void save(T entity) throws ExecuteAsyncQueryException, UnsupportedOperationException, NullPointerException {
        getTemplate().save(entity);
    }

    @Override
    public void save(T entity, Duration ttl) throws ExecuteAsyncQueryException, UnsupportedOperationException, NullPointerException {
        getTemplate().save(entity, ttl);
    }

    @Override
    public void save(Iterable entities) throws ExecuteAsyncQueryException, UnsupportedOperationException, NullPointerException {
        getTemplate().save(entities);
    }

    @Override
    public void save(Iterable entities, Duration ttl) throws NullPointerException {
        getTemplate().save(entities, ttl);
    }

    @Override
    public void update(Iterable entities) throws ExecuteAsyncQueryException, UnsupportedOperationException, NullPointerException {
        getTemplate().update(entities);
    }

    @Override
    public void update(Object entity) throws ExecuteAsyncQueryException, UnsupportedOperationException, NullPointerException {
        getTemplate().save(entity);
    }

    @Override
    public void update(Object entity, Consumer callBack) throws ExecuteAsyncQueryException, UnsupportedOperationException, NullPointerException {
        getTemplate().update(entity, callBack);
    }

    @Override
    public void save(Object entity, Duration ttl, Consumer callBack) throws ExecuteAsyncQueryException, UnsupportedOperationException, NullPointerException {
        getTemplate().save(entity, ttl, callBack);
    }

    @Override
    public void save(Object entity, Consumer callBack) throws ExecuteAsyncQueryException, UnsupportedOperationException, NullPointerException {
        getTemplate().save(entity, callBack);
    }
}
