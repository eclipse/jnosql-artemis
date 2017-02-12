/*
 * Copyright 2017 Eclipse Foundation
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

import java.time.Duration;
import java.util.List;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

public abstract class AbstractColumnRepository implements ColumnRepository {


    protected abstract ColumnEntityConverter getConverter();

    protected abstract ColumnFamilyManager getManager();

    protected abstract ColumnWorkflow getFlow();


    @Override
    public <T> T save(T entity) throws NullPointerException {
        requireNonNull(entity, "entity is required");
        UnaryOperator<ColumnEntity> save = e -> getManager().save(e);
        return getFlow().flow(entity, save);
    }


    @Override
    public <T> T save(T entity, Duration ttl) {
        requireNonNull(entity, "entity is required");
        requireNonNull(ttl, "ttl is required");
        UnaryOperator<ColumnEntity> save = e -> getManager().save(e, ttl);
        return getFlow().flow(entity, save);
    }


    @Override
    public <T> T update(T entity) {
        requireNonNull(entity, "entity is required");
        UnaryOperator<ColumnEntity> save = e -> getManager().update(e);
        return getFlow().flow(entity, save);
    }


    @Override
    public void delete(ColumnDeleteQuery query) {
        requireNonNull(query, "query is required");
        getManager().delete(query);
    }


    @Override
    public <T> List<T> find(ColumnQuery query) throws NullPointerException {
        requireNonNull(query, "query is required");
        List<ColumnEntity> entities = getManager().find(query);
        Function<ColumnEntity, T> function = e -> getConverter().toEntity(e);
        return entities.stream().map(function).collect(Collectors.toList());
    }
}
