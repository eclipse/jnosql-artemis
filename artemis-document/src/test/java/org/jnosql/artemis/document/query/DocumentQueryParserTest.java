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

import org.jnosql.artemis.CDIExtension;
import org.jnosql.artemis.Converters;
import org.jnosql.artemis.DynamicQueryException;
import org.jnosql.artemis.Pagination;
import org.jnosql.artemis.model.Person;
import org.jnosql.artemis.reflection.ClassRepresentation;
import org.jnosql.artemis.reflection.ClassRepresentations;
import org.jnosql.diana.api.Condition;
import org.jnosql.diana.api.Sort;
import org.jnosql.diana.api.TypeReference;
import org.jnosql.diana.api.document.Document;
import org.jnosql.diana.api.document.DocumentCondition;
import org.jnosql.diana.api.document.DocumentQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


@ExtendWith(CDIExtension.class)
public class DocumentQueryParserTest {

    @Inject
    private ClassRepresentations classRepresentations;

    @Inject
    private Converters converters;

    private DocumentQueryParser parser;

    private ClassRepresentation classRepresentation;

    @BeforeEach
    public void setUp() {
        parser = new DocumentQueryParser();
        classRepresentation = classRepresentations.get(Person.class);
    }

    @Test
    public void shouldFindByName() {
        DocumentQuery query = parser.parse("findByName", new Object[]{"name"},
                classRepresentation, converters);
        assertEquals("Person", query.getDocumentCollection());
        assertEquals(Condition.EQUALS, query.getCondition().get().getCondition());
        assertEquals(Document.of("name", "name"), query.getCondition().get().getDocument());
    }


    @Test
    public void shouldFindByNameAndAge() {
        DocumentQuery query = parser.parse("findByNameAndAge", new Object[]{"name", 10},
                classRepresentation, converters);
        DocumentCondition condition = query.getCondition().get();
        assertEquals("Person", query.getDocumentCollection());
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
    public void shouldFindByNameOrAge() {
        DocumentQuery query = parser.parse("findByNameOrAge", new Object[]{"name", 10},
                classRepresentation, converters);
        DocumentCondition condition = query.getCondition().get();
        assertEquals("Person", query.getDocumentCollection());
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
        DocumentQuery query = parser.parse("findByAgeLessThan", new Object[]{10},
                classRepresentation, converters);
        assertEquals("Person", query.getDocumentCollection());
        assertEquals(Condition.LESSER_THAN, query.getCondition().get().getCondition());
        assertEquals(Document.of("age", 10), query.getCondition().get().getDocument());
    }

    @Test
    public void shouldFindByAgeGreaterThan() {
        DocumentQuery query = parser.parse("findByAgeGreaterThan", new Object[]{10},
                classRepresentation, converters);
        assertEquals("Person", query.getDocumentCollection());
        assertEquals(Condition.GREATER_THAN, query.getCondition().get().getCondition());
        assertEquals(Document.of("age", 10), query.getCondition().get().getDocument());
    }

    @Test
    public void shouldFindByAgeLessThanEqual() {
        DocumentQuery query = parser.parse("findByAgeLessThanEqual", new Object[]{10},
                classRepresentation, converters);
        assertEquals("Person", query.getDocumentCollection());
        assertEquals(Condition.LESSER_EQUALS_THAN, query.getCondition().get().getCondition());
        assertEquals(Document.of("age", 10), query.getCondition().get().getDocument());
    }

    @Test
    public void shouldFindByAgeGreaterThanEqual() {
        DocumentQuery query = parser.parse("findByAgeGreaterThanEqual", new Object[]{10},
                classRepresentation, converters);
        assertEquals("Person", query.getDocumentCollection());
        assertEquals(Condition.GREATER_EQUALS_THAN, query.getCondition().get().getCondition());
        assertEquals(Document.of("age", 10), query.getCondition().get().getDocument());
    }

    @Test
    public void shouldFindByNameLike() {
        DocumentQuery query = parser.parse("findByNameLike", new Object[]{"name"},
                classRepresentation, converters);
        assertEquals("Person", query.getDocumentCollection());
        assertEquals(Condition.LIKE, query.getCondition().get().getCondition());
        assertEquals(Document.of("name", "name"), query.getCondition().get().getDocument());
    }

    @Test
    public void shouldFindByNameLikeOrderByName() {
        DocumentQuery query = parser.parse("findByNameLikeOrderByName", new Object[]{"name"},
                classRepresentation, converters);
        assertEquals("Person", query.getDocumentCollection());
        assertEquals(Condition.LIKE, query.getCondition().get().getCondition());
        assertEquals(Document.of("name", "name"), query.getCondition().get().getDocument());
        assertEquals(Sort.of("name", Sort.SortType.ASC), query.getSorts().get(0));
    }

    @Test
    public void shouldFindByNameLikeOrderByNameAsc() {
        DocumentQuery query = parser.parse("findByNameLikeOrderByNameAsc", new Object[]{"name"}
                , classRepresentation, converters);
        assertEquals("Person", query.getDocumentCollection());
        assertEquals(Condition.LIKE, query.getCondition().get().getCondition());
        assertEquals(Document.of("name", "name"), query.getCondition().get().getDocument());
        assertEquals(Sort.of("name", Sort.SortType.ASC), query.getSorts().get(0));
    }

    @Test
    public void shouldFindByNameLikeOrderByNameDesc() {
        DocumentQuery query = parser.parse("findByNameLikeOrderByNameDesc", new Object[]{"name"},
                classRepresentation, converters);
        assertEquals("Person", query.getDocumentCollection());
        assertEquals(Condition.LIKE, query.getCondition().get().getCondition());
        assertEquals(Document.of("name", "name"), query.getCondition().get().getDocument());
        assertEquals(Sort.of("name", Sort.SortType.DESC), query.getSorts().get(0));
    }

    @Test
    public void shouldFindByNameLikeOrderByNameDescOrderByAgeAsc() {
        DocumentQuery query = parser.parse("findByNameLikeOrderByNameDescOrderByAgeAsc", new Object[]{"name"},
                classRepresentation, converters);
        assertEquals("Person", query.getDocumentCollection());
        assertEquals(Condition.LIKE, query.getCondition().get().getCondition());
        assertEquals(Document.of("name", "name"), query.getCondition().get().getDocument());
        assertEquals(Sort.of("name", Sort.SortType.DESC), query.getSorts().get(0));
        assertEquals(Sort.of("age", Sort.SortType.ASC), query.getSorts().get(1));
    }

    @Test
    public void shouldFindByNameAndAAgeBetween() {
        DocumentQuery query = parser.parse("findByNameAndAgeBetween", new Object[]{"name", 10, 11},
                classRepresentation, converters);
        assertEquals("Person", query.getDocumentCollection());
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

    @Test
    public void shouldReturnErrorWhenIsMissedArgument() {
        assertThrows(DynamicQueryException.class, () -> {
            DocumentQuery query = parser.parse("findByNameAndAgeBetween", new Object[]{"name", 10},
                    classRepresentation, converters);
        });
    }

    @Test
    public void shouldReturnErrorWhenIsMissedArgument2() {
        assertThrows(DynamicQueryException.class, () -> {
            DocumentQuery query = parser.parse("findByName", new Object[]{},
                    classRepresentation, converters);
        });
    }

    @Test
    public void shouldFindByNameWithSortArgument() {
        Sort sort = Sort.of("age", Sort.SortType.ASC);
        DocumentQuery query = parser.parse("findByName", new Object[]{"name",
                sort}, classRepresentation, converters);

        assertEquals("Person", query.getDocumentCollection());
        assertEquals(Condition.EQUALS, query.getCondition().get().getCondition());
        assertEquals(Document.of("name", "name"), query.getCondition().get().getDocument());
        assertEquals(sort, query.getSorts().get(0));
    }

    @Test
    public void shouldFindByNameWithPageArgument() {
        Pagination pagination = Pagination.of(2L, 10);
        DocumentQuery query = parser.parse("findByName", new Object[]{"name",
                pagination}, classRepresentation, converters);

        assertEquals("Person", query.getDocumentCollection());
        assertEquals(Condition.EQUALS, query.getCondition().get().getCondition());
        assertEquals(Document.of("name", "name"), query.getCondition().get().getDocument());
        assertEquals(pagination.getMaxResults(), query.getMaxResults());
        assertEquals(pagination.getFirstResult(), query.getFirstResult());
    }

    @Test
    public void shouldFindByNameWithPageSortArgument() {
        Pagination pagination = Pagination.of(2L, 10);
        Sort sort = Sort.of("age", Sort.SortType.ASC);
        DocumentQuery query = parser.parse("findByName", new Object[]{"name",
                pagination, sort}, classRepresentation, converters);

        assertEquals("Person", query.getDocumentCollection());
        assertEquals(Condition.EQUALS, query.getCondition().get().getCondition());
        assertEquals(Document.of("name", "name"), query.getCondition().get().getDocument());
        assertEquals(pagination.getMaxResults(), query.getMaxResults());
        assertEquals(pagination.getFirstResult(), query.getFirstResult());
        assertEquals(sort, query.getSorts().get(0));
    }

    @Test
    public void shouldFindByNameWithPageSortArgumentAndIgnore() {
        Pagination pagination = Pagination.of(2L, 10);
        Sort sort = Sort.of("age", Sort.SortType.ASC);
        DocumentQuery query = parser.parse("findByName", new Object[]{"name",
                pagination, sort, "ignore"}, classRepresentation, converters);

        assertEquals("Person", query.getDocumentCollection());
        assertEquals(Condition.EQUALS, query.getCondition().get().getCondition());
        assertEquals(Document.of("name", "name"), query.getCondition().get().getDocument());
        assertEquals(pagination.getMaxResults(), query.getMaxResults());
        assertEquals(pagination.getFirstResult(), query.getFirstResult());
        assertEquals(sort, query.getSorts().get(0));
    }

    @Test
    public void shouldConvertsJavaFieldToColumn() {
        DocumentQuery query = parser.parse("findById", new Object[]{"10"}, classRepresentation, converters);
        assertEquals("Person", query.getDocumentCollection());
        assertEquals(Condition.EQUALS, query.getCondition().get().getCondition());
        assertEquals(Document.of("_id", 10L), query.getCondition().get().getDocument());
    }
}