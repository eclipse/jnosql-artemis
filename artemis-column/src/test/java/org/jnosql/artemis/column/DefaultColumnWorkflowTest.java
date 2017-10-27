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
package org.jnosql.artemis.column;

import org.jnosql.diana.api.column.ColumnEntity;
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
public class DefaultColumnWorkflowTest {


    @InjectMocks
    private DefaultColumnWorkflow subject;

    @Mock
    private ColumnEventPersistManager columnEventPersistManager;

    @Mock
    private ColumnEntityConverter converter;

    @Mock
    private ColumnEntity columnEntity;

    @Before
    public void setUp() {
        when(converter.toColumn(any(Object.class)))
                .thenReturn(columnEntity);

    }

    @Test(expected = NullPointerException.class)
    public void shouldReturnErrorWhenEntityIsNull() {
        UnaryOperator<ColumnEntity> action = t -> t;
        subject.flow(null, action);
    }

    @Test(expected = NullPointerException.class)
    public void shouldReturnErrorWhenActionIsNull() {
        subject.flow("", null);
    }

    @Test
    public void shouldFollowWorkflow() {
        UnaryOperator<ColumnEntity> action = t -> t;
        subject.flow("entity", action);

        verify(columnEventPersistManager).firePreColumn(any(ColumnEntity.class));
        verify(columnEventPersistManager).firePostColumn(any(ColumnEntity.class));
        verify(columnEventPersistManager).firePreEntity(any(ColumnEntity.class));
        verify(columnEventPersistManager).firePostEntity(any(ColumnEntity.class));

        verify(columnEventPersistManager).firePreColumnEntity(any(ColumnEntity.class));
        verify(columnEventPersistManager).firePostColumnEntity(any(ColumnEntity.class));
        verify(converter).toColumn(any(Object.class));
    }

}