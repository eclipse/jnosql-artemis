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
package org.jnosql.artemis.document;

import org.jnosql.diana.api.document.DocumentEntity;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

    @BeforeEach
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
        verify(columnEventPersistManager).firePreDocumentEntity(any(DocumentEntity.class));
        verify(columnEventPersistManager).firePostDocumentEntity(any(DocumentEntity.class));
        verify(converter).toDocument(any(Object.class));
    }

}