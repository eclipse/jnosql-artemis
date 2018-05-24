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
package org.jnosql.artemis.graph.query;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.T;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.VertexProperty;
import org.jnosql.artemis.DynamicQueryException;
import org.jnosql.artemis.graph.cdi.CDIExtension;
import org.jnosql.artemis.graph.model.Person;
import org.jnosql.artemis.reflection.ClassRepresentation;
import org.jnosql.artemis.reflection.ClassRepresentations;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(CDIExtension.class)
public class GraphQueryParserTest {

    @Inject
    private ClassRepresentations classRepresentations;

    private GraphQueryParser parser;

    private ClassRepresentation classRepresentation;

    @Inject
    private Graph graph;

    @BeforeEach
    public void setUp() {
        graph.traversal().V().toList().forEach(Vertex::remove);
        graph.traversal().E().toList().forEach(Edge::remove);


        parser = new GraphQueryParser();
        classRepresentation = classRepresentations.get(Person.class);
    }

    @AfterEach
    public void after() {
        graph.traversal().V().toList().forEach(Vertex::remove);
        graph.traversal().E().toList().forEach(Edge::remove);

    }

    @Test
    public void shouldFindByName() {
        graph.addVertex(T.label, "Person", "name", "name");

        GraphTraversal<Vertex, Vertex> traversal = graph.traversal().V();
        parser.findByParse("findByName", new Object[]{"name"}, classRepresentation, traversal);
        Optional<Vertex> vertex = traversal.tryNext();
        assertTrue(vertex.isPresent());
        assertEquals("Person", vertex.get().label());
        VertexProperty<Object> name = vertex.get().property("name");
        assertEquals("name", name.value());

    }

    @Test
    public void shouldFindByNameAndAge() {
        graph.addVertex(T.label, "Person", "name", "name", "age", 10);

        GraphTraversal<Vertex, Vertex> traversal = graph.traversal().V();
        parser.findByParse("findByNameAndAge", new Object[]{"name", 10}, classRepresentation, traversal);

        Optional<Vertex> vertex = traversal.tryNext();
        assertTrue(vertex.isPresent());
        assertEquals("Person", vertex.get().label());
        VertexProperty<Object> name = vertex.get().property("name");
        VertexProperty<Object> age = vertex.get().property("age");
        assertEquals("name", name.value());
        assertEquals(10, age.value());

    }


    @Test
    public void shouldFindByAgeLessThan() {

        graph.addVertex(T.label, "Person", "name", "name", "age", 10);
        graph.addVertex(T.label, "Person", "name", "name2", "age", 9);
        graph.addVertex(T.label, "Person", "name", "name3", "age", 8);

        GraphTraversal<Vertex, Vertex> traversal = graph.traversal().V();
        parser.findByParse("findByAgeLessThan", new Object[]{10}, classRepresentation, traversal);
        assertEquals(2, traversal.toList().size());

    }

    @Test
    public void shouldFindByAgeGreaterThan() {
        graph.addVertex(T.label, "Person", "name", "name", "age", 10);
        graph.addVertex(T.label, "Person", "name", "name2", "age", 9);
        graph.addVertex(T.label, "Person", "name", "name3", "age", 11);
        graph.addVertex(T.label, "Person", "name", "name4", "age", 12);
        graph.addVertex(T.label, "Person", "name", "name5", "age", 13);
        GraphTraversal<Vertex, Vertex> traversal = graph.traversal().V();

        parser.findByParse("findByAgeGreaterThan", new Object[]{10}, classRepresentation, traversal);
        assertEquals(3, traversal.toList().size());


    }


    @Test
    public void shouldFindByAgeLessThanEqual() {
        graph.addVertex(T.label, "Person", "name", "name", "age", 10);
        graph.addVertex(T.label, "Person", "name", "name2", "age", 9);
        graph.addVertex(T.label, "Person", "name", "name3", "age", 11);
        graph.addVertex(T.label, "Person", "name", "name4", "age", 12);
        graph.addVertex(T.label, "Person", "name", "name5", "age", 13);

        GraphTraversal<Vertex, Vertex> traversal = graph.traversal().V();

        parser.findByParse("findByAgeLessThanEqual", new Object[]{10}, classRepresentation, traversal);
        assertEquals(2, traversal.toList().size());

    }

    @Test
    public void shouldFindByAgeGreaterEqualThan() {
        graph.addVertex(T.label, "Person", "name", "name", "age", 10);
        graph.addVertex(T.label, "Person", "name", "name2", "age", 9);
        graph.addVertex(T.label, "Person", "name", "name3", "age", 11);
        graph.addVertex(T.label, "Person", "name", "name4", "age", 12);
        graph.addVertex(T.label, "Person", "name", "name5", "age", 13);

        GraphTraversal<Vertex, Vertex> traversal = graph.traversal().V();


        parser.findByParse("findByAgeGreaterEqualThan", new Object[]{10}, classRepresentation, traversal);
    }


    @Test
    public void shouldFindByNameAndAgeBetween() {
        graph.addVertex(T.label, "Person", "name", "name", "age", 10);
        graph.addVertex(T.label, "Person", "name", "name2", "age", 9);
        graph.addVertex(T.label, "Person", "name", "name3", "age", 11);
        graph.addVertex(T.label, "Person", "name", "name4", "age", 12);
        graph.addVertex(T.label, "Person", "name", "name5", "age", 13);

        GraphTraversal<Vertex, Vertex> traversal = graph.traversal().V();

        parser.findByParse("findByNameAndAgeBetween", new Object[]{"name", 10, 12},
                classRepresentation, traversal);

        assertEquals(1, traversal.toList().size());
    }

    @Test
    public void shouldFindByKnowsOutV() {
        Vertex poliana = graph.addVertex(T.label, "Person", "name", "Poliana", "age", 10);
        Vertex otavio = graph.addVertex(T.label, "Person", "name", "Otavio", "age", 9);

        poliana.addEdge("knows", otavio);


        GraphTraversal<Vertex, Vertex> traversal = graph.traversal().V();

        parser.findByParse("findByKnowsOutV", new Object[]{},
                classRepresentation, traversal);

        Optional<Vertex> vertex = traversal.tryNext();
        assertTrue(vertex.isPresent());
        assertEquals(otavio.id(), vertex.get().id());
    }

    @Test
    public void shouldFindByOutV() {
        Vertex poliana = graph.addVertex(T.label, "Person", "name", "Poliana", "age", 10);
        Vertex otavio = graph.addVertex(T.label, "Person", "name", "Otavio", "age", 9);

        poliana.addEdge("knows", otavio);


        GraphTraversal<Vertex, Vertex> traversal = graph.traversal().V();

        parser.findByParse("findByOutV", new Object[]{"knows"},
                classRepresentation, traversal);

        Optional<Vertex> vertex = traversal.tryNext();
        assertTrue(vertex.isPresent());
        assertEquals(otavio.id(), vertex.get().id());
    }

    @Test
    public void shouldFindByKnowsInV() {
        Vertex poliana = graph.addVertex(T.label, "Person", "name", "Poliana", "age", 10);
        Vertex otavio = graph.addVertex(T.label, "Person", "name", "Otavio", "age", 9);

        poliana.addEdge("knows", otavio);

        GraphTraversal<Vertex, Vertex> traversal = graph.traversal().V();

        parser.findByParse("findByKnowsInV", new Object[]{},
                classRepresentation, traversal);

        Optional<Vertex> vertex = traversal.tryNext();
        assertTrue(vertex.isPresent());
        assertEquals(poliana.id(), vertex.get().id());
    }

    @Test
    public void shouldFindByInV() {
        Vertex poliana = graph.addVertex(T.label, "Person", "name", "Poliana", "age", 10);
        Vertex otavio = graph.addVertex(T.label, "Person", "name", "Otavio", "age", 9);

        poliana.addEdge("knows", otavio);

        GraphTraversal<Vertex, Vertex> traversal = graph.traversal().V();

        parser.findByParse("findByInV", new Object[]{"knows"},
                classRepresentation, traversal);

        Optional<Vertex> vertex = traversal.tryNext();
        assertTrue(vertex.isPresent());
        assertEquals(poliana.id(), vertex.get().id());
    }


    @Test
    public void shouldFindByKnowsBothV() {
        Vertex poliana = graph.addVertex(T.label, "Person", "name", "Poliana", "age", 10);
        Vertex otavio = graph.addVertex(T.label, "Person", "name", "Otavio", "age", 9);

        poliana.addEdge("knows", otavio);

        GraphTraversal<Vertex, Vertex> traversal = graph.traversal().V();

        parser.findByParse("findByKnowsBothV", new Object[]{},
                classRepresentation, traversal);

        List<Vertex> vertices = traversal.toList();

        List<Object> ids = vertices.stream().map(Vertex::id).collect(toList());
        assertThat(ids, containsInAnyOrder(poliana.id(), otavio.id()));
    }

    @Test
    public void shouldFindByBothV() {
        Vertex poliana = graph.addVertex(T.label, "Person", "name", "Poliana", "age", 10);
        Vertex otavio = graph.addVertex(T.label, "Person", "name", "Otavio", "age", 9);

        poliana.addEdge("knows", otavio);

        GraphTraversal<Vertex, Vertex> traversal = graph.traversal().V();

        parser.findByParse("findByBothV", new Object[]{"knows"},
                classRepresentation, traversal);

        List<Vertex> vertices = traversal.toList();

        List<Object> ids = vertices.stream().map(Vertex::id).collect(toList());
        assertThat(ids, containsInAnyOrder(poliana.id(), otavio.id()));
    }

    @Test
    public void shouldFindByNameAndKnowsOutV() {
        Vertex poliana = graph.addVertex(T.label, "Person", "name", "Poliana", "age", 10);
        Vertex otavio = graph.addVertex(T.label, "Person", "name", "Otavio", "age", 9);

        poliana.addEdge("knows", otavio);

        GraphTraversal<Vertex, Vertex> traversal = graph.traversal().V();

        parser.findByParse("findByNameAndKnowsOutV", new Object[]{"Poliana"},
                classRepresentation, traversal);

        Optional<Vertex> vertex = traversal.tryNext();
        assertTrue(vertex.isPresent());
        assertEquals(otavio.id(), vertex.get().id());

    }

    @Test
    public void shouldFindByKnowsOutVAndName() {
        Vertex poliana = graph.addVertex(T.label, "Person", "name", "Poliana", "age", 10);
        Vertex otavio = graph.addVertex(T.label, "Person", "name", "Otavio", "age", 9);

        poliana.addEdge("knows", otavio);

        GraphTraversal<Vertex, Vertex> traversal = graph.traversal().V();

        parser.findByParse("findByKnowsOutVAndName", new Object[]{"Otavio"},
                classRepresentation, traversal);

        Optional<Vertex> vertex = traversal.tryNext();
        assertTrue(vertex.isPresent());
        assertEquals(otavio.id(), vertex.get().id());

    }

    @Test
    public void shouldReturnErrorWhenIsMissedArgument() {
        assertThrows(DynamicQueryException.class, () -> {
            GraphTraversal<Vertex, Vertex> traversal = graph.traversal().V();

            parser.findByParse("findByNameAndAgeBetween", new Object[]{"name", 10},
                    classRepresentation, traversal);
        });
    }

    @Test
    public void shouldReturnErrorWhenIsMissedArgument2() {
        assertThrows(DynamicQueryException.class, () -> {
            GraphTraversal<Vertex, Vertex> traversal = graph.traversal().V();
            parser.findByParse("findByName", new Object[]{},
                    classRepresentation, traversal);
        });

    }

}