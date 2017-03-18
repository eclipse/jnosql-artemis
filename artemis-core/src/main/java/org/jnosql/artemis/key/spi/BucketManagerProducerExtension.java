/*
 * Copyright 2017 Otavio Santana and others
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jnosql.artemis.key.spi;


import org.jnosql.artemis.Database;
import org.jnosql.artemis.Databases;
import org.jnosql.artemis.key.query.KeyValueCrudRepository;
import org.jnosql.artemis.key.query.KeyValueRepositoryBean;
import org.jnosql.diana.api.key.BucketManager;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import javax.enterprise.inject.spi.ProcessProducer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Logger;

import static org.jnosql.artemis.DatabaseType.KEY_VALUE;

/**
 * Extension to start up {@link org.jnosql.artemis.key.KeyValueRepository} and {@link KeyValueCrudRepository}
 * from the {@link javax.enterprise.inject.Default} and {@link Database} qualifier
 */
class BucketManagerProducerExtension implements Extension {

    private static final Logger LOGGER = Logger.getLogger(BucketManagerProducerExtension.class.getName());

    private final List<Database> databases = new ArrayList<>();

    private final Collection<Class<?>> crudTypes = new HashSet<>();

    <T, X extends BucketManager> void processProducer(@Observes final ProcessProducer<T, X> pp) {
        Databases.addDatabase(pp, KEY_VALUE, databases);
    }

    <T extends KeyValueCrudRepository> void onProcessAnnotatedType(@Observes final ProcessAnnotatedType<T> repo) {
        LOGGER.info("Starting the onProcessAnnotatedType");
        crudTypes.add(repo.getAnnotatedType().getJavaClass());
        LOGGER.info("Finished the onProcessAnnotatedType");
    }

    void onAfterBeanDiscovery(@Observes final AfterBeanDiscovery afterBeanDiscovery, final BeanManager beanManager) {
        LOGGER.info("Starting the onAfterBeanDiscovery with elements number: " + databases.size());

        databases.forEach(type -> {
            final org.jnosql.artemis.key.spi.KeyValueRepositoryBean bean = new org.jnosql.artemis.key.spi.KeyValueRepositoryBean(beanManager, type.provider());
            afterBeanDiscovery.addBean(bean);
        });

        crudTypes.forEach(type -> {
            afterBeanDiscovery.addBean(new KeyValueRepositoryBean(type, beanManager, ""));
            databases.forEach(database -> {
                afterBeanDiscovery.addBean(new KeyValueRepositoryBean(type, beanManager, database.provider()));
            });
        });

        LOGGER.info("Finished the onAfterBeanDiscovery");
    }
}
