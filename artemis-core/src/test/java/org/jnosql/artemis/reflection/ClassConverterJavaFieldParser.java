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
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;

import static org.junit.Assert.assertEquals;

@RunWith(CDIJUnitRunner.class)
public class ClassConverterJavaFieldParser {

    @Inject
    private ClassConverter classConverter;

    @Test(expected = NullPointerException.class)
    public void shouldReturnErrorWhenParameterIsNull() {
        ClassRepresentation classRepresentation = classConverter.create(Person.class);
        classRepresentation.getColumnField(null);
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

    //should test simple field
    //should return
    //should test
}
