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
import org.jnosql.artemis.Pagination;
import org.jnosql.artemis.CDIJUnitRunner;
import org.jnosql.artemis.model.Person;
import org.jnosql.artemis.reflection.ClassRepresentation;
import org.jnosql.artemis.reflection.ClassRepresentations;
import org.jnosql.diana.api.Condition;
import org.jnosql.diana.api.Sort;
import org.jnosql.diana.api.TypeReference;
import org.jnosql.diana.api.column.Column;
import org.jnosql.diana.api.column.ColumnCondition;
import org.jnosql.diana.api.column.ColumnQuery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(CDIJUnitRunner.class)
public class ColumnQueryParserTest {

    @Inject
    private ClassRepresentations classRepresentations;

    private ColumnQueryParser parser;

    private ClassRepresentation classRepresentation;

    @Before
    public void setUp() {
        parser = new DefaultColumnQueryParser();
        classRepresentation = classRepresentations.get(Person.class);
    }

    @Test
    public void shouldFindByName() {
        ColumnQuery query = parser.parse("findByName", new Object[]{"name"}, classRepresentation);
        assertEquals("Person", query.getColumnFamily());
        assertEquals(Condition.EQUALS, query.getCondition().get().getCondition());
        assertEquals(Column.of("name", "name"), query.getCondition().get().getColumn());
    }


    @Test
    public void shouldFindByNameAndAge() {
        ColumnQuery query = parser.parse("findByNameAndAge", new Object[]{"name", 10}, classRepresentation);
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
    public void shouldFindByNameOrAge() {
        ColumnQuery query = parser.parse("findByNameOrAge", new Object[]{"name", 10}, classRepresentation);
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
    public void shouldFindByAgeLessThan() {
        ColumnQuery query = parser.parse("findByAgeLessThan", new Object[]{10}, classRepresentation);
        assertEquals("Person", query.getColumnFamily());
        assertEquals(Condition.LESSER_THAN, query.getCondition().get().getCondition());
        assertEquals(Column.of("age", 10), query.getCondition().get().getColumn());
    }

    @Test
    public void shouldFindByAgeGreaterThan() {
        ColumnQuery query = parser.parse("findByAgeGreaterThan", new Object[]{10}, classRepresentation);
        assertEquals("Person", query.getColumnFamily());
        assertEquals(Condition.GREATER_THAN, query.getCondition().get().getCondition());
        assertEquals(Column.of("age", 10), query.getCondition().get().getColumn());
    }

    @Test
    public void shouldFindByAgeLessEqualThan() {
        ColumnQuery query = parser.parse("findByAgeLessEqualThan", new Object[]{10}, classRepresentation);
        assertEquals("Person", query.getColumnFamily());
        assertEquals(Condition.LESSER_EQUALS_THAN, query.getCondition().get().getCondition());
        assertEquals(Column.of("age", 10), query.getCondition().get().getColumn());
    }

    @Test
    public void shouldFindByAgeGreaterEqualThan() {
        ColumnQuery query = parser.parse("findByAgeGreaterEqualThan", new Object[]{10}, classRepresentation);
        assertEquals("Person", query.getColumnFamily());
        assertEquals(Condition.GREATER_EQUALS_THAN, query.getCondition().get().getCondition());
        assertEquals(Column.of("age", 10), query.getCondition().get().getColumn());
    }

    @Test
    public void shouldFindByNameLike() {
        ColumnQuery query = parser.parse("findByNameLike", new Object[]{"name"}, classRepresentation);
        assertEquals("Person", query.getColumnFamily());
        assertEquals(Condition.LIKE, query.getCondition().get().getCondition());
        assertEquals(Column.of("name", "name"), query.getCondition().get().getColumn());
    }

    @Test
    public void shouldFindByNameLikeOrderByName() {
        ColumnQuery query = parser.parse("findByNameLikeOrderByName", new Object[]{"name"}, classRepresentation);
        assertEquals("Person", query.getColumnFamily());
        assertEquals(Condition.LIKE, query.getCondition().get().getCondition());
        assertEquals(Column.of("name", "name"), query.getCondition().get().getColumn());
        assertEquals(Sort.of("name", Sort.SortType.ASC), query.getSorts().get(0));
    }

    @Test
    public void shouldFindByNameLikeOrderByNameAsc() {
        ColumnQuery query = parser.parse("findByNameLikeOrderByNameAsc", new Object[]{"name"}, classRepresentation);
        assertEquals("Person", query.getColumnFamily());
        assertEquals(Condition.LIKE, query.getCondition().get().getCondition());
        assertEquals(Column.of("name", "name"), query.getCondition().get().getColumn());
        assertEquals(Sort.of("name", Sort.SortType.ASC), query.getSorts().get(0));
    }

    @Test
    public void shouldFindByNameLikeOrderByNameDesc() {
        ColumnQuery query = parser.parse("findByNameLikeOrderByNameDesc", new Object[]{"name"}, classRepresentation);
        assertEquals("Person", query.getColumnFamily());
        assertEquals(Condition.LIKE, query.getCondition().get().getCondition());
        assertEquals(Column.of("name", "name"), query.getCondition().get().getColumn());
        assertEquals(Sort.of("name", Sort.SortType.DESC), query.getSorts().get(0));
    }

    @Test
    public void shouldFindByNameLikeOrderByNameDescOrderByAgeAsc() {
        ColumnQuery query = parser.parse("findByNameLikeOrderByNameDescOrderByAgeAsc", new Object[]{"name"}, classRepresentation);
        assertEquals("Person", query.getColumnFamily());
        assertEquals(Condition.LIKE, query.getCondition().get().getCondition());
        assertEquals(Column.of("name", "name"), query.getCondition().get().getColumn());
        assertEquals(Sort.of("name", Sort.SortType.DESC), query.getSorts().get(0));
        assertEquals(Sort.of("age", Sort.SortType.ASC), query.getSorts().get(1));
    }

    @Test
    public void shouldFindByNameAndAAgeBetween() {
        ColumnQuery query = parser.parse("findByNameAndAgeBetween", new Object[]{"name", 10, 11},
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
        ColumnQuery query = parser.parse("findByNameAndAgeBetween", new Object[]{"name", 10},
                classRepresentation);
    }

    @Test(expected = DynamicQueryException.class)
    public void shouldReturnErrorWhenIsMissedArgument2() {
        ColumnQuery query = parser.parse("findByName", new Object[]{},
                classRepresentation);
    }

    @Test
    public void shouldFindByNameWithSortArgument() {
        Sort sort = Sort.of("age", Sort.SortType.ASC);
        ColumnQuery query = parser.parse("findByName", new Object[]{"name",
                sort}, classRepresentation);

        assertEquals("Person", query.getColumnFamily());
        assertEquals(Condition.EQUALS, query.getCondition().get().getCondition());
        assertEquals(Column.of("name", "name"), query.getCondition().get().getColumn());
        assertEquals(sort, query.getSorts().get(0));
    }

    @Test
    public void shouldFindByNameWithPageArgument() {
        Pagination pagination = Pagination.of(2L, 10);
        ColumnQuery query = parser.parse("findByName", new Object[]{"name",
                pagination}, classRepresentation);

        assertEquals("Person", query.getColumnFamily());
        assertEquals(Condition.EQUALS, query.getCondition().get().getCondition());
        assertEquals(Column.of("name", "name"), query.getCondition().get().getColumn());
        assertEquals(pagination.getMaxResults(), query.getMaxResults());
        assertEquals(pagination.getFirstResult(), query.getFirstResult());
    }

    @Test
    public void shouldFindByNameWithPageSortArgument() {
        Pagination pagination = Pagination.of(2L, 10);
        Sort sort = Sort.of("age", Sort.SortType.ASC);
        ColumnQuery query = parser.parse("findByName", new Object[]{"name",
                pagination, sort}, classRepresentation);

        assertEquals("Person", query.getColumnFamily());
        assertEquals(Condition.EQUALS, query.getCondition().get().getCondition());
        assertEquals(Column.of("name", "name"), query.getCondition().get().getColumn());
        assertEquals(pagination.getMaxResults(), query.getMaxResults());
        assertEquals(pagination.getFirstResult(), query.getFirstResult());
        assertEquals(sort, query.getSorts().get(0));
    }

    @Test
    public void shouldFindByNameWithPageSortArgumentAndIgnore() {
        Pagination pagination = Pagination.of(2L, 10);
        Sort sort = Sort.of("age", Sort.SortType.ASC);
        ColumnQuery query = parser.parse("findByName", new Object[]{"name",
                pagination, sort, "ignore"}, classRepresentation);

        assertEquals("Person", query.getColumnFamily());
        assertEquals(Condition.EQUALS, query.getCondition().get().getCondition());
        assertEquals(Column.of("name", "name"), query.getCondition().get().getColumn());
        assertEquals(pagination.getMaxResults(), query.getMaxResults());
        assertEquals(pagination.getFirstResult(), query.getFirstResult());
        assertEquals(sort, query.getSorts().get(0));
    }

    @Test
    public void shouldConvertsJavaFieldToColumn() {
        ColumnQuery query = parser.parse("findById", new Object[]{"id"}, classRepresentation);
        assertEquals("Person", query.getColumnFamily());
        assertEquals(Condition.EQUALS, query.getCondition().get().getCondition());
        assertEquals(Column.of("_id", "id"), query.getCondition().get().getColumn());
    }
}