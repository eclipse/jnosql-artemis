
/*
 * Copyright 2017 Otavio Santana and others
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jnosql.artemis.document;

import org.jnosql.artemis.WeldJUnit4Runner;
import org.jnosql.artemis.model.Person;
import org.jnosql.artemis.reflection.ClassRepresentation;
import org.jnosql.artemis.reflection.ClassRepresentations;
import org.jnosql.diana.api.Condition;
import org.jnosql.diana.api.Sort;
import org.jnosql.diana.api.TypeReference;
import org.jnosql.diana.api.document.Document;
import org.jnosql.diana.api.document.DocumentCondition;
import org.jnosql.diana.api.document.DocumentQuery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.util.List;

import static org.junit.Assert.assertEquals;


@RunWith(WeldJUnit4Runner.class)
public class DocumentQueryParserTest {

    @Inject
    private ClassRepresentations classRepresentations;

    private DocumentQueryParser parser;

    private ClassRepresentation classRepresentation;

    @Before
    public void setUp() {
        parser = new DocumentQueryParser();
        classRepresentation = classRepresentations.get(Person.class);
    }

    @Test
    public void shouldFindByName() {
        DocumentQuery query = parser.parse("findByName", new Object[]{"name"}, classRepresentation);
        assertEquals("Person", query.getCollection());
        assertEquals(Condition.EQUALS, query.getCondition().get().getCondition());
        assertEquals(Document.of("name", "name"), query.getCondition().get().getDocument());
    }


    @Test
    public void shouldFindByNameANDAge() {
        DocumentQuery query = parser.parse("findByNameANDAge", new Object[]{"name", 10}, classRepresentation);
        DocumentCondition condition = query.getCondition().get();
        assertEquals("Person", query.getCollection());
        assertEquals(Condition.AND, condition.getCondition());
        List<DocumentCondition> conditions = condition.getDocument().get(new TypeReference<List<DocumentCondition>>() {
        });
        DocumentCondition condition1 = conditions.get(0);
        assertEquals(Condition.EQUALS, condition1.getCondition());
        assertEquals(Document.of("name", "name"), condition1.getDocument());

        DocumentCondition condition2 = conditions.get(1);
        assertEquals(Condition.EQUALS, condition2.getCondition());
        assertEquals(Document.of("age", 10), condition2.getDocument());
    }

    @Test
    public void shouldFindByNameORAge() {
        DocumentQuery query = parser.parse("findByNameORAge", new Object[]{"name", 10}, classRepresentation);
        DocumentCondition condition = query.getCondition().get();
        assertEquals("Person", query.getCollection());
        assertEquals(Condition.OR, condition.getCondition());
        List<DocumentCondition> conditions = condition.getDocument().get(new TypeReference<List<DocumentCondition>>() {
        });
        DocumentCondition condition1 = conditions.get(0);
        assertEquals(Condition.EQUALS, condition1.getCondition());
        assertEquals(Document.of("name", "name"), condition1.getDocument());

        DocumentCondition condition2 = conditions.get(1);
        assertEquals(Condition.EQUALS, condition2.getCondition());
        assertEquals(Document.of("age", 10), condition2.getDocument());
    }


    @Test
    public void shouldFindByAgeLessThan() {
        DocumentQuery query = parser.parse("findByAgeLessThan", new Object[]{10}, classRepresentation);
        assertEquals("Person", query.getCollection());
        assertEquals(Condition.LESSER_THAN, query.getCondition().get().getCondition());
        assertEquals(Document.of("age", 10), query.getCondition().get().getDocument());
    }

    @Test
    public void shouldFindByAgeGreaterThan() {
        DocumentQuery query = parser.parse("findByAgeGreaterThan", new Object[]{10}, classRepresentation);
        assertEquals("Person", query.getCollection());
        assertEquals(Condition.GREATER_THAN, query.getCondition().get().getCondition());
        assertEquals(Document.of("age", 10), query.getCondition().get().getDocument());
    }

    @Test
    public void shouldFindByAgeLessEqualThan() {
        DocumentQuery query = parser.parse("findByAgeLessEqualThan", new Object[]{10}, classRepresentation);
        assertEquals("Person", query.getCollection());
        assertEquals(Condition.LESSER_EQUALS_THAN, query.getCondition().get().getCondition());
        assertEquals(Document.of("age", 10), query.getCondition().get().getDocument());
    }

    @Test
    public void shouldFindByAgeGreaterEqualThan() {
        DocumentQuery query = parser.parse("findByAgeGreaterEqualThan", new Object[]{10}, classRepresentation);
        assertEquals("Person", query.getCollection());
        assertEquals(Condition.GREATER_EQUALS_THAN, query.getCondition().get().getCondition());
        assertEquals(Document.of("age", 10), query.getCondition().get().getDocument());
    }

    @Test
    public void shouldFindByNameLike() {
        DocumentQuery query = parser.parse("findByNameLike", new Object[]{"name"}, classRepresentation);
        assertEquals("Person", query.getCollection());
        assertEquals(Condition.LIKE, query.getCondition().get().getCondition());
        assertEquals(Document.of("name", "name"), query.getCondition().get().getDocument());
    }

    @Test
    public void shouldFindByNameLikeOrderByName() {
        DocumentQuery query = parser.parse("findByNameLikeOrderByName", new Object[]{"name"}, classRepresentation);
        assertEquals("Person", query.getCollection());
        assertEquals(Condition.LIKE, query.getCondition().get().getCondition());
        assertEquals(Document.of("name", "name"), query.getCondition().get().getDocument());
        assertEquals(Sort.of("name", Sort.SortType.ASC), query.getSorts().get(0));
    }

    @Test
    public void shouldFindByNameLikeOrderByNameAsc() {
        DocumentQuery query = parser.parse("findByNameLikeOrderByNameAsc", new Object[]{"name"}, classRepresentation);
        assertEquals("Person", query.getCollection());
        assertEquals(Condition.LIKE, query.getCondition().get().getCondition());
        assertEquals(Document.of("name", "name"), query.getCondition().get().getDocument());
        assertEquals(Sort.of("name", Sort.SortType.ASC), query.getSorts().get(0));
    }

    @Test
    public void shouldFindByNameLikeOrderByNameDesc() {
        DocumentQuery query = parser.parse("findByNameLikeOrderByNameDesc", new Object[]{"name"}, classRepresentation);
        assertEquals("Person", query.getCollection());
        assertEquals(Condition.LIKE, query.getCondition().get().getCondition());
        assertEquals(Document.of("name", "name"), query.getCondition().get().getDocument());
        assertEquals(Sort.of("name", Sort.SortType.DESC), query.getSorts().get(0));
    }


    //AND
    //OR
    //Between
    //LessThan
    //GreaterThan
    //LessThanEqual
    //GreaterThanEqual
    //Like
    //OrderBy____Desc
    //OrderBy_____ASC

}