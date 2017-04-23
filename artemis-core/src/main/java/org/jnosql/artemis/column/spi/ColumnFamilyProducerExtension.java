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
package org.jnosql.artemis.column.spi;


import org.jnosql.artemis.CrudRepository;
import org.jnosql.artemis.CrudRepositoryAsync;
import org.jnosql.artemis.Database;
import org.jnosql.artemis.Databases;
import org.jnosql.artemis.column.query.CrudRepositoryAsyncColumnBean;
import org.jnosql.artemis.column.query.CrudRepositoryColumnBean;
import org.jnosql.diana.api.column.ColumnFamilyManager;
import org.jnosql.diana.api.column.ColumnFamilyManagerAsync;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import javax.enterprise.inject.spi.ProcessProducer;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Stream;

import static org.jnosql.artemis.DatabaseType.COLUMN;

/**
 * Extension to start up the ColumnRepository, ColumnRepositoryAsync, CrudRepository and CrudRepositoryAsync
 * from the {@link Database} qualifier
 */
public class ColumnFamilyProducerExtension implements Extension {

    private static final Logger LOGGER = Logger.getLogger(ColumnFamilyProducerExtension.class.getName());

    private final List<Database> databases = new ArrayList<>();

    private final List<Database> databasesAsync = new ArrayList<>();

    private final Collection<Class<?>> crudTypes = new HashSet<>();

    private final Collection<Class<?>> crudAsyncTypes = new HashSet<>();


    <T extends CrudRepository> void onProcessAnnotatedType(@Observes final ProcessAnnotatedType<T> repo) {
        Class<T> javaClass = repo.getAnnotatedType().getJavaClass();
        if (CrudRepository.class.equals(javaClass)) {
            return;
        }
        if (Stream.of(javaClass.getInterfaces()).anyMatch(CrudRepository.class::equals)
                && Modifier.isInterface(javaClass.getModifiers())) {
            LOGGER.info("Adding a new CrudRepository as discovered on Column: " + javaClass);
            crudTypes.add(javaClass);
        }
    }

    <T extends CrudRepositoryAsync> void onProcessAnnotatedTypeAsync(@Observes final ProcessAnnotatedType<T> repo) {
        Class<T> javaClass = repo.getAnnotatedType().getJavaClass();
        if (CrudRepositoryAsync.class.equals(javaClass)) {
            return;
        }
        if (Stream.of(javaClass.getInterfaces()).anyMatch(CrudRepositoryAsync.class::equals)
                && Modifier.isInterface(javaClass.getModifiers())) {
            LOGGER.info("Adding a new CrudRepositoryAsync as discovered on Column: " + javaClass);
            crudAsyncTypes.add(javaClass);
        }
    }

    <T, X extends ColumnFamilyManager> void processProducer(@Observes final ProcessProducer<T, X> pp) {
        Databases.addDatabase(pp, COLUMN, databases);
    }

    <T, X extends ColumnFamilyManagerAsync> void processProducerAsync(@Observes final ProcessProducer<T, X> pp) {
        Databases.addDatabase(pp, COLUMN, databasesAsync);
    }

    void onAfterBeanDiscovery(@Observes final AfterBeanDiscovery afterBeanDiscovery, final BeanManager beanManager) {
        LOGGER.info(String.format("Starting to process on columns: %d databases crud %d and crudAsync %d",
                databases.size(), crudTypes.size(), crudAsyncTypes.size()));
        databases.forEach(type -> {
            final ColumnRepositoryBean bean = new ColumnRepositoryBean(beanManager, type.provider());
            afterBeanDiscovery.addBean(bean);
        });

        databasesAsync.forEach(type -> {
            final ColumnRepositoryAsyncBean bean = new ColumnRepositoryAsyncBean(beanManager, type.provider());
            afterBeanDiscovery.addBean(bean);
        });

        crudTypes.forEach(type -> {
            afterBeanDiscovery.addBean(new CrudRepositoryColumnBean(type, beanManager, ""));
            databases.forEach(database -> afterBeanDiscovery
                    .addBean(new CrudRepositoryColumnBean(type, beanManager, database.provider())));
        });

        crudAsyncTypes.forEach(type -> {
            afterBeanDiscovery.addBean(new CrudRepositoryAsyncColumnBean(type, beanManager, ""));
            databasesAsync.forEach(database -> afterBeanDiscovery
                    .addBean(new CrudRepositoryAsyncColumnBean(type, beanManager, database.provider())));
        });


    }

}
