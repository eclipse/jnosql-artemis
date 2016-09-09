package org.jnosql.artemis.document;

import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Stream;
import javax.inject.Inject;
import org.jnosql.artemis.WeldJUnit4Runner;
import org.jnosql.artemis.model.Actor;
import org.jnosql.artemis.model.Person;
import org.jnosql.artemis.reflection.ClassRepresentations;
import org.jnosql.diana.api.document.Document;
import org.jnosql.diana.api.document.DocumentCollectionEntity;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.*;

@RunWith(WeldJUnit4Runner.class)
public class DefaultDocumentEntityConverterTest {

    @Inject
    private DefaultDocumentEntityConverter converter;

    @Inject
    private ClassRepresentations classRepresentations;
    private Document[] documents;

    private Actor actor = Actor.actorBuilder().withAge(10)
            .withId(12)
            .withName("Otavio")
            .withPhones(Arrays.asList("234", "2342"))
            .withMovieCharacter(Collections.singletonMap("JavaZone", "Jedi"))
            .withMovierRating(Collections.singletonMap("JavaZone", 10))
            .build();

    @Before
    public void init() {

        documents = new Document[]{Document.of("_id", 12L),
                Document.of("age", 10), Document.of("name", "Otavio"), Document.of("phones", Arrays.asList("234", "2342"))
                , Document.of("movieCharacter", Collections.singletonMap("JavaZone", "Jedi"))
                , Document.of("movieRating", Collections.singletonMap("JavaZone", 10))};
    }

    @Test
    public void shouldConvertPersonToDocument() {

        Person person = Person.builder().withAge(10)
                .withId(12)
                .withName("Otavio")
                .withPhones(Arrays.asList("234", "2342")).build();

        DocumentCollectionEntity entity = converter.toDocument(person);
        assertEquals("Person", entity.getName());
        assertEquals(4, entity.size());
        assertThat(entity.getDocuments(), containsInAnyOrder(Document.of("_id", 12L),
                Document.of("age", 10), Document.of("name", "Otavio"), Document.of("phones", Arrays.asList("234", "2342"))));

    }

    @Test
    public void shouldConvertActorToDocument() {


        DocumentCollectionEntity entity = converter.toDocument(actor);
        assertEquals("Actor", entity.getName());
        assertEquals(6, entity.size());


        assertThat(entity.getDocuments(), containsInAnyOrder(documents));
    }

    @Test
    public void shouldConvertDocumentToActor() {
        DocumentCollectionEntity entity = DocumentCollectionEntity.of("Actor");
        Stream.of(documents).forEach(entity::add);

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
        DocumentCollectionEntity entity = DocumentCollectionEntity.of("Actor");
        Stream.of(documents).forEach(entity::add);

        Actor actor = converter.toEntity(entity);
        assertNotNull(actor);
        assertEquals(10, actor.getAge());
        assertEquals(12L, actor.getId());
        assertEquals(Arrays.asList("234", "2342"), actor.getPhones());
        assertEquals(Collections.singletonMap("JavaZone", "Jedi"), actor.getMovieCharacter());
        assertEquals(Collections.singletonMap("JavaZone", 10), actor.getMovieRating());
    }
}