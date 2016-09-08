package org.jnosql.artemis.reflection;

import java.lang.reflect.Field;
import org.jnosql.artemis.model.Actor;
import org.jnosql.artemis.model.Movie;
import org.jnosql.artemis.model.Person;
import org.junit.Assert;
import org.junit.Test;


public class FieldTypeTest {


    @Test
    public void shouldReturnList() throws NoSuchFieldException {
        Field field = Person.class.getDeclaredField("phones");
        Assert.assertEquals(FieldType.LIST, FieldType.of(field));
    }

    @Test
    public void shouldReturnSet() throws NoSuchFieldException {
        Field field = Movie.class.getDeclaredField("actors");
        Assert.assertEquals(FieldType.SET, FieldType.of(field));
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

}