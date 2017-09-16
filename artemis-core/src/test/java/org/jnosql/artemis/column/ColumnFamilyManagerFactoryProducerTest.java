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
package org.jnosql.artemis.column;

import org.jnosql.artemis.ConfigurationUnit;
import org.jnosql.artemis.WeldJUnit4Runner;
import org.jnosql.diana.api.Settings;
import org.jnosql.diana.api.column.ColumnFamilyManagerAsyncFactory;
import org.jnosql.diana.api.column.ColumnFamilyManagerFactory;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

@RunWith(WeldJUnit4Runner.class)
public class ColumnFamilyManagerFactoryProducerTest {


    @Inject
    @ConfigurationUnit(fileName = "column.json", name = "name")
    private ColumnFamilyManagerFactory<?> factoryA;

    @Inject
    @ConfigurationUnit(fileName = "column.json", name = "name-2")
    private ColumnFamilyManagerFactory factoryB;


    @Inject
    @ConfigurationUnit(fileName = "column.json", name = "name")
    private ColumnFamilyManagerAsyncFactory<?> factoryAsyncA;

    @Inject
    @ConfigurationUnit(fileName = "column.json", name = "name-2")
    private ColumnFamilyManagerAsyncFactory factoryAsyncB;


    @Test
    public void shouldReadInjectColumnFamily() {
        factoryA.get("database");
        Assert.assertTrue(ColumnFamilyManagerMock.MockFamilyManager.class.isInstance(factoryA));
        ColumnFamilyManagerMock.MockFamilyManager mock = ColumnFamilyManagerMock.MockFamilyManager.class.cast(factoryA);
        Map<String, Object> settings = new HashMap<>();
        settings.put("key","value");
        settings.put("key2","value2");
        assertEquals(Settings.of(settings), mock.getSettings());
    }

    @Test
    public void shouldReadInjectColumnFamilyB() {
        factoryB.get("database");
        Assert.assertTrue(ColumnFamilyManagerMock.MockFamilyManager.class.isInstance(factoryB));
        ColumnFamilyManagerMock.MockFamilyManager mock = ColumnFamilyManagerMock.MockFamilyManager.class.cast(factoryB);
        Map<String, Object> settings = new HashMap<>();
        settings.put("key","value");
        settings.put("key2","value2");
        settings.put("key3","value3");
        assertEquals(Settings.of(settings), mock.getSettings());
    }

    @Test
    public void shouldReadInjectColumnFamilyAsync() {
        factoryAsyncA.getAsync("database");
        Assert.assertTrue(ColumnFamilyManagerMock.MockFamilyManager.class.isInstance(factoryAsyncA));
        ColumnFamilyManagerMock.MockFamilyManager mock = ColumnFamilyManagerMock.MockFamilyManager.class.cast(factoryAsyncA);
        Map<String, Object> settings = new HashMap<>();
        settings.put("key","value");
        settings.put("key2","value2");
        assertEquals(Settings.of(settings), mock.getSettings());
    }

    @Test
    public void shouldReadInjectColumnFamilyBAsync() {
        factoryAsyncB.getAsync("database");
        Assert.assertTrue(ColumnFamilyManagerMock.MockFamilyManager.class.isInstance(factoryAsyncB));
        ColumnFamilyManagerMock.MockFamilyManager mock = ColumnFamilyManagerMock.MockFamilyManager.class.cast(factoryAsyncB);
        Map<String, Object> settings = new HashMap<>();
        settings.put("key","value");
        settings.put("key2","value2");
        settings.put("key3","value3");
        assertEquals(Settings.of(settings), mock.getSettings());
    }

}