/*
 *  Copyright (c) 2017 Otávio Santana and others
 *   All rights reserved. This program and the accompanying materials
 *   are made available under the terms of the Eclipse Public License v1.0
 *   and Apache License v2.0 which accompanies this distribution.
 *   The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 *   and the Apache License v2.0 is available at http://www.opensource.org/licenses/apache2.0.php.
 *
 *   You may elect to redistribute this code under either of these licenses.
 *
 *   Contributors:
 *
 *   Otavio Santana
 */
package org.jnosql.artemis.key;

import org.jnosql.artemis.CDIJUnitRunner;
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

import static org.junit.jupiter.api.Assertions.assertEquals;


@RunWith(CDIJUnitRunner.class)
public class DefaultKeyValueTemplateTest {

    @Inject
    private KeyValueEntityConverter converter;

    @Inject
    private KeyValueWorkflow flow;

    private BucketManager manager;

    private ArgumentCaptor<KeyValueEntity> captor;

    private KeyValueTemplate subject;


    @Before
    public void setUp() {
        this.manager = Mockito.mock(BucketManager.class);
        Instance<BucketManager> instance = Mockito.mock(Instance.class);
        Mockito.when(instance.get()).thenReturn(manager);
        captor = ArgumentCaptor.forClass(KeyValueEntity.class);
        this.subject = new DefaultKeyValueTemplate(converter, instance, flow);
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