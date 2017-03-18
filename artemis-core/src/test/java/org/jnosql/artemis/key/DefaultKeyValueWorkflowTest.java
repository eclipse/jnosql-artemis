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

import org.jnosql.diana.api.document.DocumentEntity;
import org.jnosql.diana.api.key.KeyValueEntity;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.function.UnaryOperator;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class DefaultKeyValueWorkflowTest {

    @Mock
    private KeyValueEventPersistManager eventPersistManager;

    @Mock
    private KeyValueEntityConverter converter;

    @InjectMocks
    private DefaultKeyValueWorkflow subject;

    @Mock
    private KeyValueEntity<Object> keyValueEntity;

    @Before
    public void setUp() {
        when(converter.toKeyValue(any(Object.class)))
                .thenReturn(keyValueEntity);

    }

    @Test(expected = NullPointerException.class)
    public void shouldReturnErrorWhenEntityIsNull() {
        UnaryOperator<KeyValueEntity<?>> action = t -> t;
        subject.flow(null, action);
    }

    @Test(expected = NullPointerException.class)
    public void shouldReturnErrorWhenActionIsNull() {
        subject.flow("", null);
    }

    @Test
    public void shouldFollowWorkflow() {
        UnaryOperator<KeyValueEntity<?>> action = t -> t;
        subject.flow("entity", action);

        verify(eventPersistManager).firePreKeyValue(any(KeyValueEntity.class));
        verify(eventPersistManager).firePostKeyValue(any(KeyValueEntity.class));
        verify(eventPersistManager).firePreEntity(any(DocumentEntity.class));
        verify(eventPersistManager).firePostEntity(any(DocumentEntity.class));

        verify(eventPersistManager).firePreKeyValueEntity(any(DocumentEntity.class));
        verify(eventPersistManager).firePostKeyValueEntity(any(DocumentEntity.class));
    }

}