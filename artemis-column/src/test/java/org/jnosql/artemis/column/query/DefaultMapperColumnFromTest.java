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
import org.jnosql.artemis.model.Worker;
import org.jnosql.diana.api.column.ColumnQuery;
import org.jnosql.diana.api.column.query.ColumnFrom;
import org.jnosql.diana.api.column.query.ColumnQueryBuilder;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;

import static org.jnosql.diana.api.column.query.ColumnQueryBuilder.select;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(CDIJUnitRunner.class)
public class DefaultMapperColumnFromTest {

    @Inject
    private ColumnQueryMapperBuilder mapperBuilder;


    @Test
    public void shouldReturnSelectStarFrom() {
        ColumnFrom columnFrom = mapperBuilder.selectFrom(Person.class);
        ColumnQuery query = columnFrom.build();
        ColumnQuery queryExpected = ColumnQueryBuilder.select().from("Person").build();
        Assert.assertEquals(queryExpected, query);
    }

    @Test
    public void shouldSelectOrderAsc() {
        ColumnQuery query = mapperBuilder.selectFrom(Worker.class).orderBy("salary").asc().build();
        ColumnQuery queryExpected = select().from("Worker").orderBy("money").asc().build();
        Assert.assertEquals(queryExpected, query);
    }

    @Test
    public void shouldSelectOrderDesc() {
        ColumnQuery query = mapperBuilder.selectFrom(Worker.class).orderBy("salary").desc().build();
        ColumnQuery queryExpected = select().from("Worker").orderBy("money").desc().build();
        Assert.assertEquals(queryExpected, query);
    }


    @Test(expected = NullPointerException.class)
    public void shouldReturnErrorSelectWhenOrderIsNull() {
        mapperBuilder.selectFrom(Worker.class).orderBy(null);
    }

    @Test
    public void shouldSelectLimit() {
        ColumnQuery query = mapperBuilder.selectFrom(Worker.class).limit(10).build();
        ColumnQuery queryExpected = select().from("Worker").limit(10).build();
        Assert.assertEquals(queryExpected, query);
    }

    @Test
    public void shouldSelectStart() {
        ColumnQuery query = mapperBuilder.selectFrom(Worker.class).start(10).build();
        ColumnQuery queryExpected = select().from("Worker").start(10).build();
        Assert.assertEquals(queryExpected, query);
    }

}