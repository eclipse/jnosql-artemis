package org.jnosql.artemis.key;

import org.jnosql.artemis.CDIExtension;
import org.jnosql.artemis.ConfigurationUnit;
import org.jnosql.diana.api.Settings;
import org.jnosql.diana.api.key.BucketManagerFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(CDIExtension.class)
class KeyValueTemplateConfigurationFactoryTest {

    @Inject
    @ConfigurationUnit(fileName = "key-value.json", name = "name", database = "database")
    private KeyValueTemplate templateA;

    @Inject
    @ConfigurationUnit(fileName = "key-value.json", name = "name-2", database = "database")
    private KeyValueTemplate templateB;


    @Test
    public void shouldReadBucketManager() {
        System.out.println(templateA);
    }

    @Test
    public void shouldReadBucketManagerB() {

    }

}