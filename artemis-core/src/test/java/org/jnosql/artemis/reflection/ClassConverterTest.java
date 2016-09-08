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
        assertThat(classRepresentation.getFieldsName(), containsInAnyOrder("_id", "name", "age", "phones", "movieCharacter", "movierRating"));

    }

}