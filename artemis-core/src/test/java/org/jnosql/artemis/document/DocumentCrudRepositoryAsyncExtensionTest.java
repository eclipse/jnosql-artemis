package org.jnosql.artemis.document;

import org.jnosql.artemis.ArtemisDatabase;
import org.jnosql.artemis.DatabaseType;
import org.jnosql.artemis.PersonRepositoryAsync;
import org.jnosql.artemis.WeldJUnit4Runner;
import org.jnosql.artemis.model.Person;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;

import static org.junit.Assert.assertNotNull;

@RunWith(WeldJUnit4Runner.class)
public class DocumentCrudRepositoryAsyncExtensionTest {

    @Inject
    @ArtemisDatabase(value = DatabaseType.DOCUMENT)
    private PersonRepositoryAsync repository;


    @Test
    public void shouldIniciate() {
        assertNotNull(repository);
        repository.save(Person.builder().build());
    }
}