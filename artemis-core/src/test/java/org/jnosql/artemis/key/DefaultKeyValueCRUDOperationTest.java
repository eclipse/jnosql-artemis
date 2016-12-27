package org.jnosql.artemis.key;

import org.jnosql.artemis.WeldJUnit4Runner;
import org.jnosql.artemis.model.Person;
import org.jnosql.artemis.model.User;
import org.jnosql.diana.api.document.DocumentEntity;
import org.jnosql.diana.api.key.BucketManager;
import org.jnosql.diana.api.key.KeyValueEntity;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;


@RunWith(WeldJUnit4Runner.class)
public class DefaultKeyValueCRUDOperationTest {

    @Inject
    private KeyValueEntityConverter converter;

    @Inject
    private KeyValueWorkflow flow;

    private BucketManager manager;

    private ArgumentCaptor<KeyValueEntity> captor;

    private KeyValueCRUDOperation subject;


    @Before
    public void setUp() {
        this.manager = Mockito.mock(BucketManager.class);
        Instance<BucketManager> instance = Mockito.mock(Instance.class);
        Mockito.when(instance.get()).thenReturn(manager);
        captor = ArgumentCaptor.forClass(KeyValueEntity.class);
        this.subject = new DefaultKeyValueCRUDOperation(converter, instance, flow);
    }


    @Test
    public void shouldPut() {
        User user = new User("otaviojava", "otavio", 27);
        subject.put(user);
        Mockito.verify(manager).put(captor.capture());
        KeyValueEntity entity = captor.getValue();
        assertEquals("otaviojava", entity.getKey());
        assertEquals(user, entity.getValue().get());
    }
}