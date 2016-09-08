package org.jnosql.artemis.document;

import java.util.Arrays;
import java.util.Collections;
import javax.inject.Inject;
import org.hamcrest.Matchers;
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
public class DocumentEntityConverterTest {

    @Inject
    private DocumentEntityConverter converter;

    @Inject
    private ClassRepresentations classRepresentations;

    @Before
    public void init() {
        classRepresentations.load(Person.class);
        classRepresentations.load(Actor.class);
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

        Actor actor = Actor.actorBuilder().withAge(10)
                .withId(12)
                .withName("Otavio")
                .withPhones(Arrays.asList("234", "2342"))
                .withMovieCharacter(Collections.singletonMap("JavaZone", "Jedi"))
                .withMovierRating(Collections.singletonMap("JavaZone", 10))
                .build();

        DocumentCollectionEntity entity = converter.toDocument(actor);
        assertEquals("Actor", entity.getName());
        assertEquals(6, entity.size());
        Document[] documents = {Document.of("_id", 12L),
                Document.of("age", 10), Document.of("name", "Otavio"), Document.of("phones", Arrays.asList("234", "2342"))
                , Document.of("movieCharacter", Collections.singletonMap("JavaZone", "Jedi"))
                , Document.of("movieRating", Collections.singletonMap("JavaZone", 10))};

        assertThat(entity.getDocuments(), containsInAnyOrder(documents));

    }
}