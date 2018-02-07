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

import org.jnosql.artemis.CDIExtension;
import org.jnosql.artemis.model.User;
import org.jnosql.diana.api.key.BucketManager;
import org.jnosql.diana.api.key.KeyValueEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


@ExtendWith(CDIExtension.class)
public class DefaultKeyValueTemplateTest {

    @Inject
    private KeyValueEntityConverter converter;

    @Inject
    private KeyValueWorkflow flow;

    private BucketManager manager;

    private ArgumentCaptor<KeyValueEntity> captor;

    private KeyValueTemplate subject;


    @BeforeEach
    public void setUp() {
        this.manager = Mockito.mock(BucketManager.class);
        Instance<BucketManager> instance = Mockito.mock(Instance.class);
        Mockito.when(instance.get()).thenReturn(manager);
        captor = ArgumentCaptor.forClass(KeyValueEntity.class);
        this.subject = new DefaultKeyValueTemplate(converter, instance, flow);
    }

    @Test
    public void shouldCheckNullParametersInPut() {
        User user = new User("otaviojava", "otavio", 27);
        assertThrows(NullPointerException.class, () -> subject.put(null));
        assertThrows(NullPointerException.class, () -> subject.put(null, null));
        assertThrows(NullPointerException.class, () -> subject.put(null, Duration.ofSeconds(2L)));
        assertThrows(NullPointerException.class, () -> subject.put(user, null));
        assertThrows(NullPointerException.class, () -> subject.put((Iterable<? extends Object>) null));
        assertThrows(NullPointerException.class, () -> subject.put((Iterable<? extends Object>) null, null));
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

    @Test
    public void shouldPutTTL() {

        Duration duration = Duration.ofSeconds(2L);
        User user = new User("otaviojava", "otavio", 27);
        subject.put(user, duration);

        Mockito.verify(manager).put(captor.capture(), Mockito.eq(duration));
        KeyValueEntity entity = captor.getValue();
        assertEquals("otaviojava", entity.getKey());
        assertEquals(user, entity.getValue().get());
    }
}