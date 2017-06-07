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
package org.jnosql.artemis.key.query;

import org.hamcrest.Matchers;
import org.jnosql.artemis.key.KeyValueTemplate;
import org.jnosql.artemis.model.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.lang.reflect.Proxy;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class KeyValueRepositoryProxyTest {

    @Mock
    private KeyValueTemplate repository;

    private UserRepository userRepository;

    @Before
    public void setUp() {

        KeyValueCrudRepositoryProxy handler = new KeyValueCrudRepositoryProxy(UserRepository.class, repository);
        userRepository = (UserRepository) Proxy.newProxyInstance(UserRepository.class.getClassLoader(),
                new Class[]{UserRepository.class},
                handler);
    }

    @Test
    public void shouldPut() {
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);

        User user = new User("ada", "Ada", 10);
        userRepository.put(user);
        Mockito.verify(repository).put(captor.capture());
        User value = captor.getValue();
        assertEquals(user, value);
    }

    @Test
    public void shouldPutWithDdl() {
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        ArgumentCaptor<Duration> durationCaptor = ArgumentCaptor.forClass(Duration.class);

        User user = new User("ada", "Ada", 10);
        userRepository.put(user, Duration.ofHours(2));
        Mockito.verify(repository).put(captor.capture(), durationCaptor.capture());
        User value = captor.getValue();
        assertEquals(user, value);
        assertEquals(Duration.ofHours(2), durationCaptor.getValue());
    }

    @Test
    public void shouldPutIterable() {
        ArgumentCaptor<Iterable> captor = ArgumentCaptor.forClass(Iterable.class);

        User user = new User("ada", "Ada", 10);
        userRepository.put(Collections.singleton(user));
        Mockito.verify(repository).put(captor.capture());
        User value = (User) captor.getValue().iterator().next();
        assertEquals(user, value);
    }

    @Test
    public void shouldPutIterableWithDdl() {
        ArgumentCaptor<Iterable> captor = ArgumentCaptor.forClass(Iterable.class);
        ArgumentCaptor<Duration> durationCaptor = ArgumentCaptor.forClass(Duration.class);

        User user = new User("ada", "Ada", 10);
        userRepository.put(Collections.singleton(user), Duration.ofHours(2));
        Mockito.verify(repository).put(captor.capture(), durationCaptor.capture());
        User value = (User) captor.getValue().iterator().next();
        assertEquals(user, value);
        assertEquals(Duration.ofHours(2), durationCaptor.getValue());
    }


    @Test
    public void shouldRemove() {
        userRepository.remove("key");
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(repository).remove(captor.capture());
        assertEquals("key", captor.getValue());
    }

    @Test
    public void shouldRemoveIterable() {
        userRepository.remove(Collections.singletonList("key"));
        ArgumentCaptor<Iterable> captor = ArgumentCaptor.forClass(Iterable.class);
        Mockito.verify(repository).remove(captor.capture());
        assertEquals("key", captor.getValue().iterator().next());
    }

    @Test
    public void shouldGet() {
        User user = new User("ada", "Ada", 10);
        Mockito.when(repository.get("key", User.class)).thenReturn(
                Optional.of(user));

        assertEquals(user, userRepository.get("key").get());
    }

    @Test
    public void shouldGetIterable() {
        User user = new User("ada", "Ada", 10);
        User user2 = new User("ada", "Ada", 10);
        List<String> keys = Arrays.asList("key", "key2");
        Mockito.when(repository.get(keys, User.class)).thenReturn(
                Arrays.asList(user, user2));

        assertThat(userRepository.get(keys), Matchers.containsInAnyOrder(user, user2));
    }

    interface UserRepository extends KeyValueRepository<User> {

    }

}