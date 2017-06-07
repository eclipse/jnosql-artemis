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

import org.jnosql.artemis.WeldJUnit4Runner;
import org.jnosql.diana.api.column.ColumnFamilyManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import javax.inject.Inject;

import static org.junit.Assert.assertNotNull;

@RunWith(WeldJUnit4Runner.class)
public class DefaultColumnTemplateProducerTest {

    @Inject
    private ColumnTemplateProducer producer;


    @Test(expected = NullPointerException.class)
    public void shouldReturnErrorWhenColumnFamilyManagerNull() {
        producer.get(null);
    }

    @Test
    public void shouldReturn() {
        ColumnFamilyManager manager = Mockito.mock(ColumnFamilyManager.class);
        ColumnTemplate columnTemplate = producer.get(manager);
        assertNotNull(columnTemplate);
    }
}