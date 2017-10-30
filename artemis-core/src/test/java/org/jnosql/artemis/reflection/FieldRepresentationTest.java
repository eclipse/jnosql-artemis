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


import org.jnosql.artemis.Column;
import org.jnosql.artemis.Embeddable;
import org.jnosql.artemis.CDIJUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

import static org.jnosql.artemis.reflection.FieldType.COLLECTION;
import static org.jnosql.artemis.reflection.FieldType.DEFAULT;
import static org.jnosql.artemis.reflection.FieldType.EMBEDDED;
import static org.jnosql.artemis.reflection.FieldType.MAP;
import static org.junit.Assert.assertEquals;

@RunWith(CDIJUnitRunner.class)
public class FieldRepresentationTest {


    @Inject
    private ClassConverter classConverter;

    @Test
    public void shouldReadDefaultField() {
        ClassRepresentation classRepresentation = classConverter.create(ForClass.class);
        List<FieldRepresentation> fields = classRepresentation.getFields();

        FieldRepresentation field = fields.stream()
                .filter(f -> "string".equals(f.getFieldName())).findFirst().get();

        assertEquals("string", field.getFieldName());
        assertEquals("stringTypeAnnotation", field.getName());
        assertEquals(DEFAULT, field.getType());

    }

    @Test
    public void shouldReadCollectionField() {
        ClassRepresentation classRepresentation = classConverter.create(ForClass.class);
        List<FieldRepresentation> fields = classRepresentation.getFields();
        FieldRepresentation field = fields.stream()
                .filter(f -> "list".equals(f.getFieldName())).findFirst().get();

        assertEquals("list", field.getFieldName());
        assertEquals("listAnnotation", field.getName());
        assertEquals(COLLECTION, field.getType());
    }

    @Test
    public void shouldReadMapField() {
        ClassRepresentation classRepresentation = classConverter.create(ForClass.class);
        List<FieldRepresentation> fields = classRepresentation.getFields();
        FieldRepresentation field = fields.stream()
                .filter(f -> "map".equals(f.getFieldName())).findFirst().get();

        assertEquals("map", field.getFieldName());
        assertEquals("mapAnnotation", field.getName());
        assertEquals(MAP, field.getType());

    }

    @Test
    public void shouldReadEmbeddableField() {
        ClassRepresentation classRepresentation = classConverter.create(ForClass.class);
        List<FieldRepresentation> fields = classRepresentation.getFields();
        FieldRepresentation field = fields.stream()
                .filter(f -> "barClass".equals(f.getFieldName())).findFirst().get();

        assertEquals("barClass", field.getFieldName());
        assertEquals("barClass", field.getName());
        assertEquals(EMBEDDED, field.getType());
    }


    public static class ForClass {

        @Column("stringTypeAnnotation")
        private String string;

        @Column("listAnnotation")
        private List<String> list;

        @Column("mapAnnotation")
        private Map<String, String> map;


        @Column
        private BarClass barClass;
    }

    @Embeddable
    public static class BarClass {

        @Column("integerAnnotation")
        private Integer integer;
    }

}
