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

import org.jnosql.artemis.CDIExtension;
import org.jnosql.artemis.ConfigurationReader;
import org.jnosql.artemis.ConfigurationSettingsUnit;
import org.jnosql.artemis.ConfigurationUnit;
import org.jnosql.diana.api.Settings;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(CDIExtension.class)
public class DefaultConfigurationReaderTest {


    @Inject
    private ConfigurationReader configurationReader;


    @Test
    public void shouldReturnNPEWhenAnnotationIsNull() {
        Assertions.assertThrows(NullPointerException.class, () -> configurationReader.read(null, MockConfiguration.class));
    }

    @Test
    public void shouldReturnNPEWhenConfigurationIsNull() {
        Assertions.assertThrows(NullPointerException.class, () -> {
            ConfigurationUnit annotation = mock(ConfigurationUnit.class);
            configurationReader.read(annotation, null);
        });
    }

    @Test
    public void shouldReturnErrorWhenFileDoesNotExist() {
        Assertions.assertThrows(ConfigurationException.class, () -> {
            ConfigurationUnit annotation = mock(ConfigurationUnit.class);
            when(annotation.fileName()).thenReturn("unknown.json");
            configurationReader.read(annotation, MockConfiguration.class);
        });
    }

    @Test
    public void shouldReturnErrorWhenFileIsInvalid() {
        Assertions.assertThrows(ConfigurationException.class, () -> {
            ConfigurationUnit annotation = mock(ConfigurationUnit.class);
            when(annotation.fileName()).thenReturn("invalid.json");
            configurationReader.read(annotation, MockConfiguration.class);
        });
    }

    @Test
    public void shouldReturnAnErrorWhenTheExtensionDoesNotSupport() {
        Assertions.assertThrows(ConfigurationException.class, () -> {
            ConfigurationUnit annotation = mock(ConfigurationUnit.class);
            when(annotation.fileName()).thenReturn("invalid.invalid");
            configurationReader.read(annotation, MockConfiguration.class);
        });
    }

    @Test
    public void shouldReturnAnErrorWhenTheFileIsInvalid() {
        Assertions.assertThrows(ConfigurationException.class, () -> {
            ConfigurationUnit annotation = mock(ConfigurationUnit.class);
            when(annotation.fileName()).thenReturn("invalid");
            configurationReader.read(annotation, MockConfiguration.class);
        });
    }


    @Test
    public void shouldReturnErrorWhenUnitNameIsNotFind() {
        Assertions.assertThrows(ConfigurationException.class, () -> {
            ConfigurationUnit annotation = mock(ConfigurationUnit.class);
            when(annotation.fileName()).thenReturn("jnosql.json");
            when(annotation.name()).thenReturn("unknown");
            configurationReader.read(annotation, MockConfiguration.class);
        });
    }

    @Test
    public void shouldReturnErrorWhenClassIsNotFound() {
        Assertions.assertThrows(ConfigurationException.class, () -> {
            ConfigurationUnit annotation = mock(ConfigurationUnit.class);
            when(annotation.fileName()).thenReturn("invalid-class.json");
            when(annotation.name()).thenReturn("name-1");
            configurationReader.read(annotation, MockConfiguration.class);
        });
    }

    @Test
    public void shouldReturnErrorWhenClassDoesNotMatch() {
        Assertions.assertThrows(ConfigurationException.class, () -> {
            ConfigurationUnit annotation = mock(ConfigurationUnit.class);
            when(annotation.fileName()).thenReturn("invalid-class.json");
            when(annotation.name()).thenReturn("name");
            configurationReader.read(annotation, MockConfiguration.class);
        });
    }


    @Test
    public void shouldReturnErrorWhenThereIsNotDefaultConstructor() {
        Assertions.assertThrows(ConfigurationException.class, () -> {
            ConfigurationUnit annotation = mock(ConfigurationUnit.class);
            when(annotation.fileName()).thenReturn("invalid-class.json");
            when(annotation.name()).thenReturn("name-2");
            configurationReader.read(annotation, MockConfiguration.class);
        });
    }

    @Test
    public void shouldReturnErrorWhenThereIsAmbiguous() {
        Assertions.assertThrows(ConfigurationException.class, () -> {
            ConfigurationUnit annotation = mock(ConfigurationUnit.class);
            when(annotation.fileName()).thenReturn("jnosql.json");
            configurationReader.read(annotation, MockConfiguration.class);
        });
    }

    @Test
    public void shouldReadConfiguration() {
        ConfigurationUnit annotation = mock(ConfigurationUnit.class);
        when(annotation.fileName()).thenReturn("jnosql.json");
        when(annotation.name()).thenReturn("name");
        ConfigurationSettingsUnit unit = configurationReader.read(annotation, MockConfiguration.class);

        Map<String, Object> settings = new HashMap<>();
        settings.put("key","value");
        settings.put("key2","value2");

        assertEquals("name", unit.getName().get());
        assertEquals("that is the description", unit.getDescription().get());
        assertEquals(Settings.of(settings), unit.getSettings());
        assertEquals(DefaultMockConfiguration.class, unit.getProvider().get());
    }

    @Test
    public void shouldReadDefaultFile() {
        ConfigurationUnit annotation = mock(ConfigurationUnit.class);
        when(annotation.name()).thenReturn("name-2");
        when(annotation.fileName()).thenReturn("jnosql.json");
        ConfigurationSettingsUnit unit = configurationReader.read(annotation, MockConfiguration.class);

        Map<String, Object> settings = new HashMap<>();
        settings.put("key","value");
        settings.put("key2","value2");
        settings.put("key3","value3");

        assertEquals("name-2", unit.getName().get());
        assertEquals("that is the description", unit.getDescription().get());
        assertEquals(Settings.of(settings), unit.getSettings());
        assertEquals(DefaultMockConfiguration.class, unit.getProvider().get());
    }


    @Test
    public void shouldReadDefaultNameXML() {
        ConfigurationUnit annotation = mock(ConfigurationUnit.class);
        when(annotation.fileName()).thenReturn("jnosql.xml");
        when(annotation.name()).thenReturn("name");
        ConfigurationSettingsUnit unit = configurationReader.read(annotation, MockConfiguration.class);

        Map<String, Object> settings = new HashMap<>();
        settings.put("key","value");
        settings.put("key2","value2");

        assertEquals("name", unit.getName().get());
        assertEquals("that is the description", unit.getDescription().get());
        assertEquals(Settings.of(settings), unit.getSettings());
        assertEquals(DefaultMockConfiguration.class, unit.getProvider().get());
    }

    @Test
    public void shouldReadAnnotationDefaultFile() {
        ConfigurationUnit annotation = mock(ConfigurationUnit.class);
        when(annotation.name()).thenReturn("name-2");
        when(annotation.fileName()).thenReturn("jnosql.json");
        ConfigurationSettingsUnit unit = configurationReader.read(annotation);

        Map<String, Object> settings = new HashMap<>();
        settings.put("key","value");
        settings.put("key2","value2");
        settings.put("key3","value3");

        assertEquals("name-2", unit.getName().get());
        assertEquals("that is the description", unit.getDescription().get());
        assertEquals(Settings.of(settings), unit.getSettings());
        assertFalse(unit.getProvider().isPresent());
    }


    @Test
    public void shouldReadDefaultAnnotationNameXML() {
        ConfigurationUnit annotation = mock(ConfigurationUnit.class);
        when(annotation.fileName()).thenReturn("jnosql.xml");
        when(annotation.name()).thenReturn("name");
        ConfigurationSettingsUnit unit = configurationReader.read(annotation);

        Map<String, Object> settings = new HashMap<>();
        settings.put("key","value");
        settings.put("key2","value2");

        assertEquals("name", unit.getName().get());
        assertEquals("that is the description", unit.getDescription().get());
        assertEquals(Settings.of(settings), unit.getSettings());
        assertFalse(unit.getProvider().isPresent());
    }

}