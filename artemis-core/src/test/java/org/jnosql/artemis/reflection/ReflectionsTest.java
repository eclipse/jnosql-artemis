package org.jnosql.artemis.reflection;

import javax.inject.Inject;
import org.jnosql.artemis.WeldJUnit4Runner;
import org.jnosql.artemis.model.Movie;
import org.jnosql.artemis.model.Person;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;


@RunWith(WeldJUnit4Runner.class)
public class ReflectionsTest {


    @Inject
    private Reflections reflections;

    @Test
    public void shouldReturnsEntityName() {
        Assert.assertEquals("Person", reflections.getEntityName(Person.class));
        Assert.assertEquals("movie", reflections.getEntityName(Movie.class));
    }

}