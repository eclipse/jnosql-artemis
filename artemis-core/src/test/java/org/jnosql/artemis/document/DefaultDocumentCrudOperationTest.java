package org.jnosql.artemis.document;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import org.jnosql.artemis.WeldJUnit4Runner;
import org.jnosql.artemis.model.Person;
import org.jnosql.diana.api.document.Document;
import org.jnosql.diana.api.document.DocumentCollectionEntity;
import org.jnosql.diana.api.document.DocumentCollectionManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;


@RunWith(WeldJUnit4Runner.class)
public class DefaultDocumentCrudOperationTest {

    private Person person = Person.builder().
            withAge(10).
            withPhones(Arrays.asList("234", "432")).
            withName("Name")
            .withId(19)
            .withIgnore("Just Ignore").build();

    private Document[] documents = new Document[]{
            Document.of("age", 10),
            Document.of("phones", Arrays.asList("234", "432")),
            Document.of("name", "Name"),
            Document.of("id", 19L),
    };


    @Inject
    private DocumentEntityConverter converter;

    private DocumentCollectionManager managerMock;

    private DefaultDocumentCrudOperation subject;

    private ArgumentCaptor<DocumentCollectionEntity> captor;

    private DocumentPersistManager documentPersistManager;

    @Before
    public void setUp() {
        managerMock = Mockito.mock(DocumentCollectionManager.class);
        documentPersistManager = Mockito.mock(DocumentPersistManager.class);
        captor = ArgumentCaptor.forClass(DocumentCollectionEntity.class);
        Instance<DocumentCollectionManager> instance = Mockito.mock(Instance.class);
        Mockito.when(instance.get()).thenReturn(managerMock);
        this.subject = new DefaultDocumentCrudOperation(converter, instance, documentPersistManager);
    }

    @Test
    public void shouldSave() {
        DocumentCollectionEntity document = DocumentCollectionEntity.of("Person");
        document.addAll(Stream.of(documents).collect(Collectors.toList()));

        Mockito.when(managerMock
                .save(Mockito.any(DocumentCollectionEntity.class)))
                .thenReturn(document);

        subject.save(this.person);
        verify(managerMock).save(captor.capture());
        verify(documentPersistManager).firePostEntity(Mockito.any(Person.class));
        verify(documentPersistManager).firePreEntity(Mockito.any(Person.class));
        verify(documentPersistManager).firePreDocument(Mockito.any(DocumentCollectionEntity.class));
        verify(documentPersistManager).firePostDocument(Mockito.any(DocumentCollectionEntity.class));
        DocumentCollectionEntity value = captor.getValue();
        assertEquals("Person", value.getName());
        assertEquals(4, value.getDocuments().size());
    }

}