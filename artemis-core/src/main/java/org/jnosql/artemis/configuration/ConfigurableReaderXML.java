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

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;

class ConfigurableReaderXML implements ConfigurableReader {

    private static final JAXBContext JAXB_CONTEX;

    private static final ThreadLocal<Unmarshaller> UNMARSHALLER;

    static {
        try {
            JAXB_CONTEX = JAXBContext.newInstance(ConfigurablesXML.class);
            UNMARSHALLER = ThreadLocal.withInitial(() -> {
                try {
                    return JAXB_CONTEX.createUnmarshaller();
                } catch (JAXBException e) {
                    throw new ConfigurationException("Error to load xml Unmarshaller context", e);
                }
            });
        } catch (JAXBException e) {
            throw new ConfigurationException("Error to load xml context", e);
        }

    }

    @Override
    public List<Configurable> read(InputStream stream, ConfigurationUnit annotation) throws NullPointerException, ConfigurationException {
        Unmarshaller unmarshaller = UNMARSHALLER.get();
        try {
            ConfigurablesXML configurablesXML = (ConfigurablesXML) unmarshaller.unmarshal(stream);
            List<Configurable> configurables = new ArrayList<>();
            Optional.ofNullable(configurablesXML.getConfigurations()).orElse(emptyList()).forEach(configurables::add);
            return configurables;
        } catch (JAXBException e) {
            throw new ConfigurationException("Error to read XML file:" + annotation.fileName(), e);
        }
    }
}
