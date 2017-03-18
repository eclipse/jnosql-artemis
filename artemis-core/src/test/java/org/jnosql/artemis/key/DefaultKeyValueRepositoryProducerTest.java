/*
 * Copyright 2017 Otavio Santana and others
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jnosql.artemis.key;

import org.jnosql.artemis.WeldJUnit4Runner;
import org.jnosql.diana.api.key.BucketManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import javax.inject.Inject;

import static org.junit.Assert.assertNotNull;

@RunWith(WeldJUnit4Runner.class)
public class DefaultKeyValueRepositoryProducerTest {


    @Inject
    private KeyValueRepositoryProducer producer;


    @Test(expected = NullPointerException.class)
    public void shouldReturnErrorWhenManagerNull() {
        producer.get(null);
    }

    @Test
    public void shouldReturn() {
        BucketManager manager = Mockito.mock(BucketManager.class);
        KeyValueRepository repository = producer.get(manager);
        assertNotNull(repository);
    }
}