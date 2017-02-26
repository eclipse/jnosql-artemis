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
package org.jnosql.artemis.document.query;


import org.jnosql.artemis.CrudRepository;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import java.util.Collection;
import java.util.HashSet;
import java.util.logging.Logger;

/**
 * The extenstion to startup {@link CrudRepository}
 * as {@link CrudRepositoryDocumentBean}
 */
class DocumentCrudRepositoryExtension implements Extension {

    private static final Logger LOGGER = Logger.getLogger(DocumentCrudRepositoryExtension.class.getName());

    private final Collection<Class<?>> types = new HashSet<>();

    <T extends CrudRepository> void onProcessAnnotatedType(@Observes final ProcessAnnotatedType<T> repo) {
        LOGGER.info("Starting the onProcessAnnotatedType");
        Class<T> javaClass = repo.getAnnotatedType().getJavaClass();
        types.add(javaClass);
        LOGGER.info("Finished the onProcessAnnotatedType");
    }

    void onAfterBeanDiscovery(@Observes final AfterBeanDiscovery afterBeanDiscovery, final BeanManager beanManager) {
        LOGGER.info("Starting the onAfterBeanDiscovery with elements number: " + types.size());
        types.forEach(t -> {
            final CrudRepositoryDocumentBean bean = new CrudRepositoryDocumentBean(t, beanManager);
            afterBeanDiscovery.addBean(bean);
        });
        LOGGER.info("Finished the onAfterBeanDiscovery");
    }


}
