package org.jnosql.artemis.key.spi;

import org.jnosql.artemis.ConfigurationUnit;

import javax.inject.Inject;

public class Amor {
    @Inject
    @ConfigurationUnit(fileName = "key-value.json", name = "name", database = "database")
    private Wrapper<PersonRepository> repository;

    public Wrapper<PersonRepository> getRepository() {
        return repository;
    }
}
