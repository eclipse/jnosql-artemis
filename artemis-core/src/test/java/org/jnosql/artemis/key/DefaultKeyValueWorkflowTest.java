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