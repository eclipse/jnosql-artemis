package org.jnosql.artemis.key;

import org.jnosql.artemis.WeldJUnit4Runner;
import org.jnosql.diana.api.document.DocumentEntity;
import org.jnosql.diana.api.key.BucketManager;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import static org.junit.Assert.*;


@RunWith(WeldJUnit4Runner.class)
public class DefaultKeyValueCRUDOperationTest {

    @Inject
    private KeyValueEntityConverter converter;

    @Inject
    private KeyValueWorkflow flow;

    private BucketManager manager;

    private ArgumentCaptor<DocumentEntity> captor;

    private KeyValueCRUDOperation subject;

    @Before
    public void setUp() {

    }
}