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

import org.jnosql.artemis.IdNotFoundException;
import org.jnosql.artemis.CDIJUnitRunner;
import org.jnosql.artemis.model.User;
import org.jnosql.artemis.model.Worker;
import org.jnosql.diana.api.Value;
import org.jnosql.diana.api.key.KeyValueEntity;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(CDIJUnitRunner.class)
public class DefaultKeyValueEntityConverterTest {

    @Inject
    private KeyValueEntityConverter converter;

    @Test(expected = NullPointerException.class)
    public void shouldReturnNPEWhenEntityIsNull() {
        converter.toKeyValue(null);
    }

    @Test(expected = IdNotFoundException.class)
    public void shouldReturnErrorWhenThereIsNotKeyAnnotation() {
        converter.toKeyValue(new Worker());
    }

    @Test(expected = NullPointerException.class)
    public void shouldReturnErrorWhenTheKeyIsNull() {
        User user = new User(null, "name", 24);
        converter.toKeyValue(user);
    }

    @Test
    public void shouldConvertToKeyValue() {
        User user = new User("nickname", "name", 24);
        KeyValueEntity<String> keyValueEntity = converter.toKeyValue(user);
        assertEquals("nickname", keyValueEntity.getKey());
        assertEquals(user, keyValueEntity.getValue().get());
    }

    @Test(expected = NullPointerException.class)
    public void shouldReturnNPEWhenKeyValueIsNull() {
        converter.toEntity(User.class, (KeyValueEntity<?>) null);
    }

    @Test(expected = NullPointerException.class)
    public void shouldReturnNPEWhenClassIsNull() {
        converter.toEntity(null, KeyValueEntity.of("user", new User("nickname", "name", 21)));
    }

    @Test(expected = IdNotFoundException.class)
    public void shouldReturnErrorWhenTheKeyIsMissing() {
        converter.toEntity(Worker.class, KeyValueEntity.of("worker", new Worker()));
    }

    @Test
    public void shouldConvertToEntity() {
        User expectedUser = new User("nickname", "name", 21);
        User user = converter.toEntity(User.class,
                KeyValueEntity.of("user", expectedUser));
        assertEquals(expectedUser, user);
    }

    @Test
    public void shouldConvertAndFeedTheKeyValue() {
        User expectedUser = new User("nickname", "name", 21);
        User user = converter.toEntity(User.class,
                KeyValueEntity.of("nickname", new User(null, "name", 21)));
        assertEquals(expectedUser, user);
    }

    @Test
    public void shouldConvertAndFeedTheKeyValueIfKeyAndFieldAreDifferent() {
        User expectedUser = new User("nickname", "name", 21);
        User user = converter.toEntity(User.class,
                KeyValueEntity.of("nickname", new User("newName", "name", 21)));
        assertEquals(expectedUser, user);
    }

    @Test
    public void shouldConvertValueToEntity() {
        User expectedUser = new User("nickname", "name", 21);
        User user = converter.toEntity(User.class, Value.of(expectedUser));
        assertEquals(expectedUser, user);
    }


}