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
package org.jnosql.artemis.key;


import org.jnosql.artemis.EntityPostPersit;
import org.jnosql.artemis.EntityPrePersist;
import org.jnosql.diana.api.key.KeyValueEntity;

import javax.enterprise.event.Event;
import javax.inject.Inject;

class DefaultKeyValueEventPersistManager implements KeyValueEventPersistManager {

    @Inject
    private Event<KeyValueEntityPrePersist> keyValueEntityPrePersistEvent;

    @Inject
    private Event<KeyValueEntityPostPersist> keyValueEntityPostPersistEvent;

    @Inject
    private Event<EntityPrePersist> entityPrePersistEvent;

    @Inject
    private Event<EntityPostPersit> entityPostPersitEvent;

    @Inject
    private Event<EntityKeyValuePrePersist> entityKeyValuePrePersist;

    @Inject
    private Event<EntityKeyValuePostPersist> entityKeyValuePostPersist;


    @Override
    public void firePreKeyValue(KeyValueEntity<?> entity) {
        keyValueEntityPrePersistEvent.fire(KeyValueEntityPrePersist.of(entity));
    }

    @Override
    public void firePostKeyValue(KeyValueEntity<?> entity) {
        keyValueEntityPostPersistEvent.fire(KeyValueEntityPostPersist.of(entity));
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
    public <T> void firePreKeyValueEntity(T entity) {
        entityKeyValuePrePersist.fire(EntityKeyValuePrePersist.of(entity));
    }

    @Override
    public <T> void firePostKeyValueEntity(T entity) {
        entityKeyValuePostPersist.fire(EntityKeyValuePostPersist.of(entity));
    }
}
