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
package org.jnosql.artemis.column;

import org.jnosql.diana.api.column.ColumnEntity;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class ColumnEntityPostPersistTest {


    @Test(expected = NullPointerException.class)
    public void shouldReturnNPEWhenEntityIsNull() {
        ColumnEntityPostPersist.of(null);
    }

    @Test
    public void shouldReturnInstance() {
        ColumnEntity entity = ColumnEntity.of("columnFamily");
        ColumnEntityPostPersist postPersist = ColumnEntityPostPersist.of(entity);
        assertEquals(entity, postPersist.getEntity());
    }
}