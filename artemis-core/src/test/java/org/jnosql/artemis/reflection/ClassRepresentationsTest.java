package org.jnosql.artemis.reflection;

import javax.inject.Inject;
import org.jnosql.artemis.WeldJUnit4Runner;
import org.jnosql.artemis.model.Person;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

@RunWith(WeldJUnit4Runner.class)
public class ClassRepresentationsTest {

    @Inject
    private ClassRepresentations classRepresentations;


    @Test
    public void shouldCreateClassRepresentation() {
        ClassRepresentation classRepresentation = classRepresentations.create(Person.class);

        Assert.assertEquals("Person", classRepresentation.getName());
        Assert.assertEquals(Person.class, classRepresentation.getClassInstance());

    }

}