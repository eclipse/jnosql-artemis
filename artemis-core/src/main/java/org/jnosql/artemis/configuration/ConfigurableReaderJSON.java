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

import javax.inject.Named;
import javax.json.JsonException;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * The {@link ConfigurableReader} to JSON
 */
@Named("json")
class ConfigurableReaderJSON implements ConfigurableReader {

    private static final Jsonb JSONB = JsonbBuilder.create();

    @Override
    public List<Configurable> read(InputStream stream, ConfigurationUnit annotation) throws NullPointerException, ConfigurationException {
        try {
            return JSONB.fromJson(stream, new ArrayList<ConfigurableJSON>() {
            }.getClass().getGenericSuperclass());
        } catch (JsonException exception) {
            throw new ConfigurationException("An error when read the JSON file: " + annotation.fileName()
                    , exception);
        }
    }
}
