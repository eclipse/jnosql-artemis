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
import org.jnosql.artemis.DynamicQueryException;
import org.jnosql.artemis.Repository;
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

        KeyValueRepositoryProxy handler = new KeyValueRepositoryProxy(UserRepository.class, repository);
        userRepository = (UserRepository) Proxy.newProxyInstance(UserRepository.class.getClassLoader(),
                new Class[]{UserRepository.class},
                handler);
    }

    @Test
    public void shouldSave() {
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);

        User user = new User("ada", "Ada", 10);
        userRepository.save(user);
        Mockito.verify(repository).put(captor.capture());
        User value = captor.getValue();
        assertEquals(user, value);
    }


    @Test
    public void shouldSaveIterable() {
        ArgumentCaptor<Iterable> captor = ArgumentCaptor.forClass(Iterable.class);

        User user = new User("ada", "Ada", 10);
        userRepository.save(Collections.singleton(user));
        Mockito.verify(repository).put(captor.capture());
        User value = (User) captor.getValue().iterator().next();
        assertEquals(user, value);
    }



    @Test
    public void shouldDeleve() {
        userRepository.deleteById("key");
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(repository).remove(captor.capture());
        assertEquals("key", captor.getValue());
    }

    @Test
    public void shouldDeleteIterable() {
        userRepository.deleteById(Collections.singletonList("key"));
        ArgumentCaptor<Iterable> captor = ArgumentCaptor.forClass(Iterable.class);
        Mockito.verify(repository).remove(captor.capture());
        assertEquals("key", captor.getValue().iterator().next());
    }

    @Test
    public void shouldFindById() {
        User user = new User("ada", "Ada", 10);
        Mockito.when(repository.get("key", User.class)).thenReturn(
                Optional.of(user));

        assertEquals(user, userRepository.findById("key").get());
    }

    @Test
    public void shouldFindByIdterable() {
        User user = new User("ada", "Ada", 10);
        User user2 = new User("ada", "Ada", 10);
        List<String> keys = Arrays.asList("key", "key2");
        Mockito.when(repository.get(keys, User.class)).thenReturn(
                Arrays.asList(user, user2));

        assertThat(userRepository.findById(keys), Matchers.containsInAnyOrder(user, user2));
    }
    

    @Test(expected = DynamicQueryException.class)
    public void shouldReturnErrorWhenExecuteMethodQuery() {
        userRepository.findByName("name");
    }

    interface UserRepository extends Repository<User, String> {

        Optional<User> findByName(String name);
    }

}