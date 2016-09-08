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
        Assert.assertEquals(5, reflections.getFields(Actor.class).size());

    }

    @Test
    public void shouldReturnColumnName() throws NoSuchFieldException {
        Field phones = Person.class.getDeclaredField("phones");
        Field id = Person.class.getDeclaredField("id");

        Assert.assertEquals("phones", reflections.getColumnName(phones));
        Assert.assertEquals("_id", reflections.getColumnName(id));
    }

}