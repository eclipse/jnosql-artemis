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
import org.jnosql.artemis.ConfigurationReader;
import org.jnosql.artemis.ConfigurationSettingsUnit;
import org.jnosql.artemis.ConfigurationUnit;
import org.jnosql.artemis.reflection.Reflections;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.Annotated;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;

import static org.jnosql.artemis.util.ConfigurationUnitUtils.getConfigurationUnit;

@ApplicationScoped
class GraphProducer {

    @Inject
    private Reflections reflections;

    @Inject
    private Instance<ConfigurationReader> configurationReader;

    @ConfigurationUnit
    @Produces
    public Graph getBucketManagerGenerics(InjectionPoint injectionPoint) {
        return getBuckerManagerFactocy(injectionPoint);
    }

    private Graph getBuckerManagerFactocy(InjectionPoint injectionPoint) {
        Annotated annotated = injectionPoint.getAnnotated();
        ConfigurationUnit annotation = getConfigurationUnit(injectionPoint, annotated)
                .orElseThrow(() -> new IllegalStateException("The @ConfigurationUnit does not found"));

        ConfigurationSettingsUnit unit = configurationReader.get().read(annotation, GraphFactory.class);
        Class<GraphFactory> configurationClass = unit.<GraphFactory>getProvider()
                .orElseThrow(() -> new IllegalStateException("The ColumnConfiguration provider is required in the configuration"));

        GraphFactory factory = reflections.newInstance(configurationClass);

        return factory.apply(unit.getSettings());
    }
}
