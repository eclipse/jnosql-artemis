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
import org.jnosql.artemis.WeldJUnit4Runner;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(WeldJUnit4Runner.class)
public class DefaultConfigurationReaderTest {


    @Inject
    private ConfigurationReader configurationReader;


    @Test(expected = NullPointerException.class)
    public void shouldReturnNPEWhenAnnotationIsNull() {
        configurationReader.read(null, MockConfiguration.class);
    }

    @Test(expected = NullPointerException.class)
    public void shouldReturnNPEWhenConfigurationIsNull() {
        ConfigurationUnit annotation = mock(ConfigurationUnit.class);
        configurationReader.read(annotation, null);
    }

    @Test(expected = ConfigurationException.class)
    public void shouldReturnErrorWhenFileDoesNotExist() {
        ConfigurationUnit annotation = mock(ConfigurationUnit.class);
        when(annotation.fileName()).thenReturn("unknown.json");
        configurationReader.read(annotation, MockConfiguration.class);
    }

    @Test(expected = ConfigurationException.class)
    public void shouldReturnErrorWhenFileIsInvalid() {
        ConfigurationUnit annotation = mock(ConfigurationUnit.class);
        when(annotation.fileName()).thenReturn("invalid.json");
        configurationReader.read(annotation, MockConfiguration.class);
    }


    @Test(expected = ConfigurationException.class)
    public void shouldReturnErrorWhenUnitNameIsNotFind() {
        ConfigurationUnit annotation = mock(ConfigurationUnit.class);
        when(annotation.fileName()).thenReturn("jnosql.json");
        when(annotation.name()).thenReturn("unknown");
        configurationReader.read(annotation, MockConfiguration.class);
    }

    @Test(expected = ConfigurationException.class)
    public void shouldReturnErrorWhenClassIsNotFound() {
        ConfigurationUnit annotation = mock(ConfigurationUnit.class);
        when(annotation.fileName()).thenReturn("invalid-class.json");
        when(annotation.name()).thenReturn("name-1");
        configurationReader.read(annotation, MockConfiguration.class);
    }

    @Test(expected = ConfigurationException.class)
    public void shouldReturnErrorWhenClassDoesNotMatch() {
        ConfigurationUnit annotation = mock(ConfigurationUnit.class);
        when(annotation.fileName()).thenReturn("invalid-class.json");
        when(annotation.name()).thenReturn("name");
        configurationReader.read(annotation, MockConfiguration.class);
    }


    @Test
    public void shouldReturnErrorWhenThereIsNotDefaultConstructor() {

    }

    //should match when there is just one file and not name
    //should match when the configuration file does not have
    //should find when there is not than one file
    //when the unit is not found in the file
    //when the class does not match
    //when there is not default constructor
    //when there is an ambiguous exception
}