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

import org.jnosql.artemis.CrudRepository;
import org.jnosql.artemis.column.ColumnRepository;

import java.time.Duration;

/**
 * The {@link CrudRepository} template method
 */
public abstract class AbstractColumnCrudRepository implements CrudRepository {

    protected abstract ColumnRepository getColumnRepository();

    @Override
    public Object save(Object entity) throws NullPointerException {
        return getColumnRepository().save(entity);
    }

    @Override
    public Object save(Object entity, Duration ttl) {
        return getColumnRepository().save(entity, ttl);
    }

    @Override
    public Iterable save(Iterable entities) throws NullPointerException {
        return getColumnRepository().save(entities);
    }

    @Override
    public Iterable save(Iterable entities, Duration ttl) throws NullPointerException {
        return getColumnRepository().save(entities, ttl);
    }

    @Override
    public Object update(Object entity) {
        return getColumnRepository().update(entity);
    }

    @Override
    public Iterable update(Iterable entities) throws NullPointerException {
        return getColumnRepository().update(entities);
    }
}
