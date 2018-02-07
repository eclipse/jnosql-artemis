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

import org.jnosql.artemis.CDIExtension;
import org.jnosql.artemis.column.ColumnQueryMapperBuilder;
import org.jnosql.artemis.model.Address;
import org.jnosql.artemis.model.Money;
import org.jnosql.artemis.model.Person;
import org.jnosql.artemis.model.Worker;
import org.jnosql.diana.api.column.ColumnQuery;
import org.jnosql.diana.api.column.query.ColumnFrom;
import org.jnosql.diana.api.column.query.ColumnQueryBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.inject.Inject;
import java.math.BigDecimal;

import static org.jnosql.diana.api.column.query.ColumnQueryBuilder.select;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(CDIExtension.class)
public class DefaultColumnMapperSelectBuilderTest {

    @Inject
    private ColumnQueryMapperBuilder mapperBuilder;


    @Test
    public void shouldReturnSelectStarFrom() {
        ColumnFrom columnFrom = mapperBuilder.selectFrom(Person.class);
        ColumnQuery query = columnFrom.build();
        ColumnQuery queryExpected = ColumnQueryBuilder.select().from("Person").build();
        assertEquals(queryExpected, query);
    }

    @Test
    public void shouldSelectOrderAsc() {
        ColumnQuery query = mapperBuilder.selectFrom(Worker.class).orderBy("salary").asc().build();
        ColumnQuery queryExpected = select().from("Worker").orderBy("money").asc().build();
        assertEquals(queryExpected, query);
    }

    @Test
    public void shouldSelectOrderDesc() {
        ColumnQuery query = mapperBuilder.selectFrom(Worker.class).orderBy("salary").desc().build();
        ColumnQuery queryExpected = select().from("Worker").orderBy("money").desc().build();
        assertEquals(queryExpected, query);
    }


    @Test
    public void shouldReturnErrorSelectWhenOrderIsNull() {
        Assertions.assertThrows(NullPointerException.class, () -> mapperBuilder.selectFrom(Worker.class).orderBy(null));
    }

    @Test
    public void shouldSelectLimit() {
        ColumnQuery query = mapperBuilder.selectFrom(Worker.class).limit(10).build();
        ColumnQuery queryExpected = select().from("Worker").limit(10L).build();
        assertEquals(queryExpected, query);
    }

    @Test
    public void shouldSelectStart() {
        ColumnQuery query = mapperBuilder.selectFrom(Worker.class).start(10).build();
        ColumnQuery queryExpected = select().from("Worker").start(10L).build();
        assertEquals(queryExpected, query);
    }


    @Test
    public void shouldSelectWhereNameEq() {
        ColumnQuery query = mapperBuilder.selectFrom(Person.class).where("name").eq("Ada").build();
        ColumnQuery queryExpected = select().from("Person").where("name").eq("Ada").build();
        assertEquals(queryExpected, query);
    }

    @Test
    public void shouldSelectWhereNameLike() {
        ColumnQuery query = mapperBuilder.selectFrom(Person.class).where("name").like("Ada").build();
        ColumnQuery queryExpected = select().from("Person").where("name").like("Ada").build();
        assertEquals(queryExpected, query);
    }

    @Test
    public void shouldSelectWhereNameGt() {
        ColumnQuery query = mapperBuilder.selectFrom(Person.class).where("id").gt(10).build();
        ColumnQuery queryExpected = select().from("Person").where("_id").gt(10L).build();
        assertEquals(queryExpected, query);
    }

    @Test
    public void shouldSelectWhereNameGte() {
        ColumnQuery query = mapperBuilder.selectFrom(Person.class).where("id").gte(10).build();
        ColumnQuery queryExpected = select().from("Person").where("_id").gte(10L).build();
        assertEquals(queryExpected, query);
    }

    @Test
    public void shouldSelectWhereNameLt() {
        ColumnQuery query = mapperBuilder.selectFrom(Person.class).where("id").lt(10).build();
        ColumnQuery queryExpected = select().from("Person").where("_id").lt(10L).build();
        assertEquals(queryExpected, query);
    }

    @Test
    public void shouldSelectWhereNameLte() {
        ColumnQuery query = mapperBuilder.selectFrom(Person.class).where("id").lte(10).build();
        ColumnQuery queryExpected = select().from("Person").where("_id").lte(10L).build();
        assertEquals(queryExpected, query);
    }

    @Test
    public void shouldSelectWhereNameBetween() {
        ColumnQuery query = mapperBuilder.selectFrom(Person.class).where("id")
                .between(10, 20).build();
        ColumnQuery queryExpected = select().from("Person").where("_id")
                .between(10L, 20L).build();
        assertEquals(queryExpected, query);
    }

    @Test
    public void shouldSelectWhereNameNot() {
        ColumnQuery query = mapperBuilder.selectFrom(Person.class).where("name").not().like("Ada").build();
        ColumnQuery queryExpected = select().from("Person").where("name").not().like("Ada").build();
        assertEquals(queryExpected, query);
    }


    @Test
    public void shouldSelectWhereNameAnd() {
        ColumnQuery query = mapperBuilder.selectFrom(Person.class).where("age").between(10, 20)
                .and("name").eq("Ada").build();
        ColumnQuery queryExpected = select().from("Person").where("age")
                .between(10, 20)
                .and("name").eq("Ada").build();

        assertEquals(queryExpected, query);
    }

    @Test
    public void shouldSelectWhereNameOr() {
        ColumnQuery query = mapperBuilder.selectFrom(Person.class).where("id").between(10, 20)
                .or("name").eq("Ada").build();
        ColumnQuery queryExpected = select().from("Person").where("_id")
                .between(10L, 20L)
                .or("name").eq("Ada").build();

        assertEquals(queryExpected, query);
    }

    @Test
    public void shouldConvertField() {
        ColumnQuery query = mapperBuilder.selectFrom(Person.class).where("id").eq("20")
                .build();
        ColumnQuery queryExpected = select().from("Person").where("_id").eq(20L)
                .build();

        assertEquals(queryExpected, query);
    }

    @Test
    public void shouldUseAttibuteConverter() {
        ColumnQuery query = mapperBuilder.selectFrom(Worker.class).where("salary")
                .eq(new Money("USD", BigDecimal.TEN)).build();
        ColumnQuery queryExpected = select().from("Worker").where("money")
                .eq("USD 10").build();
        assertEquals(queryExpected, query);
    }

    @Test
    public void shouldQueryByEmbeddable() {
        ColumnQuery query = mapperBuilder.selectFrom(Worker.class).where("job.city").eq("Salvador")
                .build();
        ColumnQuery queryExpected = select().from("Worker").where("job.city").eq("Salvador")
                .build();

        assertEquals(queryExpected, query);
    }

    @Test
    public void shouldQueryBySubEntity() {
        ColumnQuery query = mapperBuilder.selectFrom(Address.class).where("zipcode.zip").eq("01312321")
                .build();
        ColumnQuery queryExpected = select().from("Address").where("zip").eq("01312321")
                .build();

        assertEquals(queryExpected, query);
    }

}