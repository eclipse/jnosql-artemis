/*
 * Copyright 2017 Otavio Santana and others
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

import java.lang.reflect.Field;
import javax.inject.Inject;
import org.jnosql.artemis.WeldJUnit4Runner;
import org.jnosql.artemis.model.Actor;
import org.jnosql.artemis.model.Movie;
import org.jnosql.artemis.model.Person;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(WeldJUnit4Runner.class)
public class ReflectionsTest {


    @Inject
    private Reflections reflections;

    @Test
    public void shouldReturnsEntityName() {
        Assert.assertEquals("Person", reflections.getEntityName(Person.class));
        Assert.assertEquals("movie", reflections.getEntityName(Movie.class));
    }

    @Test
    public void shouldListFields() {

        Assert.assertEquals(4, reflections.getFields(Person.class).size());
        Assert.assertEquals(6, reflections.getFields(Actor.class).size());

    }

    @Test
    public void shouldReturnColumnName() throws NoSuchFieldException {
        Field phones = Person.class.getDeclaredField("phones");
        Field id = Person.class.getDeclaredField("id");

        Assert.assertEquals("phones", reflections.getColumnName(phones));
        Assert.assertEquals("_id", reflections.getColumnName(id));
    }

}