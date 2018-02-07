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
import org.jnosql.artemis.MockitoExtension;
import org.jnosql.artemis.model.User;
import org.jnosql.diana.api.Value;
import org.jnosql.diana.api.key.BucketManager;
import org.jnosql.diana.api.key.KeyValueEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.StreamSupport.stream;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;


@ExtendWith(CDIExtension.class)
@ExtendWith(MockitoExtension.class)
public class DefaultKeyValueTemplateTest {

    private static final String KEY = "otaviojava";
    @Inject
    private KeyValueEntityConverter converter;

    @Inject
    private KeyValueWorkflow flow;

    @Mock
    private BucketManager manager;

    @Captor
    private ArgumentCaptor<KeyValueEntity> captor;

    private KeyValueTemplate subject;


    @BeforeEach
    public void setUp() {
        Instance<BucketManager> instance = Mockito.mock(Instance.class);
        when(instance.get()).thenReturn(manager);
        this.subject = new DefaultKeyValueTemplate(converter, instance, flow);
    }

    @Test
    public void shouldCheckNullParametersInPut() {
        User user = new User(KEY, "otavio", 27);
        assertThrows(NullPointerException.class, () -> subject.put(null));
        assertThrows(NullPointerException.class, () -> subject.put(null, null));
        assertThrows(NullPointerException.class, () -> subject.put(null, Duration.ofSeconds(2L)));
        assertThrows(NullPointerException.class, () -> subject.put(user, null));
        assertThrows(NullPointerException.class, () -> subject.put((Iterable<? extends Object>) null));
        assertThrows(NullPointerException.class, () -> subject.put((Iterable<? extends Object>) null, null));
    }


    @Test
    public void shouldPut() {
        User user = new User(KEY, "otavio", 27);
        subject.put(user);
        Mockito.verify(manager).put(captor.capture());
        KeyValueEntity entity = captor.getValue();
        assertEquals(KEY, entity.getKey());
        assertEquals(user, entity.getValue().get());
    }

    @Test
    public void shouldPutIterable() {
        User user = new User(KEY, "otavio", 27);
        subject.put(singletonList(user));
        Mockito.verify(manager).put(captor.capture());
        KeyValueEntity entity = captor.getValue();
        assertEquals(KEY, entity.getKey());
        assertEquals(user, entity.getValue().get());
    }

    @Test
    public void shouldPutTTL() {

        Duration duration = Duration.ofSeconds(2L);
        User user = new User(KEY, "otavio", 27);
        subject.put(user, duration);

        Mockito.verify(manager).put(captor.capture(), Mockito.eq(duration));
        KeyValueEntity entity = captor.getValue();
        assertEquals(KEY, entity.getKey());
        assertEquals(user, entity.getValue().get());
    }

    @Test
    public void shouldPutTTLIterable() {

        Duration duration = Duration.ofSeconds(2L);
        User user = new User(KEY, "otavio", 27);
        subject.put(singletonList(user), duration);

        Mockito.verify(manager).put(captor.capture(), Mockito.eq(duration));
        KeyValueEntity entity = captor.getValue();
        assertEquals(KEY, entity.getKey());
        assertEquals(user, entity.getValue().get());
    }

    @Test
    public void shouldCheckNullParametersInGet() {
        User user = new User(KEY, "otavio", 27);
        assertThrows(NullPointerException.class, () -> subject.get(null, null));
        assertThrows(NullPointerException.class, () -> subject.get(user, null));
        assertThrows(NullPointerException.class, () -> subject.get(null, User.class));
    }


    @Test
    public void shouldGet() {
        User user = new User(KEY, "otavio", 27);

        when(manager.get(KEY)).thenReturn(Optional.of(Value.of(user)));
        Optional<User> userOptional = subject.get(KEY, User.class);

        assertTrue(userOptional.isPresent());
        assertEquals(user, userOptional.get());
    }

    @Test
    public void shouldGetIterable() {
        User user = new User(KEY, "otavio", 27);

        when(manager.get(singletonList(KEY))).thenReturn(singletonList(Value.of(user)));
        List<User> userOptional = stream(subject.get(singletonList(KEY), User.class).spliterator(), false)
                .collect(toList());

        assertFalse(userOptional.isEmpty());
        assertEquals(user, userOptional.get(0));
    }

    @Test
    public void shouldCheckNullParametersInRemove() {
        assertThrows(NullPointerException.class, () -> subject.remove(null));
        assertThrows(NullPointerException.class, () -> subject.remove((Iterable<? extends Object>) null));
    }


    @Test
    public void shouldRemove() {
        subject.remove(KEY);
        Mockito.verify(manager).remove(KEY);
    }

    @Test
    public void shouldRemoveIterable() {
        subject.remove(singletonList(KEY));
        Mockito.verify(manager).remove(singletonList(KEY));
    }
}