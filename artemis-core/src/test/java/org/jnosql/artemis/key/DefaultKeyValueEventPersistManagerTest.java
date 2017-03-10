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
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.enterprise.event.Event;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;


@RunWith(MockitoJUnitRunner.class)
public class DefaultKeyValueEventPersistManagerTest {

    @InjectMocks
    private DefaultKeyValueEventPersistManager subject;

    @Mock
    private Event<KeyValueEntityPrePersist> keyValueEntityPrePersistEvent;

    @Mock
    private Event<KeyValueEntityPostPersist> keyValueEntityPostPersistEvent;

    @Mock
    private Event<EntityPrePersist> entityPrePersistEvent;

    @Mock
    private Event<EntityPostPersit> entityPostPersitEvent;

    @Mock
    private Event<EntityKeyValuePrePersist> entityKeyValuePrePersist;

    @Mock
    private Event<EntityKeyValuePostPersist> entityKeyValuePostPersist;

    @Test
    public void shouldFirePreColumn() {
        KeyValueEntity entity = KeyValueEntity.of("key", "value");
        subject.firePreKeyValue(entity);
        ArgumentCaptor<KeyValueEntityPrePersist> captor = ArgumentCaptor.forClass(KeyValueEntityPrePersist.class);
        verify(keyValueEntityPrePersistEvent).fire(captor.capture());

        KeyValueEntityPrePersist captorValue = captor.getValue();
        assertEquals(entity, captorValue.getEntity());
    }


    @Test
    public void shouldFirePostColumn() {
        KeyValueEntity entity = KeyValueEntity.of("key", "value");
        subject.firePostKeyValue(entity);
        ArgumentCaptor<KeyValueEntityPostPersist> captor = ArgumentCaptor.forClass(KeyValueEntityPostPersist.class);
        verify(keyValueEntityPostPersistEvent).fire(captor.capture());

        KeyValueEntityPostPersist captorValue = captor.getValue();
        assertEquals(entity, captorValue.getEntity());
    }

    @Test
    public void shouldFirePreEntity() {
        Jedi jedi = new Jedi();
        jedi.name = "Luke";
        subject.firePreEntity(jedi);
        ArgumentCaptor<EntityPrePersist> captor = ArgumentCaptor.forClass(EntityPrePersist.class);
        verify(entityPrePersistEvent).fire(captor.capture());
        EntityPrePersist value = captor.getValue();
        assertEquals(jedi, value.getValue());
    }

    @Test
    public void shouldFirePostEntity() {
        Jedi jedi = new Jedi();
        jedi.name = "Luke";
        subject.firePostEntity(jedi);
        ArgumentCaptor<EntityPostPersit> captor = ArgumentCaptor.forClass(EntityPostPersit.class);
        verify(entityPostPersitEvent).fire(captor.capture());
        EntityPostPersit value = captor.getValue();
        assertEquals(jedi, value.getValue());
    }

    @Test
    public void shouldFirePreKeyValueEntity() {
        Jedi jedi = new Jedi();
        jedi.name = "Luke";
        subject.firePreKeyValueEntity(jedi);
        ArgumentCaptor<EntityKeyValuePrePersist> captor = ArgumentCaptor.forClass(EntityKeyValuePrePersist.class);
        verify(entityKeyValuePrePersist).fire(captor.capture());
        EntityKeyValuePrePersist value = captor.getValue();
        assertEquals(jedi, value.getValue());
    }

    @Test
    public void shouldFirePostColumnEntity() {
        Jedi jedi = new Jedi();
        jedi.name = "Luke";
        subject.firePostKeyValueEntity(jedi);
        ArgumentCaptor<EntityKeyValuePostPersist> captor = ArgumentCaptor.forClass(EntityKeyValuePostPersist.class);
        verify(entityKeyValuePostPersist).fire(captor.capture());
        EntityKeyValuePostPersist value = captor.getValue();
        assertEquals(jedi, value.getValue());
    }

    class Jedi {
        private String name;
    }

}