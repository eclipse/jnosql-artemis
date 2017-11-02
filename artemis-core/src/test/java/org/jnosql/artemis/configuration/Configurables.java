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
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Configurables {


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

    @Test
    public void shouldGenerateXML() throws JAXBException {

        JAXBContext jaxbContext = JAXBContext.newInstance(ConfigurablesXML.class);
        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        ConfigurablesXML configurables = new ConfigurablesXML();

        ConfigurableXML configuration = new ConfigurableXML();
        configuration.setName("name");
        configuration.setDescription("that is the description");
        configuration.setProvider("class");
        Map<String, String> settings = new HashMap<>();
        settings.put("key", "value");
        settings.put("key2", "value2");
        configuration.setSettings(settings);

        configurables.setConfigurations(Collections.singletonList(configuration));

        jaxbMarshaller.marshal(configurables, System.out);


    }

}