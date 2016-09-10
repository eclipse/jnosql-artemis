/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jnosql.artemis.reflection;

import javax.inject.Inject;
import org.jnosql.artemis.WeldJUnit4Runner;
import org.jnosql.artemis.model.Actor;
import org.jnosql.artemis.model.Person;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

@RunWith(WeldJUnit4Runner.class)
public class ClassConverterTest {

    @Inject
    private ClassConverter classConverter;


    @Test
    public void shouldCreateClassRepresentation() {
        ClassRepresentation classRepresentation = classConverter.create(Person.class);

        assertEquals("Person", classRepresentation.getName());
        assertEquals(Person.class, classRepresentation.getClassInstance());
        assertEquals(4, classRepresentation.getFields().size());
        assertThat(classRepresentation.getFieldsName(), containsInAnyOrder("_id", "name", "age", "phones"));

    }

    @Test
    public void shouldCreateClassRepresentation2() {
        ClassRepresentation classRepresentation = classConverter.create(Actor.class);

        assertEquals("Actor", classRepresentation.getName());
        assertEquals(Actor.class, classRepresentation.getClassInstance());
        assertEquals(6, classRepresentation.getFields().size());
        assertThat(classRepresentation.getFieldsName(), containsInAnyOrder("_id", "name", "age", "phones", "movieCharacter", "movieRating"));

    }

}