package org.jnosql.artemis.key;

import org.jnosql.artemis.CDIExtension;
import org.jnosql.artemis.ConfigurationUnit;
import org.jnosql.diana.api.key.BucketManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.inject.Inject;

@ExtendWith(CDIExtension.class)
class KeyValueTemplateConfigurationFactoryTest {

    @Inject
    @ConfigurationUnit(fileName = "key-value.json", name = "name", database = "database")
    private KeyValueTemplate templateA;

    @Inject
    @ConfigurationUnit(fileName = "key-value.json", name = "name-2", database = "database")
    private KeyValueTemplate templateB;

    @Test
    public void shouldTemplate() {
        Assertions.assertNotNull(templateA);
        BucketManager manager = AbstractKeyValueTemplate.class.cast(templateA).getManager();
        Assertions.assertNotNull(manager);

    }

    @Test
    public void shouldTemplateB() {
        Assertions.assertNotNull(templateB);
        BucketManager manager = AbstractKeyValueTemplate.class.cast(templateA).getManager();
        Assertions.assertNotNull(manager);
    }


}