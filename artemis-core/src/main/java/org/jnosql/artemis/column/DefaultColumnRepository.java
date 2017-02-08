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

import org.jnosql.diana.api.column.ColumnFamilyManager;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

/**
 * The default implementation of {@link ColumnRepository}
 */
@SuppressWarnings("unchecked")
@ColumnRepositoryInterceptor
class DefaultColumnRepository extends AbstractColumnRepository {

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
    protected ColumnEntityConverter getConverter() {
        return converter;
    }

    @Override
    protected ColumnFamilyManager getManager() {
        return manager.get();
    }

    @Override
    protected ColumnWorkflow getFlow() {
        return flow;
    }
}
