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

import org.jnosql.artemis.DynamicQueryException;
import org.jnosql.artemis.CDIJUnitRunner;
import org.jnosql.artemis.model.Person;
import org.jnosql.artemis.reflection.ClassRepresentation;
import org.jnosql.artemis.reflection.ClassRepresentations;
import org.jnosql.diana.api.Condition;
import org.jnosql.diana.api.TypeReference;
import org.jnosql.diana.api.column.Column;
import org.jnosql.diana.api.column.ColumnCondition;
import org.jnosql.diana.api.column.ColumnDeleteQuery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(CDIJUnitRunner.class)
public class ColumnQueryDeleteParserTest {
    @Inject
    private ClassRepresentations classRepresentations;

    private ColumnQueryDeleteParser parser;

    private ClassRepresentation classRepresentation;

    @Before
    public void setUp() {
        parser = new ColumnQueryDeleteParser();
        classRepresentation = classRepresentations.get(Person.class);
    }


    @Test
    public void shouldDeleteByName() {
        ColumnDeleteQuery query = parser.parse("deleteByName", new Object[]{"name"}, classRepresentation);
        assertEquals("Person", query.getColumnFamily());
        assertEquals(Condition.EQUALS, query.getCondition().get().getCondition());
        assertEquals(Column.of("name", "name"), query.getCondition().get().getColumn());
    }


    @Test
    public void shouldDeleteByNameAndAge() {
        ColumnDeleteQuery query = parser.parse("deleteByNameAndAge", new Object[]{"name", 10}, classRepresentation);
        ColumnCondition condition = query.getCondition().get();
        assertEquals("Person", query.getColumnFamily());
        assertEquals(Condition.AND, condition.getCondition());
        List<ColumnCondition> conditions = condition.getColumn().get(new TypeReference<List<ColumnCondition>>() {
        });
        ColumnCondition condition1 = conditions.get(0);
        assertEquals(Condition.EQUALS, condition1.getCondition());
        assertEquals(Column.of("name", "name"), condition1.getColumn());

        ColumnCondition condition2 = conditions.get(1);
        assertEquals(Condition.EQUALS, condition2.getCondition());
        assertEquals(Column.of("age", 10), condition2.getColumn());
    }

    @Test
    public void shouldDeleteByNameOrAge() {
        ColumnDeleteQuery query = parser.parse("deleteByNameOrAge", new Object[]{"name", 10}, classRepresentation);
        ColumnCondition condition = query.getCondition().get();
        assertEquals("Person", query.getColumnFamily());
        assertEquals(Condition.OR, condition.getCondition());
        List<ColumnCondition> conditions = condition.getColumn().get(new TypeReference<List<ColumnCondition>>() {
        });
        ColumnCondition condition1 = conditions.get(0);
        assertEquals(Condition.EQUALS, condition1.getCondition());
        assertEquals(Column.of("name", "name"), condition1.getColumn());

        ColumnCondition condition2 = conditions.get(1);
        assertEquals(Condition.EQUALS, condition2.getCondition());
        assertEquals(Column.of("age", 10), condition2.getColumn());
    }


    @Test
    public void shouldDeleteByAgeLessThan() {
        ColumnDeleteQuery query = parser.parse("deleteByAgeLessThan", new Object[]{10}, classRepresentation);
        assertEquals("Person", query.getColumnFamily());
        assertEquals(Condition.LESSER_THAN, query.getCondition().get().getCondition());
        assertEquals(Column.of("age", 10), query.getCondition().get().getColumn());
    }

    @Test
    public void shouldDeleteByAgeGreaterThan() {
        ColumnDeleteQuery query = parser.parse("deleteByAgeGreaterThan", new Object[]{10}, classRepresentation);
        assertEquals("Person", query.getColumnFamily());
        assertEquals(Condition.GREATER_THAN, query.getCondition().get().getCondition());
        assertEquals(Column.of("age", 10), query.getCondition().get().getColumn());
    }

    @Test
    public void shouldDeleteByAgeLessThanEqual() {
        ColumnDeleteQuery query = parser.parse("deleteByAgeLessThanEqual", new Object[]{10}, classRepresentation);
        assertEquals("Person", query.getColumnFamily());
        assertEquals(Condition.LESSER_EQUALS_THAN, query.getCondition().get().getCondition());
        assertEquals(Column.of("age", 10), query.getCondition().get().getColumn());
    }

    @Test
    public void shouldDeleteByAgeGreaterThanEqual() {
        ColumnDeleteQuery query = parser.parse("deleteByAgeGreaterThanEqual", new Object[]{10}, classRepresentation);
        assertEquals("Person", query.getColumnFamily());
        assertEquals(Condition.GREATER_EQUALS_THAN, query.getCondition().get().getCondition());
        assertEquals(Column.of("age", 10), query.getCondition().get().getColumn());
    }

    @Test
    public void shouldDeleteByNameLike() {
        ColumnDeleteQuery query = parser.parse("deleteByNameLike", new Object[]{"name"}, classRepresentation);
        assertEquals("Person", query.getColumnFamily());
        assertEquals(Condition.LIKE, query.getCondition().get().getCondition());
        assertEquals(Column.of("name", "name"), query.getCondition().get().getColumn());
    }



    @Test
    public void shouldDeleteByNameAndAAgeBetween() {
        ColumnDeleteQuery query = parser.parse("deleteByNameAndAgeBetween", new Object[]{"name", 10, 11},
                classRepresentation);
        assertEquals("Person", query.getColumnFamily());
        ColumnCondition condition = query.getCondition().get();
        assertEquals(Condition.AND, condition.getCondition());
        List<ColumnCondition> conditions = condition.getColumn().get(new TypeReference<List<ColumnCondition>>() {
        });

        ColumnCondition condition1 = conditions.get(0);
        assertEquals(Condition.EQUALS, condition1.getCondition());
        assertEquals(Column.of("name", "name"), condition1.getColumn());

        ColumnCondition condition2 = conditions.get(1);
        assertEquals(Condition.BETWEEN, condition2.getCondition());
        assertEquals(Column.of("age", Arrays.asList(10, 11)), condition2.getColumn());
    }

    @Test(expected = DynamicQueryException.class)
    public void shouldReturnErrorWhenIsMissedArgument() {
        parser.parse("deleteByNameAndAgeBetween", new Object[]{"name", 10},
                classRepresentation);
    }

    @Test(expected = DynamicQueryException.class)
    public void shouldReturnErrorWhenIsMissedArgument2() {
        parser.parse("deleteByName", new Object[]{},
                classRepresentation);
    }

}