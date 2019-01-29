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
package org.jnosql.artemis.key.spi;

import org.jnosql.artemis.ConfigurationUnit;
import org.jnosql.artemis.Repository;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.inject.spi.ProcessInjectionPoint;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

/**
 * The keyvalue extension to capture any class that has {@link ConfigurationUnit} annotation
 */
public class KeyValueConfigurationUnitExtension implements Extension {

    private static final Predicate<Annotation> IS_CONFIGURATION_UNIT = a -> ConfigurationUnit.class.isInstance(a);
    private static final List<RepositoryBean> REPOSITORIES = new ArrayList<>();

    public void captureProducerTypes(@Observes final ProcessInjectionPoint<?, ? extends Repository> pip) {
        InjectionPoint injectionPoint = pip.getInjectionPoint();
        Type type = injectionPoint.getType();
        Set<Annotation> qualifiers = injectionPoint.getQualifiers();
        qualifiers.stream().filter(IS_CONFIGURATION_UNIT).findFirst()
                .ifPresent(a -> REPOSITORIES.add(new RepositoryBean(qualifiers, (Class<? extends Repository<?, ?>>) type)));


    }


    public void addProducers(@Observes final AfterBeanDiscovery afterBeanDiscovery, final BeanManager beanManager) {
        System.out.println(REPOSITORIES);

    }


    private static class RepositoryBean {
        private final Set<Annotation> qualifiers;
        private final Class<? extends Repository<?, ?>> repository;

        private RepositoryBean(Set<Annotation> qualifiers, Class<? extends Repository<?, ?>> repository) {
            this.qualifiers = qualifiers;
            this.repository = repository;
        }
    }


}
