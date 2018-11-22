/*
 *  Copyright (c) 2018 Otávio Santana and others
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
package org.jnosql.artemis.graph;

import org.apache.tinkerpop.gremlin.structure.Graph;
import org.jnosql.artemis.ConfigurationUnit;
import org.jnosql.artemis.graph.cdi.CDIExtension;
import org.jnosql.artemis.graph.producer.GraphMockA;
import org.jnosql.artemis.graph.producer.GraphMockB;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.inject.Inject;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(CDIExtension.class)
class GraphProducerTest {

    @Inject
    @ConfigurationUnit(fileName = "graph.json", name = "graphA")
    private Graph graphA;

    @Inject
    @ConfigurationUnit(fileName = "graph.json", name = "graphB")
    private Graph graphB;


    @Test
    public void shouldInjectGraphA() {
        Assertions.assertTrue(graphA.getClass().isInstance(GraphMockA.class));
        GraphMockA graphMockA = GraphMockA.class.cast(GraphMockA.class);
        Assertions.assertEquals("key", graphMockA.get("valueA"));
        Assertions.assertEquals("key2", graphMockA.get("valueB"));

    }

    @Test
    public void shouldInjectGraphB() {
        Assertions.assertTrue(graphB.getClass().isInstance(GraphMockB.class));
        GraphMockB graphMockB = GraphMockB.class.cast(GraphMockB.class);
        Assertions.assertEquals("key", graphMockB.get("valueA"));
        Assertions.assertEquals("key2", graphMockB.get("valueB"));

    }
}