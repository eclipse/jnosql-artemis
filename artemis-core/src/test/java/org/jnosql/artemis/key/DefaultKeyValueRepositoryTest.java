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
package org.jnosql.artemis.key;

import org.jnosql.artemis.WeldJUnit4Runner;
import org.jnosql.artemis.model.User;
import org.jnosql.diana.api.key.BucketManager;
import org.jnosql.diana.api.key.KeyValueEntity;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import static org.junit.Assert.assertEquals;


@RunWith(WeldJUnit4Runner.class)
public class DefaultKeyValueRepositoryTest {

    @Inject
    private KeyValueEntityConverter converter;

    @Inject
    private KeyValueWorkflow flow;

    private BucketManager manager;

    private ArgumentCaptor<KeyValueEntity> captor;

    private KeyValueRepository subject;


    @Before
    public void setUp() {
        this.manager = Mockito.mock(BucketManager.class);
        Instance<BucketManager> instance = Mockito.mock(Instance.class);
        Mockito.when(instance.get()).thenReturn(manager);
        captor = ArgumentCaptor.forClass(KeyValueEntity.class);
        this.subject = new DefaultKeyValueRepository(converter, instance, flow);
    }


    @Test
    public void shouldPut() {
        User user = new User("otaviojava", "otavio", 27);
        subject.put(user);
        Mockito.verify(manager).put(captor.capture());
        KeyValueEntity entity = captor.getValue();
        assertEquals("otaviojava", entity.getKey());
        assertEquals(user, entity.getValue().get());
    }
}