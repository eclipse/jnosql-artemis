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


import org.jnosql.artemis.EntityPostPersit;
import org.jnosql.artemis.EntityPrePersist;
import org.jnosql.diana.api.column.ColumnDeleteQuery;
import org.jnosql.diana.api.column.ColumnEntity;
import org.jnosql.diana.api.column.ColumnQuery;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

/**
 * The default implementation of {@link ColumnEventPersistManager}
 */
@ApplicationScoped
class DefaultColumnEventPersistManager implements ColumnEventPersistManager {
    @Inject
    private Event<ColumnEntityPrePersist> documentEntityPrePersistEvent;

    @Inject
    private Event<ColumnEntityPostPersist> documentEntityPostPersistEvent;

    @Inject
    private Event<EntityPrePersist> entityPrePersistEvent;

    @Inject
    private Event<EntityPostPersit> entityPostPersitEvent;

    @Inject
    private Event<EntityColumnPrePersist> entityColumnPrePersist;

    @Inject
    private Event<EntityColumnPostPersist> entityColumnPostPersist;

    @Inject
    private Event<ColumnQueryExecute> columnQueryExecute;

    @Inject
    private Event<ColumnDeleteQueryExecute> columnDeleteQueryExecute;

    @Override
    public void firePreColumn(ColumnEntity entity) {
        documentEntityPrePersistEvent.fire(ColumnEntityPrePersist.of(entity));
    }

    @Override
    public void firePostColumn(ColumnEntity entity) {
        documentEntityPostPersistEvent.fire(ColumnEntityPostPersist.of(entity));
    }

    @Override
    public <T> void firePreEntity(T entity) {
        entityPrePersistEvent.fire(EntityPrePersist.of(entity));
    }

    @Override
    public <T> void firePostEntity(T entity) {
        entityPostPersitEvent.fire(EntityPostPersit.of(entity));
    }

    @Override
    public <T> void firePreColumnEntity(T entity) {
        entityColumnPrePersist.fire(EntityColumnPrePersist.of(entity));
    }

    @Override
    public <T> void firePostColumnEntity(T entity) {
        entityColumnPostPersist.fire(EntityColumnPostPersist.of(entity));
    }

    @Override
    public void firePreQuery(ColumnQuery query) {
        columnQueryExecute.fire(ColumnQueryExecute.of(query));
    }

    @Override
    public void firePreDeleteQuery(ColumnDeleteQuery query) {
        columnDeleteQueryExecute.fire(ColumnDeleteQueryExecute.of(query));
    }
}
