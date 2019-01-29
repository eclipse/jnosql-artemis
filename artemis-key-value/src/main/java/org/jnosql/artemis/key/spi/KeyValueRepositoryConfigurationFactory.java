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
import org.jnosql.artemis.key.KeyRepositorySupplier;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.InjectionPoint;
import java.lang.annotation.Annotation;
import java.lang.reflect.Member;
import java.lang.reflect.ParameterizedType;
import java.util.Set;

@ApplicationScoped
class KeyValueRepositoryConfigurationFactory {

    @ConfigurationUnit
    @Produces
    public <R extends Repository<?,?>> KeyRepositorySupplier<R> get(InjectionPoint injectionPoint) {
        Member member = injectionPoint.getMember();
        Bean<?> bean = injectionPoint.getBean();
        Set<Annotation> qualifiers = injectionPoint.getQualifiers();
        ParameterizedType type= (ParameterizedType) injectionPoint.getType();
        Class repository = (Class) type.getActualTypeArguments()[0];
        return null;
    }
}
