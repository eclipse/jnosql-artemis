/*
 * Copyright 2017 Otavio Santana and others
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

import javax.enterprise.inject.Vetoed;
import javax.inject.Inject;
import java.util.Objects;

/**
 * The default implementation of {@link ColumnRepositoryProducer}
 */
class DefaultColumnRepositoryProducer implements ColumnRepositoryProducer {


    @Inject
    private ColumnEntityConverter converter;

    @Inject
    private ColumnWorkflow columnWorkflow;


    @Override
    public ColumnRepository get(ColumnFamilyManager columnFamilyManager) throws NullPointerException {
        Objects.requireNonNull(columnFamilyManager, "columnFamilyManager is required");
        return new ProducerColumnRepository(converter, columnWorkflow, columnFamilyManager);
    }


    @Vetoed
    static class ProducerColumnRepository extends AbstractColumnRepository {

        private ColumnEntityConverter converter;

        private ColumnWorkflow columnWorkflow;

        private ColumnFamilyManager columnFamilyManager;

        ProducerColumnRepository(ColumnEntityConverter converter, ColumnWorkflow columnWorkflow, ColumnFamilyManager columnFamilyManager) {
            this.converter = converter;
            this.columnWorkflow = columnWorkflow;
            this.columnFamilyManager = columnFamilyManager;
        }

        ProducerColumnRepository() {
        }

        @Override
        protected ColumnEntityConverter getConverter() {
            return converter;
        }

        @Override
        protected ColumnFamilyManager getManager() {
            return columnFamilyManager;
        }

        @Override
        protected ColumnWorkflow getFlow() {
            return columnWorkflow;
        }
    }
}
