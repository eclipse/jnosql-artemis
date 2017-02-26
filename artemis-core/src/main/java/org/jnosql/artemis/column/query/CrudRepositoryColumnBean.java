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
package org.jnosql.artemis.column.query;

import org.jnosql.artemis.ArtemisDatabase;
import org.jnosql.artemis.CrudRepository;
import org.jnosql.artemis.DatabaseType;
import org.jnosql.artemis.column.ColumnRepository;
import org.jnosql.artemis.reflection.ClassRepresentations;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.inject.spi.PassivationCapable;
import javax.enterprise.util.AnnotationLiteral;
import java.lang.annotation.Annotation;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static java.util.Arrays.asList;

/**
 * Artemis discoveryBean to CDI extension to register {@link CrudRepository}
 */
public class CrudRepositoryColumnBean implements Bean<CrudRepository>, PassivationCapable {

    private final Class type;

    private final BeanManager beanManager;

    private final Set<Type> types = new HashSet<>();

    private final String provider;

    /**
     * Constructor
     *
     * @param type        the tye
     * @param beanManager the beanManager
     * @param provider    the provider name, that must be a
     */
    public CrudRepositoryColumnBean(Class type, BeanManager beanManager, String provider) {
        this.type = type;
        this.beanManager = beanManager;
        this.types.add(type);
        this.provider = provider;
    }

    CrudRepositoryColumnBean(Class type, BeanManager beanManager) {
        this.type = type;
        this.beanManager = beanManager;
        this.types.addAll(asList(Object.class, type));
        this.provider = "";
    }

    @Override
    public Class<?> getBeanClass() {
        return type;
    }

    @Override
    public Set<InjectionPoint> getInjectionPoints() {
        return Collections.emptySet();
    }

    @Override
    public boolean isNullable() {
        return false;
    }

    @Override
    public CrudRepository create(CreationalContext<CrudRepository> creationalContext) {
        ClassRepresentations classRepresentations = getInstance(ClassRepresentations.class);
        ColumnRepository repository = provider.isEmpty() ? getInstance(ColumnRepository.class) :
                getInstance(ColumnRepository.class, provider);
        ColumnCrudRepositoryProxy handler = new ColumnCrudRepositoryProxy(repository,
                classRepresentations, type);
        return (CrudRepository) Proxy.newProxyInstance(type.getClassLoader(),
                new Class[]{type},
                handler);
    }


    private <T> T getInstance(Class<T> clazz) {
        Bean<T> bean = (Bean<T>) beanManager.getBeans(clazz).iterator().next();
        CreationalContext<T> ctx = beanManager.createCreationalContext(bean);
        return (T) beanManager.getReference(bean, clazz, ctx);
    }

    private <T> T getInstance(Class<T> clazz, String name) {
        Bean bean = beanManager.getBeans(clazz, new ArtemisDatabaseQualifier(name)).iterator().next();
        CreationalContext ctx = beanManager.createCreationalContext(bean);
        return (T) beanManager.getReference(bean, clazz, ctx);
    }


    @Override
    public void destroy(CrudRepository instance, CreationalContext<CrudRepository> creationalContext) {

    }

    @Override
    public Set<Type> getTypes() {
        return types;
    }

    @Override
    public Set<Annotation> getQualifiers() {
        Set<Annotation> qualifiers = new HashSet<Annotation>();
        qualifiers.add(new ArtemisDatabaseQualifier(provider));
        return qualifiers;
    }

    @Override
    public Class<? extends Annotation> getScope() {
        return ApplicationScoped.class;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public Set<Class<? extends Annotation>> getStereotypes() {
        return Collections.emptySet();
    }

    @Override
    public boolean isAlternative() {
        return false;
    }

    @Override
    public String getId() {
        return type.getName() + '@' + DatabaseType.COLUMN + "-" + provider;
    }

    static class ArtemisDatabaseQualifier extends AnnotationLiteral<ArtemisDatabase> implements ArtemisDatabase {

        private final String provider;

        ArtemisDatabaseQualifier(String provider) {
            this.provider = provider;
        }

        @Override
        public DatabaseType value() {
            return DatabaseType.COLUMN;
        }

        @Override
        public String provider() {
            return provider;
        }
    }
}