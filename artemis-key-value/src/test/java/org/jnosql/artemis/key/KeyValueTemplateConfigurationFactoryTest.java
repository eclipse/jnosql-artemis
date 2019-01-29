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
    @ConfigurationUnit(fileName = "key-value.json", name = "name")
    private BucketManagerFactory<?> factoryA;

    @Inject
    @ConfigurationUnit(fileName = "key-value.json", name = "name-2")
    private BucketManagerFactory factoryB;


    @Test
    public void shouldReadBucketManager() {
        factoryA.getBucketManager("database");
        assertTrue(KeyValueConfigurationMock.BucketManagerFactoryMock.class.isInstance(factoryA));
        KeyValueConfigurationMock.BucketManagerFactoryMock mock = KeyValueConfigurationMock.BucketManagerFactoryMock.class.cast(factoryA);
        Map<String, Object> settings = mock.getSettings();
        assertEquals("value", settings.get("key"));
        assertEquals("value2", settings.get("key2"));
    }

    @Test
    public void shouldReadBucketManagerB() {
        factoryB.getBucketManager("database");
        assertTrue(KeyValueConfigurationMock.BucketManagerFactoryMock.class.isInstance(factoryB));
        KeyValueConfigurationMock.BucketManagerFactoryMock mock = KeyValueConfigurationMock.BucketManagerFactoryMock.class.cast(factoryB);
        Map<String, Object> settings = mock.getSettings();
        assertEquals("value", settings.get("key"));
        assertEquals("value2", settings.get("key2"));
        assertEquals("value3", settings.get("key3"));
    }

}