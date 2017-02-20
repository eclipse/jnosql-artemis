package org.jnosql.artemis.validation;


import org.jnosql.diana.api.key.BucketManager;
import org.mockito.Mockito;

import javax.enterprise.inject.Produces;

public class ManagerProducerProducer {


    @Produces
    public BucketManager getBucketManager() {
        BucketManager manager = Mockito.mock(BucketManager.class);
        return manager;
    }
}
