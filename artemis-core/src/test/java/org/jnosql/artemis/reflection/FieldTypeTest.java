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
package org.jnosql.artemis.reflection;

import org.jnosql.artemis.model.Actor;
import org.jnosql.artemis.model.Address;
import org.jnosql.artemis.model.Director;
import org.jnosql.artemis.model.Movie;
import org.jnosql.artemis.model.Person;
import org.jnosql.artemis.model.Worker;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Field;


public class FieldTypeTest {


    @Test
    public void shouldReturnList() throws NoSuchFieldException {
        Field field = Person.class.getDeclaredField("phones");
        Assert.assertEquals(FieldType.COLLECTION, FieldType.of(field));
    }

    @Test
    public void shouldReturnSet() throws NoSuchFieldException {
        Field field = Movie.class.getDeclaredField("actors");
        Assert.assertEquals(FieldType.COLLECTION, FieldType.of(field));
    }

    @Test
    public void shouldReturnMap() throws NoSuchFieldException {
        Field field = Actor.class.getDeclaredField("movieCharacter");
        Assert.assertEquals(FieldType.MAP, FieldType.of(field));
    }

    @Test
    public void shouldReturnDefault() throws NoSuchFieldException {
        Field field = Person.class.getDeclaredField("name");
        Assert.assertEquals(FieldType.DEFAULT, FieldType.of(field));
    }


    @Test
    public void shouldReturnEmbeddedAsEntity() throws NoSuchFieldException{
        Field field = Director.class.getDeclaredField("movie");
        Assert.assertEquals(FieldType.EMBEDDED, FieldType.of(field));
    }


    @Test
    public void shouldReturnEmbedded() throws NoSuchFieldException{
        Field field = Worker.class.getDeclaredField("job");
        Assert.assertEquals(FieldType.EMBEDDED, FieldType.of(field));
    }

    @Test
    public void shouldReturnSubEntity() throws NoSuchFieldException{
        Field field = Address.class.getDeclaredField("zipcode");
        Assert.assertEquals(FieldType.SUBENTITY, FieldType.of(field));
    }

}