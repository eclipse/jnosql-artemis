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
package org.jnosql.artemis;


import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import java.util.Objects;

/**
 * The Default implementation to {@link Converters}
 */
@ApplicationScoped
class DefaultConverters implements Converters {

    @Inject
    private BeanManager beanManager;

    @Override
    public AttributeConverter get(Class<? extends AttributeConverter> converterClass) throws NullPointerException {
        Objects.requireNonNull(converterClass, "The converterClass is required");
        return getInstance(converterClass);
    }

    private <T> T getInstance(Class<T> clazz) {
        Bean<T> bean = (Bean<T>) beanManager.getBeans(clazz).iterator().next();
        CreationalContext<T> ctx = beanManager.createCreationalContext(bean);
        return (T) beanManager.getReference(bean, clazz, ctx);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("DefaultConverters{");
        sb.append("beanManager=").append(beanManager);
        sb.append('}');
        return sb.toString();
    }
}
