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


import org.jnosql.diana.api.column.ColumnFamilyManagerAsync;

import javax.enterprise.inject.Vetoed;
import javax.inject.Inject;
import java.util.Objects;

/**
 * The default implementation of {@link ColumnRepositoryAsyncProducer}
 */
class DefaultColumnRepositoryAsyncProducer implements ColumnRepositoryAsyncProducer {

    @Inject
    private ColumnEntityConverter converter;

    @Override
    public ColumnRepositoryAsync get(ColumnFamilyManagerAsync columnFamilyManager) throws NullPointerException {
        Objects.requireNonNull(columnFamilyManager, "columnFamilyManager is required");
        return new ProducerColumnRepositoryAsync(converter, columnFamilyManager);
    }

    @Vetoed
    @ColumnRepositoryInterceptor
    static class ProducerColumnRepositoryAsync extends AbstractColumnRepositoryAsync {

        private ColumnEntityConverter converter;

        private ColumnFamilyManagerAsync columnFamilyManager;

        ProducerColumnRepositoryAsync(ColumnEntityConverter converter, ColumnFamilyManagerAsync columnFamilyManager) {
            this.converter = converter;
            this.columnFamilyManager = columnFamilyManager;
        }

        ProducerColumnRepositoryAsync() {
        }

        @Override
        protected ColumnEntityConverter getConverter() {
            return converter;
        }

        @Override
        protected ColumnFamilyManagerAsync getManager() {
            return columnFamilyManager;
        }
    }
}
