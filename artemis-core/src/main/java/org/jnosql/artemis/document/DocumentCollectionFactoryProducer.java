/*
 *  Copyright (c) 2017 Otávio Santana and others
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
package org.jnosql.artemis.document;

import org.jnosql.artemis.ConfigurationUnit;
import org.jnosql.artemis.configuration.ConfigurationReader;
import org.jnosql.artemis.configuration.ConfigurationSettingsUnit;
import org.jnosql.artemis.reflection.Reflections;
import org.jnosql.diana.api.column.ColumnConfiguration;
import org.jnosql.diana.api.column.ColumnConfigurationAsync;
import org.jnosql.diana.api.document.DocumentCollectionManager;
import org.jnosql.diana.api.document.DocumentCollectionManagerAsync;
import org.jnosql.diana.api.document.DocumentCollectionManagerAsyncFactory;
import org.jnosql.diana.api.document.DocumentCollectionManagerFactory;
import org.jnosql.diana.api.document.DocumentConfiguration;
import org.jnosql.diana.api.document.DocumentConfigurationAsync;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.Annotated;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;

/**
 * The class that creates {@link DocumentCollectionManagerFactory} and {@link DocumentCollectionManagerFactory}
 * from the {@link ConfigurationUnit}
 */
@ApplicationScoped
class DocumentCollectionFactoryProducer {

    @Inject
    private Reflections reflections;

    @Inject
    private ConfigurationReader configurationReader;


    @ConfigurationUnit
    @Produces
    public <T extends DocumentCollectionManager> DocumentCollectionManagerFactory<T> getColumnConfigurationGenerics(InjectionPoint injectionPoint) {
        return getDocumentCollection(injectionPoint);
    }

    @ConfigurationUnit
    @Produces
    public DocumentCollectionManagerFactory getColumnConfiguration(InjectionPoint injectionPoint) {
        return getDocumentCollection(injectionPoint);
    }


    @ConfigurationUnit
    @Produces
    public <T extends DocumentCollectionManagerAsync> DocumentCollectionManagerAsyncFactory<T> getDocumentManagerAsyncGenerics(InjectionPoint injectionPoint) {
        return getDocumentCollectionAsync(injectionPoint);
    }


    @ConfigurationUnit
    @Produces
    public DocumentCollectionManagerAsyncFactory getDocumentManagerAsync(InjectionPoint injectionPoint) {
        return getDocumentCollectionAsync(injectionPoint);
    }


    private <T extends DocumentCollectionManagerAsync> DocumentCollectionManagerAsyncFactory<T>
    getDocumentCollectionAsync(InjectionPoint injectionPoint) {

        Annotated annotated = injectionPoint.getAnnotated();
        ConfigurationUnit annotation = annotated.getAnnotation(ConfigurationUnit.class);

        ConfigurationSettingsUnit unit = configurationReader.read(annotation, DocumentConfigurationAsync.class);
        Class<DocumentConfigurationAsync> configurationClass = unit.<DocumentConfigurationAsync>getProvider()
                .orElseThrow(() -> new IllegalStateException("The DocumentConfiguration provider is required in the configuration"));

        DocumentConfigurationAsync documentConfiguration = reflections.newInstance(configurationClass);
        DocumentCollectionManagerAsyncFactory documentFactory = documentConfiguration.getAsync(unit.getSettings());


        return documentFactory;
    }

    private <T extends DocumentCollectionManager> DocumentCollectionManagerFactory<T> getDocumentCollection(InjectionPoint injectionPoint) {
        Annotated annotated = injectionPoint.getAnnotated();
        ConfigurationUnit annotation = annotated.getAnnotation(ConfigurationUnit.class);

        ConfigurationSettingsUnit unit = configurationReader.read(annotation, DocumentConfiguration.class);
        Class<DocumentConfiguration> configurationClass = unit.<DocumentConfiguration>getProvider()
                .orElseThrow(() -> new IllegalStateException("The DocumentConfiguration provider is required in the configuration"));

        DocumentConfiguration columnConfiguration = reflections.newInstance(configurationClass);
        DocumentCollectionManagerFactory documentFactory = columnConfiguration.get(unit.getSettings());


        return documentFactory;
    }
}
