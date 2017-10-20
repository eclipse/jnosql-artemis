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
package org.jnosql.artemis.column;

import org.hamcrest.Matchers;
import org.jnosql.artemis.WeldJUnit4Runner;
import org.jnosql.artemis.model.Actor;
import org.jnosql.artemis.model.Contact;
import org.jnosql.artemis.model.ContactType;
import org.jnosql.artemis.model.AppointmentBook;
import org.jnosql.artemis.model.Director;
import org.jnosql.artemis.model.Job;
import org.jnosql.artemis.model.Money;
import org.jnosql.artemis.model.Movie;
import org.jnosql.artemis.model.Person;
import org.jnosql.artemis.model.Worker;
import org.jnosql.artemis.reflection.ClassRepresentations;
import org.jnosql.diana.api.TypeReference;
import org.jnosql.diana.api.Value;
import org.jnosql.diana.api.column.Column;
import org.jnosql.diana.api.column.ColumnEntity;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.Collections.singleton;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

@RunWith(WeldJUnit4Runner.class)
public class DefaultColumnEntityConverterTest {


    @Inject
    private DefaultColumnEntityConverter converter;

    @Inject
    private ClassRepresentations classRepresentations;

    private Column[] columns;

    private Actor actor = Actor.actorBuilder().withAge()
            .withId()
            .withName()
            .withPhones(Arrays.asList("234", "2342"))
            .withMovieCharacter(Collections.singletonMap("JavaZone", "Jedi"))
            .withMovierRating(Collections.singletonMap("JavaZone", 10))
            .build();

    @Before
    public void init() {

        columns = new Column[]{Column.of("_id", 12L),
                Column.of("age", 10), Column.of("name", "Otavio"),
                Column.of("phones", Arrays.asList("234", "2342"))
                , Column.of("movieCharacter", Collections.singletonMap("JavaZone", "Jedi"))
                , Column.of("movieRating", Collections.singletonMap("JavaZone", 10))};
    }

    @Test
    public void shouldConvertPersonToDocument() {

        Person person = Person.builder().withAge()
                .withId(12)
                .withName("Otavio")
                .withPhones(Arrays.asList("234", "2342")).build();

        ColumnEntity entity = converter.toColumn(person);
        assertEquals("Person", entity.getName());
        assertEquals(4, entity.size());
        /*Assert.assertThat(entity.getColumns(), containsInAnyOrder(Document.of("_id", 12L),
                Document.of("age", 10), Document.of("name", "Otavio"), Document.of("phones", Arrays.asList("234", "2342"))));*/

    }

    @Test
    public void shouldConvertActorToDocument() {


        ColumnEntity entity = converter.toColumn(actor);
        assertEquals("Actor", entity.getName());
        assertEquals(6, entity.size());


        assertThat(entity.getColumns(), containsInAnyOrder(columns));
    }

    @Test
    public void shouldConvertDocumentToActor() {
        ColumnEntity entity = ColumnEntity.of("Actor");
        Stream.of(columns).forEach(entity::add);

        Actor actor = converter.toEntity(Actor.class, entity);
        assertNotNull(actor);
        assertEquals(10, actor.getAge());
        assertEquals(12L, actor.getId());
        assertEquals(Arrays.asList("234", "2342"), actor.getPhones());
        assertEquals(Collections.singletonMap("JavaZone", "Jedi"), actor.getMovieCharacter());
        assertEquals(Collections.singletonMap("JavaZone", 10), actor.getMovieRating());
    }

    @Test
    public void shouldConvertDocumentToActorFromEntity() {
        ColumnEntity entity = ColumnEntity.of("Actor");
        Stream.of(columns).forEach(entity::add);

        Actor actor = converter.toEntity(entity);
        assertNotNull(actor);
        assertEquals(10, actor.getAge());
        assertEquals(12L, actor.getId());
        assertEquals(Arrays.asList("234", "2342"), actor.getPhones());
        assertEquals(Collections.singletonMap("JavaZone", "Jedi"), actor.getMovieCharacter());
        assertEquals(Collections.singletonMap("JavaZone", 10), actor.getMovieRating());
    }


    @Test
    public void shouldConvertDirectorToColumn() {

        Movie movie = new Movie("Matrix", 2012, Collections.singleton("Actor"));
        Director director = Director.builderDiretor().withAge(12)
                .withId(12)
                .withName("Otavio")
                .withPhones(Arrays.asList("234", "2342")).withMovie(movie).build();

        ColumnEntity entity = converter.toColumn(director);
        assertEquals(5, entity.size());

        assertEquals(getValue(entity.find("name")), director.getName());
        assertEquals(getValue(entity.find("age")), director.getAge());
        assertEquals(getValue(entity.find("_id")), director.getId());
        assertEquals(getValue(entity.find("phones")), director.getPhones());


        Column subColumn = entity.find("movie").get();
        List<Column> columns = subColumn.get(new TypeReference<List<Column>>() {
        });

        assertEquals(3, columns.size());
        assertEquals("movie", subColumn.getName());
        assertEquals(movie.getTitle(), columns.stream().filter(c -> "title".equals(c.getName())).findFirst().get().get());
        assertEquals(movie.getYear(), columns.stream().filter(c -> "year".equals(c.getName())).findFirst().get().get());
        assertEquals(movie.getActors(), columns.stream().filter(c -> "actors".equals(c.getName())).findFirst().get().get());


    }

    @Test
    public void shouldConvertToEmbeddedClassWhenHasSubColumn() {
        Movie movie = new Movie("Matrix", 2012, Collections.singleton("Actor"));
        Director director = Director.builderDiretor().withAge(12)
                .withId(12)
                .withName("Otavio")
                .withPhones(Arrays.asList("234", "2342")).withMovie(movie).build();

        ColumnEntity entity = converter.toColumn(director);
        Director director1 = converter.toEntity(entity);

        assertEquals(movie, director1.getMovie());
        assertEquals(director.getName(), director1.getName());
        assertEquals(director.getAge(), director1.getAge());
        assertEquals(director.getId(), director1.getId());
    }

    @Test
    public void shouldConvertToEmbeddedClassWhenHasSubColumn2() {
        Movie movie = new Movie("Matrix", 2012, singleton("Actor"));
        Director director = Director.builderDiretor().withAge(12)
                .withId(12)
                .withName("Otavio")
                .withPhones(Arrays.asList("234", "2342")).withMovie(movie).build();

        ColumnEntity entity = converter.toColumn(director);
        entity.remove("movie");
        entity.add(Column.of("title", "Matrix"));
        entity.add(Column.of("year", 2012));
        entity.add(Column.of("actors", singleton("Actor")));
        Director director1 = converter.toEntity(entity);

        assertEquals(movie, director1.getMovie());
        assertEquals(director.getName(), director1.getName());
        assertEquals(director.getAge(), director1.getAge());
        assertEquals(director.getId(), director1.getId());
    }

    @Test
    public void shouldConvertToEmbeddedClassWhenHasSubColumn3() {
        Movie movie = new Movie("Matrix", 2012, singleton("Actor"));
        Director director = Director.builderDiretor().withAge(12)
                .withId(12)
                .withName("Otavio")
                .withPhones(Arrays.asList("234", "2342")).withMovie(movie).build();

        ColumnEntity entity = converter.toColumn(director);
        entity.remove("movie");
        Map<String, Object> map = new HashMap<>();
        map.put("title", "Matrix");
        map.put("year", 2012);
        map.put("actors", singleton("Actor"));

        entity.add(Column.of("movie", map));
        Director director1 = converter.toEntity(entity);

        assertEquals(movie, director1.getMovie());
        assertEquals(director.getName(), director1.getName());
        assertEquals(director.getAge(), director1.getAge());
        assertEquals(director.getId(), director1.getId());
    }

    @Test
    public void shouldConvertToDocumentWhenHaConverter() {
        Worker worker = new Worker();
        Job job = new Job();
        job.setCity("Sao Paulo");
        job.setDescription("Java Developer");
        worker.setName("Bob");
        worker.setSalary(new Money("BRL", BigDecimal.TEN));
        worker.setJob(job);
        ColumnEntity entity = converter.toColumn(worker);
        assertEquals("Worker", entity.getName());
        assertEquals("Bob", entity.find("name").get().get());
        Column subColumn = entity.find("job").get();
        List<Column> columns = subColumn.get(new TypeReference<List<Column>>() {
        });
        assertThat(columns, Matchers.containsInAnyOrder(Column.of("city", "Sao Paulo"), Column.of("description", "Java Developer")));
        assertEquals("BRL 10", entity.find("money").get().get());
    }

    @Test
    public void shouldConvertToEntityWhenHasConverter() {
        Worker worker = new Worker();
        Job job = new Job();
        job.setCity("Sao Paulo");
        job.setDescription("Java Developer");
        worker.setName("Bob");
        worker.setSalary(new Money("BRL", BigDecimal.TEN));
        worker.setJob(job);
        ColumnEntity entity = converter.toColumn(worker);
        Worker worker1 = converter.toEntity(entity);
        Assert.assertEquals(worker.getSalary(), worker1.getSalary());
        assertEquals(job.getCity(), worker1.getJob().getCity());
        assertEquals(job.getDescription(), worker1.getJob().getDescription());
    }


    @Test
    public void shouldConvertListEmbeddable() {
        AppointmentBook appointmentBook = new AppointmentBook("ids");
        appointmentBook.add(Contact.builder().withType(ContactType.EMAIL).withName("Ada").withInformation("ada@lovelace.com").build());
        appointmentBook.add(Contact.builder().withType(ContactType.MOBILE).withName("Ada").withInformation("11 1231231 123").build());
        appointmentBook.add(Contact.builder().withType(ContactType.PHONE).withName("Ada").withInformation("12 123 1231 123123").build());

        ColumnEntity entity = converter.toColumn(appointmentBook);
        Column contacts = entity.find("contacts").get();
        assertEquals("ids", appointmentBook.getId());
        List<Column> columns = subDocument.get(new TypeReference<List<Column>>() {
        });

    }

    private Object getValue(Optional<Column> document) {
        return document.map(Column::getValue).map(Value::get).orElse(null);
    }

}