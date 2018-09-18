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

import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.jnosql.artemis.Converters;
import org.jnosql.artemis.Repository;
import org.jnosql.artemis.graph.cdi.CDIExtension;
import org.jnosql.artemis.graph.model.Person;
import org.jnosql.artemis.reflection.ClassRepresentation;
import org.jnosql.artemis.reflection.ClassRepresentations;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import javax.inject.Inject;
import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(CDIExtension.class)
class SelectQueryConverterTest {

    @Inject
    private SelectQueryConverter converter;

    @Inject
    private ClassRepresentations representations;

    @Inject
    private Converters converters;

    @Inject
    private Graph graph;

    @BeforeEach
    public void setUp() {
        graph.traversal().E().toList().forEach(Edge::remove);
        graph.traversal().V().toList().forEach(Vertex::remove);
    }

    @ParameterizedTest(name = "Should parser the query {0}")
    @ValueSource(strings = {"findByName"})
    public void shouldRunQuery(String methodName) {
        Method method = Stream.of(PersonRepository.class.getMethods())
                .filter(m -> m.getName().equals(methodName)).findFirst().get();

        graph.addVertex("Person").property("name", "Otavio");
        graph.addVertex("Person").property("name", "Ada");
        graph.addVertex("Person").property("name", "Poliana");
        ClassRepresentation representation = representations.get(Person.class);
        GraphQueryMethod queryMethod = new GraphQueryMethod(representation,graph.traversal().V(),
                converters, method, new Object[]{"Ada"});

        List<Vertex> vertices = converter.apply(queryMethod);
        assertEquals(1, vertices.size());
        assertEquals("Ada", vertices.get(0).value("name"));
    }



    interface PersonRepository extends Repository<Person, String> {

        List<Person> findByName(String name);
    }

}