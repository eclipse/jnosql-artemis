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


import org.jnosql.artemis.Column;
import org.jnosql.artemis.Embeddable;
import org.jnosql.artemis.WeldJUnit4Runner;
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

@RunWith(WeldJUnit4Runner.class)
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
