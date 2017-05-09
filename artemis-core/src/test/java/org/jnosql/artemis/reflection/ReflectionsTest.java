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
package org.jnosql.artemis.reflection;

import org.jnosql.artemis.WeldJUnit4Runner;
import org.jnosql.artemis.model.Actor;
import org.jnosql.artemis.model.Movie;
import org.jnosql.artemis.model.Person;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.lang.reflect.Field;

import static org.junit.Assert.assertEquals;


@RunWith(WeldJUnit4Runner.class)
public class ReflectionsTest {


    @Inject
    private Reflections reflections;

    @Test
    public void shouldReturnsEntityName() {
        assertEquals("Person", reflections.getEntityName(Person.class));
        assertEquals("movie", reflections.getEntityName(Movie.class));
    }

    @Test
    public void shouldListFields() {

        assertEquals(4, reflections.getFields(Person.class).size());
        assertEquals(6, reflections.getFields(Actor.class).size());

    }

    @Test
    public void shouldReturnColumnName() throws NoSuchFieldException {
        Field phones = Person.class.getDeclaredField("phones");
        Field id = Person.class.getDeclaredField("id");

        assertEquals("phones", reflections.getColumnName(phones));
        assertEquals("id", reflections.getColumnName(id));
        assertEquals("_id", reflections.getIdName(id));
    }

}