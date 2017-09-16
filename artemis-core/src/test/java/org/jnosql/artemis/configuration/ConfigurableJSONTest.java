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

import org.junit.Test;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import java.util.Arrays;
import java.util.Collections;

public class ConfigurableJSONTest {


    @Test
    public void shouldGenerateJSon() {

        Jsonb jsonb = JsonbBuilder.create();

        ConfigurableJSON configuration = new ConfigurableJSON();
        configuration.setName("name");
        configuration.setDescription("that is the description");
        configuration.setProvider("class");
        configuration.setSettings(Collections.singletonMap("key", "value"));
        String json = jsonb.toJson(Arrays.asList(configuration, configuration));

        System.out.println(json);

    }

}