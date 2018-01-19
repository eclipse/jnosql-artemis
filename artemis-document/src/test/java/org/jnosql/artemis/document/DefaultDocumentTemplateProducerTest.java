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

import org.jnosql.artemis.CDIJUnitRunner;
import org.jnosql.diana.api.document.DocumentCollectionManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import javax.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertNotNull;


@RunWith(CDIJUnitRunner.class)
public class DefaultDocumentTemplateProducerTest {

    @Inject
    private DocumentTemplateProducer producer;


    @Test(expected = NullPointerException.class)
    public void shouldReturnErrorWhenManagerNull() {
        producer.get(null);
    }

    @Test
    public void shouldReturn() {
        DocumentCollectionManager manager = Mockito.mock(DocumentCollectionManager.class);
        DocumentTemplate documentTemplate = producer.get(manager);
        assertNotNull(documentTemplate);
    }
}