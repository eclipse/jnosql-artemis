package org.jnosql.artemis.column;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import org.jnosql.artemis.WeldJUnit4Runner;
import org.jnosql.artemis.model.Person;
import org.jnosql.diana.api.column.Column;
import org.jnosql.diana.api.column.ColumnFamilyEntity;
import org.jnosql.diana.api.column.ColumnFamilyManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;

@RunWith(WeldJUnit4Runner.class)
public class DefaultColumnCrudOperationTest {

    private Person person = Person.builder().
            withAge(10).
            withPhones(Arrays.asList("234", "432")).
            withName("Name")
            .withId(19)
            .withIgnore("Just Ignore").build();

    private Column[] columns = new Column[]{
            Column.of("age", 10),
            Column.of("phones", Arrays.asList("234", "432")),
            Column.of("name", "Name"),
            Column.of("id", 19L),
    };


    @Inject
    private ColumnEntityConverter converter;

    private ColumnFamilyManager managerMock;

    private DefaultColumnCrudOperation subject;

    private ArgumentCaptor<ColumnFamilyEntity> captor;

    private ColumnPersistManager columnPersistManager;

    @Before
    public void setUp() {
        managerMock = Mockito.mock(ColumnFamilyManager.class);
        columnPersistManager = Mockito.mock(ColumnPersistManager.class);
        captor = ArgumentCaptor.forClass(ColumnFamilyEntity.class);
        Instance<ColumnFamilyManager> instance = Mockito.mock(Instance.class);
        Mockito.when(instance.get()).thenReturn(managerMock);
        this.subject = new DefaultColumnCrudOperation(converter, instance, columnPersistManager);
    }

    @Test
    public void shouldSave() {
        ColumnFamilyEntity document = ColumnFamilyEntity.of("Person");
        document.addAll(Stream.of(columns).collect(Collectors.toList()));

        Mockito.when(managerMock
                .save(Mockito.any(ColumnFamilyEntity.class)))
                .thenReturn(document);

        subject.save(this.person);
        verify(managerMock).save(captor.capture());
        verify(columnPersistManager).firePostEntity(Mockito.any(Person.class));
        verify(columnPersistManager).firePreEntity(Mockito.any(Person.class));
        verify(columnPersistManager).firePreDocument(Mockito.any(ColumnFamilyEntity.class));
        verify(columnPersistManager).firePostDocument(Mockito.any(ColumnFamilyEntity.class));
        ColumnFamilyEntity value = captor.getValue();
        assertEquals("Person", value.getName());
        assertEquals(4, value.getColumns().size());
    }

}