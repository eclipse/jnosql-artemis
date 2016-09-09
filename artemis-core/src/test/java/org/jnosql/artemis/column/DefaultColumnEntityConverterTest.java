package org.jnosql.artemis.column;

import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Stream;
import javax.inject.Inject;
import org.jnosql.artemis.WeldJUnit4Runner;
import org.jnosql.artemis.model.Actor;
import org.jnosql.artemis.model.Person;
import org.jnosql.artemis.reflection.ClassRepresentations;
import org.jnosql.diana.api.column.Column;
import org.jnosql.diana.api.column.ColumnFamilyEntity;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

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

    private Actor actor = Actor.actorBuilder().withAge(10)
            .withId(12)
            .withName("Otavio")
            .withPhones(Arrays.asList("234", "2342"))
            .withMovieCharacter(Collections.singletonMap("JavaZone", "Jedi"))
            .withMovierRating(Collections.singletonMap("JavaZone", 10))
            .build();

    @Before
    public void init() {
        classRepresentations.load(Person.class);
        classRepresentations.load(Actor.class);

        columns = new Column[]{Column.of("_id", 12L),
                Column.of("age", 10), Column.of("name", "Otavio"),
                Column.of("phones", Arrays.asList("234", "2342"))
                , Column.of("movieCharacter", Collections.singletonMap("JavaZone", "Jedi"))
                , Column.of("movieRating", Collections.singletonMap("JavaZone", 10))};
    }

    @Test
    public void shouldConvertPersonToDocument() {

        Person person = Person.builder().withAge(10)
                .withId(12)
                .withName("Otavio")
                .withPhones(Arrays.asList("234", "2342")).build();

        ColumnFamilyEntity entity = converter.toColumn(person);
        assertEquals("Person", entity.getName());
        assertEquals(4, entity.size());
        /*Assert.assertThat(entity.getColumns(), containsInAnyOrder(Document.of("_id", 12L),
                Document.of("age", 10), Document.of("name", "Otavio"), Document.of("phones", Arrays.asList("234", "2342"))));*/

    }

    @Test
    public void shouldConvertActorToDocument() {


        ColumnFamilyEntity entity = converter.toColumn(actor);
        assertEquals("Actor", entity.getName());
        assertEquals(6, entity.size());


        assertThat(entity.getColumns(), containsInAnyOrder(columns));
    }

    @Test
    public void shouldConvertDocumentToActor() {
        ColumnFamilyEntity entity = ColumnFamilyEntity.of("Actor");
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
        ColumnFamilyEntity entity = ColumnFamilyEntity.of("Actor");
        Stream.of(columns).forEach(entity::add);

        Actor actor = converter.toEntity(entity);
        assertNotNull(actor);
        assertEquals(10, actor.getAge());
        assertEquals(12L, actor.getId());
        assertEquals(Arrays.asList("234", "2342"), actor.getPhones());
        assertEquals(Collections.singletonMap("JavaZone", "Jedi"), actor.getMovieCharacter());
        assertEquals(Collections.singletonMap("JavaZone", 10), actor.getMovieRating());
    }

}