/*
 * Copyright 2017 Otavio Santana and others
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jnosql.artemis.column.spi;

import org.jnosql.artemis.Database;
import org.jnosql.artemis.DatabaseType;
import org.jnosql.artemis.WeldJUnit4Runner;
import org.jnosql.artemis.column.ColumnRepository;
import org.jnosql.artemis.column.ColumnRepositoryAsync;
import org.jnosql.artemis.model.Person;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;

import static org.junit.Assert.assertEquals;

@RunWith(WeldJUnit4Runner.class)
public class ColumnFamilyProducerExtensionTest {

    @Inject
    @Database(value = DatabaseType.COLUMN, provider = "columnRepositoryMock")
    private ColumnRepository managerMock;

    @Inject
    private ColumnRepository manager;

    @Inject
    @Database(value = DatabaseType.COLUMN, provider = "columnRepositoryMock")
    private ColumnRepositoryAsync managerMockAsync;

    @Inject
    private ColumnRepositoryAsync managerAsync;

    @Test
    public void shouldInstance() {
        Assert.assertNotNull(manager);
        Assert.assertNotNull(managerMock);
    }

    @Test
    public void shouldSave() {
        Person person = manager.save(Person.builder().build());
        Person personMock = managerMock.save(Person.builder().build());

        assertEquals("Default", person.getName());
        assertEquals("columnRepositoryMock", personMock.getName());
    }

    @Test
    public void shouldSaveAsync() {
        managerAsync.save(Person.builder().build());
        managerMockAsync.save(Person.builder().build());
    }
}