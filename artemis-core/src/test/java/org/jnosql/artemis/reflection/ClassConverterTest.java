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

import org.jnosql.artemis.WeldJUnit4Runner;
import org.jnosql.artemis.model.Actor;
import org.jnosql.artemis.model.Animal;
import org.jnosql.artemis.model.Director;
import org.jnosql.artemis.model.Machine;
import org.jnosql.artemis.model.Person;
import org.jnosql.artemis.model.User;
import org.jnosql.artemis.model.Worker;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.util.List;
import java.util.function.Predicate;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.jnosql.artemis.reflection.FieldType.DEFAULT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

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

    @Test
    public void shouldCreateClassRepresentationWithEmbeddedClass() {
        ClassRepresentation classRepresentation = classConverter.create(Director.class);
        assertEquals("Director", classRepresentation.getName());
        assertEquals(Director.class, classRepresentation.getClassInstance());
        assertEquals(5, classRepresentation.getFields().size());
        assertThat(classRepresentation.getFieldsName(), containsInAnyOrder("_id", "name", "age", "phones", "movie"));

    }

    @Test
    public void shouldReturnFalseWhenThereIsNotKey(){
        ClassRepresentation classRepresentation = classConverter.create(Worker.class);
        boolean allMatch = classRepresentation.getFields().stream().noneMatch(FieldRepresentation::isId);
        assertTrue(allMatch);
    }


    @Test
    public void shouldReturnTrueWhenThereIsKey() {
        ClassRepresentation classRepresentation = classConverter.create(User.class);
        List<FieldRepresentation> fields = classRepresentation.getFields();

        Predicate<FieldRepresentation> hasKeyAnnotation = FieldRepresentation::isId;
        assertTrue(fields.stream().anyMatch(hasKeyAnnotation));
        FieldRepresentation fieldRepresentation = fields.stream().filter(hasKeyAnnotation).findFirst().get();
        assertEquals("_id", fieldRepresentation.getName());
        assertEquals(DEFAULT, fieldRepresentation.getType());

    }

    @Test(expected = ConstructorException.class)
    public void shouldReturnErrorWhenThereIsNotConstructor() {
        classConverter.create(Animal.class);
    }

    @Test
    public void shouldReturnWhenIsDefaultConstructor() {
        ClassRepresentation classRepresentation = classConverter.create(Machine.class);
        List<FieldRepresentation> fields = classRepresentation.getFields();
        assertEquals(1, fields.size());
    }

}