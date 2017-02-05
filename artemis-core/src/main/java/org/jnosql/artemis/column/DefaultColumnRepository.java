/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jnosql.artemis.column;

import org.jnosql.diana.api.column.ColumnDeleteQuery;
import org.jnosql.diana.api.column.ColumnEntity;
import org.jnosql.diana.api.column.ColumnFamilyManager;
import org.jnosql.diana.api.column.ColumnQuery;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

/**
 * The default implementation of {@link ColumnRepository}
 */
@SuppressWarnings("unchecked")
@ApplicationScoped
@ColumnRepositoryInterceptor
class DefaultColumnRepository implements ColumnRepository {

    private ColumnEntityConverter converter;

    private Instance<ColumnFamilyManager> manager;


    private ColumnWorkflow flow;

    @Inject
    DefaultColumnRepository(ColumnEntityConverter converter, Instance<ColumnFamilyManager> manager, ColumnWorkflow flow) {
        this.converter = converter;
        this.manager = manager;
        this.flow = flow;
    }

    DefaultColumnRepository() {
    }

    @Override
    public <T> T save(T entity) throws NullPointerException {
        Objects.requireNonNull(entity, "entity is required");
        UnaryOperator<ColumnEntity> save = e -> manager.get().save(e);
        return flow.flow(entity, save);
    }


    @Override
    public <T> T save(T entity, Duration ttl) {
        Objects.requireNonNull(entity, "entity is required");
        Objects.requireNonNull(ttl, "ttl is required");
        UnaryOperator<ColumnEntity> save = e -> manager.get().save(e, ttl);
        return flow.flow(entity, save);
    }


    @Override
    public <T> T update(T entity) {
        Objects.requireNonNull(entity, "entity is required");
        UnaryOperator<ColumnEntity> save = e -> manager.get().update(e);
        return flow.flow(entity, save);
    }


    @Override
    public void delete(ColumnDeleteQuery query) {
        Objects.requireNonNull(query, "query is required");
        manager.get().delete(query);
    }


    @Override
    public <T> List<T> find(ColumnQuery query) throws NullPointerException {
        Objects.requireNonNull(query, "query is required");
        List<ColumnEntity> entities = manager.get().find(query);
        Function<ColumnEntity, T> function = e -> converter.toEntity(e);
        return entities.stream().map(function).collect(Collectors.toList());
    }

}
