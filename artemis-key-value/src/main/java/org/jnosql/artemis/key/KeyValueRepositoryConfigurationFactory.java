/*
 *  Copyright (c) 2019 Otávio Santana and others
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
package org.jnosql.artemis.key;

import org.jnosql.artemis.ConfigurationException;
import org.jnosql.artemis.ConfigurationUnit;
import org.jnosql.artemis.Repository;
import org.jnosql.artemis.util.StringUtils;
import org.jnosql.diana.api.key.BucketManager;
import org.jnosql.diana.api.key.BucketManagerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;

import static org.jnosql.artemis.util.ConfigurationUnitUtils.getConfigurationUnit;

/**
 * It creates a KeyValueTemplate from a ConfigurationUnit annotation.
 */
@ApplicationScoped
class KeyValueRepositoryConfigurationFactory {


    @Inject
    private KeyValueTemplateConfigurationFactory templateProducer;

    @Inject
    private KeyValueRepositoryProducer keyValueTemplateProducer;

    @ConfigurationUnit
    @Produces
    public <T extends Repository<?,?>> T getRepository(InjectionPoint injectionPoint) {
        KeyValueTemplate template = templateProducer.getKeyValueTemplate(injectionPoint);
        ConfigurationUnit annotation = getConfigurationUnit(injectionPoint, injectionPoint.getAnnotated());
        return (T) keyValueTemplateProducer.get(Repository.class, template);
    }
}
