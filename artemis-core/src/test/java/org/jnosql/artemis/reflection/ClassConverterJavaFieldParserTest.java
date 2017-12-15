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

import org.jnosql.artemis.CDIJUnitRunner;
import org.jnosql.artemis.model.Address;
import org.jnosql.artemis.model.Person;
import org.jnosql.artemis.model.Worker;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;

@RunWith(CDIJUnitRunner.class)
public class ClassConverterJavaFieldParserTest {

    @Inject
    private ClassConverter classConverter;

    @Test(expected = NullPointerException.class)
    public void shouldReturnErrorWhenParameterIsNull() {
        ClassRepresentation classRepresentation = classConverter.create(Person.class);
        classRepresentation.getColumnField(null);
    }

    @Test
    public void shouldReturnTheNativeName() {
        ClassRepresentation classRepresentation = classConverter.create(Worker.class);
        String notFound = classRepresentation.getColumnField("salary");
        assertEquals("money", notFound);
    }

    @Test
    public void shouldReturnTheSameValueWhenTheFieldDoesNotExistInTheClassRepresentation() {
        ClassRepresentation classRepresentation = classConverter.create(Person.class);
        String notFound = classRepresentation.getColumnField("notFound");
        assertEquals("notFound", notFound);
    }

    @Test
    public void shouldReadFieldWhenFieldIsSubEntity() {
        ClassRepresentation classRepresentation = classConverter.create(Address.class);
        String result = classRepresentation.getColumnField("zipcode.plusFour");
        assertEquals("plusFour", result);
    }

    @Test
    public void shouldReturnAllFieldWhenSelectTheSubEntityField() {
        ClassRepresentation classRepresentation = classConverter.create(Address.class);
        String result = classRepresentation.getColumnField("zipcode");
        List<String> resultList = Stream.of(result.split(",")).sorted().collect(toList());
        List<String> expected = Stream.of("plusFour", "zip").sorted().collect(toList());
        assertEquals(expected, resultList);
    }

    @Test
    public void shouldReadFieldWhenFieldIsEmbedded() {
        ClassRepresentation classRepresentation = classConverter.create(Worker.class);
        String result = classRepresentation.getColumnField("job.city");
        assertEquals("job.city", result);
    }

    @Test
    public void shouldReturnAllFieldWhenSelectTheEmbeddedField() {
        ClassRepresentation classRepresentation = classConverter.create(Worker.class);
        String result = classRepresentation.getColumnField("job");
        List<String> resultList = Stream.of(result.split(",")).sorted().collect(toList());
        List<String> expected = Stream.of("job.description", "job.city").sorted().collect(toList());
        assertEquals(expected, resultList);
    }

}
