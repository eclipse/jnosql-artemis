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

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.inject.spi.ProcessInjectionPoint;
import javax.enterprise.inject.spi.ProcessProducerMethod;

import static java.util.Collections.singletonList;

/**
 * It creates a KeyValueTemplate from a ConfigurationUnit annotation.
 */
public class KeyValueRepositoryConfigurationExtension implements Extension {

    private static final Predicate<? extends Class<? extends Annotation>> CLASS_PREDICATE = a -> a.isAnnotationPresent(ConfigurationUnit.class);
    private static final Function<Class<?>, GenericProducer> NEW_SET = (k) -> new GenericProducer();

    private final Map<Class<?>, GenericProducer> typesByProducer = new HashMap<>();

    public void captureProducerTypes(@Observes final ProcessInjectionPoint<?, ? extends Repository> pip) {
        System.out.println(pip);
        InjectionPoint injectionPoint = pip.getInjectionPoint();
        Type type = injectionPoint.getType();
        Set<Annotation> qualifiers = injectionPoint.getQualifiers();
        Optional<Annotation> first = qualifiers.stream().filter(a -> ConfigurationUnit.class.isInstance(a)).findFirst();
        System.out.println(first);

    }


    public void addProducers(@Observes final AfterBeanDiscovery abd) {

    }


    private static class GenericProducer {
        private final Set<Type> types = new HashSet<>(singletonList(Object.class));
        private Bean<?> bean;
    }



}
