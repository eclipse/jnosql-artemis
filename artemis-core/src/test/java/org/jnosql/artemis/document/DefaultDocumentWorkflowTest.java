/*
 * Copyright 2017 Eclipse Foundation
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
package org.jnosql.artemis.document;

import org.jnosql.diana.api.document.DocumentEntity;
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
public class DefaultDocumentWorkflowTest {


    @InjectMocks
    private DefaultDocumentWorkflow subject;

    @Mock
    private DocumentEventPersistManager columnEventPersistManager;

    @Mock
    private DocumentEntityConverter converter;

    @Mock
    private DocumentEntity columnEntity;

    @Before
    public void setUp() {
        when(converter.toDocument(any(Object.class)))
                .thenReturn(columnEntity);

    }

    @Test(expected = NullPointerException.class)
    public void shouldReturnErrorWhenEntityIsNull() {
        UnaryOperator<DocumentEntity> action = t -> t;
        subject.flow(null, action);
    }

    @Test(expected = NullPointerException.class)
    public void shouldReturnErrorWhenActionIsNull() {
        subject.flow("", null);
    }

    @Test
    public void shouldFollowWorkflow() {
        UnaryOperator<DocumentEntity> action = t -> t;
        subject.flow("entity", action);

        verify(columnEventPersistManager).firePreDocument(any(DocumentEntity.class));
        verify(columnEventPersistManager).firePostDocument(any(DocumentEntity.class));
        verify(columnEventPersistManager).firePreEntity(any(DocumentEntity.class));
        verify(columnEventPersistManager).firePostEntity(any(DocumentEntity.class));
        verify(converter).toDocument(any(Object.class));
    }

}