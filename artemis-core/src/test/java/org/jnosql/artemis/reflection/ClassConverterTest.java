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
import org.jnosql.artemis.model.Animal;
import org.jnosql.artemis.model.Director;
import org.jnosql.artemis.model.Machine;
import org.jnosql.artemis.model.Person;
import org.jnosql.artemis.model.User;
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
        ClassRepresentation classRepresentation = classConverter.create(Director.class);
        boolean allMatch = classRepresentation.getFields().stream().allMatch(f -> !f.isId());
        assertTrue(allMatch);
    }


    @Test
    public void shouldReturnTrueWhenThereIsKey() {
        ClassRepresentation classRepresentation = classConverter.create(User.class);
        List<FieldRepresentation> fields = classRepresentation.getFields();

        Predicate<FieldRepresentation> hasKeyAnnotation = FieldRepresentation::isId;
        assertTrue(fields.stream().anyMatch(hasKeyAnnotation));
        FieldRepresentation fieldRepresentation = fields.stream().filter(hasKeyAnnotation).findFirst().get();
        assertEquals("nickname", fieldRepresentation.getName());
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