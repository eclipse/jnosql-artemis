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
package org.jnosql.artemis.column.query;

import org.jnosql.artemis.CDIJUnitRunner;
import org.jnosql.artemis.Converters;
import org.jnosql.artemis.model.Money;
import org.jnosql.artemis.model.Person;
import org.jnosql.artemis.model.Worker;
import org.jnosql.artemis.reflection.ClassRepresentation;
import org.jnosql.artemis.reflection.ClassRepresentations;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.math.BigDecimal;

@RunWith(CDIJUnitRunner.class)
public class ConverterUtilTest {


    @Inject
    private Converters converters;

    @Inject
    private ClassRepresentations representations;

    @Test
    public void shouldNotConvert() {
        ClassRepresentation representation = representations.get(Person.class);
        Object value = 10_000L;
        Object id = ConverterUtil.getValue(value, representation, "id", converters);
        Assert.assertEquals(id, value);
    }

    @Test
    public void shouldConvert() {
        ClassRepresentation representation = representations.get(Person.class);
        String value = "100";
        Object id = ConverterUtil.getValue(value, representation, "id", converters);
        Assert.assertEquals(100L, id);
    }

    @Test
    public void shouldUseAttributeConvert() {
        ClassRepresentation representation = representations.get(Worker.class);
        Object value = new Money("BRL", BigDecimal.TEN);
        Object converted = ConverterUtil.getValue(value, representation, "salary", converters);
        Assert.assertEquals("BRL 10", converted);
    }

}