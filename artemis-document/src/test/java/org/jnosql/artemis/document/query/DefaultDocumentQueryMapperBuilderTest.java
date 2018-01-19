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
package org.jnosql.artemis.document.query;

import org.jnosql.artemis.CDIJUnitRunner;
import org.jnosql.artemis.document.DocumentQueryMapperBuilder;
import org.jnosql.artemis.model.Person;
import org.jnosql.diana.api.document.query.DocumentDeleteFrom;
import org.jnosql.diana.api.document.query.DocumentFrom;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@RunWith(CDIJUnitRunner.class)
public class DefaultDocumentQueryMapperBuilderTest {


    @Inject
    private DocumentQueryMapperBuilder mapperBuilder;


    @Test(expected = NullPointerException.class)
    public void shouldReturnErrorWhenEntityClassIsNull() {
        mapperBuilder.selectFrom(null);
    }

    @Test
    public void shouldReturnSelectFrom() {
        DocumentFrom documentFrom = mapperBuilder.selectFrom(Person.class);
        assertNotNull(documentFrom);
    }

    @Test(expected = NullPointerException.class)
    public void shouldReturnErrorWhenDeleteEntityClassIsNull() {
        mapperBuilder.deleteFrom(null);
    }

    @Test
    public void shouldReturnDeleteFrom() {
        DocumentDeleteFrom deleteFrom = mapperBuilder.deleteFrom(Person.class);
        assertNotNull(deleteFrom);
    }
}