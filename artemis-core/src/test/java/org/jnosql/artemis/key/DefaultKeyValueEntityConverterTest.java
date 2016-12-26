/*
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
import org.jnosql.artemis.model.Actor;
import org.jnosql.artemis.model.User;
import org.jnosql.diana.api.key.KeyValueEntity;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;

import static org.junit.Assert.*;

@RunWith(WeldJUnit4Runner.class)
public class DefaultKeyValueEntityConverterTest {

    @Inject
    private KeyValueEntityConverter converter;

    @Test(expected = NullPointerException.class)
    public void shouldReturnNPEWhenEntityIsNull() {
        converter.toKeyValue(null);
    }

    @Test(expected = KeyNotFoundException.class)
    public void shouldReturnErrorWhenThereIsNotKeyAnnotation() {
        converter.toKeyValue(new Actor());
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
}