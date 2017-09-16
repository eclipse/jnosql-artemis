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
package org.jnosql.artemis.configuration;

import org.jnosql.artemis.ConfigurationUnit;
import org.jnosql.artemis.reflection.ConstructorException;
import org.jnosql.artemis.reflection.Reflections;
import org.jnosql.diana.api.Settings;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.JsonException;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;
import static java.util.Collections.emptyMap;
import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;
import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * The default class to {@link ConfigurationReader}
 */
@ApplicationScoped
class DefaultConfigurationReader implements ConfigurationReader {

    private static final String META_INF = "META-INF/";

    private static final Jsonb JSONB = JsonbBuilder.create();

    @Inject
    private Reflections reflections;

    @Override
    public <T> ConfigurationSettingsUnit read(ConfigurationUnit annotation, Class<T> configurationClass)
            throws NullPointerException, ConfigurationException {

        requireNonNull(annotation, "annotation is required");
        requireNonNull(configurationClass, "configurationClass is required");


        InputStream stream = read(annotation);
        List<ConfigurationJson> configurations = getConfigurations(stream, annotation);
        ConfigurationJson configuration = getConfiguration(annotation, configurations);

        String name = configuration.getName();
        String description = configuration.getDescription();
        Map<String, Object> settings = new HashMap<>(ofNullable(configuration.getSettings()).orElse(emptyMap()));
        Class<?> provider = getProvider(configurationClass, configuration);

        return new DefaultConfigurationSettingsUnit(name, description, provider, Settings.of(settings));
    }

    private <T> Class<?> getProvider(Class<T> configurationClass, ConfigurationJson configuration) {
        try {
            Class<?> provider = Class.forName(configuration.getProvider());
            if (!configurationClass.isAssignableFrom(provider)) {
                throw new ConfigurationException(String.format("The class %s does not match with %s",
                        provider.toString(), configuration.toString()));
            }
            reflections.makeAccessible(provider);
            return provider;
        } catch (ClassNotFoundException e) {
            throw new ConfigurationException("An error to load the provider class: " + configuration.getProvider());
        } catch (ConstructorException exception) {
            throw new ConfigurationException("An error to load the provider class: " + configuration.getProvider(), exception);
        }
    }

    private ConfigurationJson getConfiguration(ConfigurationUnit annotation, List<ConfigurationJson> configurations) {


        String name = annotation.name();
        String fileName = annotation.fileName();
        if (isBlank(name)) {

            if (configurations.size() > 1) {
                throw new ConfigurationException(String.format("An ambitious error happened once the file %s has" +
                        " more than one configuration unit.", name));
            }

            return configurations.stream()
                    .findFirst()
                    .orElseThrow(() -> new ConfigurationException("There is not unit in the file: " + fileName));
        }
        return configurations.stream()
                .filter(c -> name.equals(c.getName()))
                .findFirst()
                .orElseThrow(() -> new ConfigurationException(format("The unit %s does not find in the file %s"
                        , name, fileName)));
    }

    private List<ConfigurationJson> getConfigurations(InputStream stream, ConfigurationUnit annotation) {
        try {
            return JSONB.fromJson(stream, new ArrayList<ConfigurationJson>() {
            }.getClass().getGenericSuperclass());
        } catch (JsonException exception) {
            throw new ConfigurationException("An error when read the JSON file: " + annotation.fileName()
                    , exception);
        }
    }

    private InputStream read(ConfigurationUnit annotation) {
        String fileName = META_INF + annotation.fileName();
        InputStream stream = DefaultConfigurationReader.class.getClassLoader().getResourceAsStream(fileName);
        return ofNullable(stream)
                .orElseThrow(() -> new ConfigurationException("The File does not found at: " + fileName));
    }
}
