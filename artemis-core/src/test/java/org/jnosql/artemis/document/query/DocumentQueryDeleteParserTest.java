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

import org.jnosql.artemis.DynamicQueryException;
import org.jnosql.artemis.WeldJUnit4Runner;
import org.jnosql.artemis.document.query.DocumentQueryDeleteParser;
import org.jnosql.artemis.model.Person;
import org.jnosql.artemis.reflection.ClassRepresentation;
import org.jnosql.artemis.reflection.ClassRepresentations;
import org.jnosql.diana.api.Condition;
import org.jnosql.diana.api.TypeReference;
import org.jnosql.diana.api.document.Document;
import org.jnosql.diana.api.document.DocumentCondition;
import org.jnosql.diana.api.document.DocumentDeleteQuery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(WeldJUnit4Runner.class)
public class DocumentQueryDeleteParserTest {

    @Inject
    private ClassRepresentations classRepresentations;

    private DocumentQueryDeleteParser parser;

    private ClassRepresentation classRepresentation;

    @Before
    public void setUp() {
        parser = new DocumentQueryDeleteParser();
        classRepresentation = classRepresentations.get(Person.class);
    }


    @Test
    public void shouldDeleteByName() {
        DocumentDeleteQuery query = parser.parse("deleteByName", new Object[]{"name"}, classRepresentation);
        assertEquals("Person", query.getCollection());
        assertEquals(Condition.EQUALS, query.getCondition().get().getCondition());
        assertEquals(Document.of("name", "name"), query.getCondition().get().getDocument());
    }


    @Test
    public void shouldDeleteByNameAndAge() {
        DocumentDeleteQuery query = parser.parse("deleteByNameAndAge", new Object[]{"name", 10}, classRepresentation);
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
    public void shouldDeleteByNameOrAge() {
        DocumentDeleteQuery query = parser.parse("deleteByNameOrAge", new Object[]{"name", 10}, classRepresentation);
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
    public void shouldDeleteByAgeLessThan() {
        DocumentDeleteQuery query = parser.parse("deleteByAgeLessThan", new Object[]{10}, classRepresentation);
        assertEquals("Person", query.getCollection());
        assertEquals(Condition.LESSER_THAN, query.getCondition().get().getCondition());
        assertEquals(Document.of("age", 10), query.getCondition().get().getDocument());
    }

    @Test
    public void shouldDeleteByAgeGreaterThan() {
        DocumentDeleteQuery query = parser.parse("deleteByAgeGreaterThan", new Object[]{10}, classRepresentation);
        assertEquals("Person", query.getCollection());
        assertEquals(Condition.GREATER_THAN, query.getCondition().get().getCondition());
        assertEquals(Document.of("age", 10), query.getCondition().get().getDocument());
    }

    @Test
    public void shouldDeleteByAgeLessEqualThan() {
        DocumentDeleteQuery query = parser.parse("deleteByAgeLessEqualThan", new Object[]{10}, classRepresentation);
        assertEquals("Person", query.getCollection());
        assertEquals(Condition.LESSER_EQUALS_THAN, query.getCondition().get().getCondition());
        assertEquals(Document.of("age", 10), query.getCondition().get().getDocument());
    }

    @Test
    public void shouldDeleteByAgeGreaterEqualThan() {
        DocumentDeleteQuery query = parser.parse("deleteByAgeGreaterEqualThan", new Object[]{10}, classRepresentation);
        assertEquals("Person", query.getCollection());
        assertEquals(Condition.GREATER_EQUALS_THAN, query.getCondition().get().getCondition());
        assertEquals(Document.of("age", 10), query.getCondition().get().getDocument());
    }

    @Test
    public void shouldDeleteByNameLike() {
        DocumentDeleteQuery query = parser.parse("deleteByNameLike", new Object[]{"name"}, classRepresentation);
        assertEquals("Person", query.getCollection());
        assertEquals(Condition.LIKE, query.getCondition().get().getCondition());
        assertEquals(Document.of("name", "name"), query.getCondition().get().getDocument());
    }



    @Test
    public void shouldDeleteByNameAndAAgeBetween() {
        DocumentDeleteQuery query = parser.parse("deleteByNameAndAgeBetween", new Object[]{"name", 10, 11},
                classRepresentation);
        assertEquals("Person", query.getCollection());
        DocumentCondition condition = query.getCondition().get();
        assertEquals(Condition.AND, condition.getCondition());
        List<DocumentCondition> conditions = condition.getDocument().get(new TypeReference<List<DocumentCondition>>() {
        });

        DocumentCondition condition1 = conditions.get(0);
        assertEquals(Condition.EQUALS, condition1.getCondition());
        assertEquals(Document.of("name", "name"), condition1.getDocument());

        DocumentCondition condition2 = conditions.get(1);
        assertEquals(Condition.BETWEEN, condition2.getCondition());
        assertEquals(Document.of("age", Arrays.asList(10, 11)), condition2.getDocument());
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