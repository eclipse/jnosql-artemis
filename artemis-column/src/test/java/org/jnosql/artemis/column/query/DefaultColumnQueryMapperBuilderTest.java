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
package org.jnosql.artemis.column.query;

import org.jnosql.artemis.CDIJUnitRunner;
import org.jnosql.artemis.column.ColumnQueryMapperBuilder;
import org.jnosql.artemis.model.Person;
import org.jnosql.diana.api.column.query.ColumnDeleteFrom;
import org.jnosql.diana.api.column.query.ColumnFrom;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;

import static org.junit.Assert.assertNotNull;

@RunWith(CDIJUnitRunner.class)
public class DefaultColumnQueryMapperBuilderTest {


    @Inject
    private ColumnQueryMapperBuilder mapperBuilder;


    @Test(expected = NullPointerException.class)
    public void shouldReturnErrorWhenEntityClassIsNull() {
        mapperBuilder.selectFrom(null);
    }

    @Test
    public void shouldReturnSelectFrom() {
        ColumnFrom columnFrom = mapperBuilder.selectFrom(Person.class);
        assertNotNull(columnFrom);
    }
    
    @Test(expected = NullPointerException.class)
    public void shouldReturnErrorWhenDeleteEntityClassIsNull() {
        mapperBuilder.deleteFrom(null);
    }

    @Test
    public void shouldReturnDeleteFrom() {
        ColumnDeleteFrom columnDeleteFrom = mapperBuilder.deleteFrom(Person.class);
        assertNotNull(columnDeleteFrom);
    }
}