package org.jnosql.artemis.model;

import java.util.List;

public class PersonBuilder {
    private long id;
    private String name;
    private int age;
    private List<String> phones;
    private String ignore;

    public PersonBuilder withId(long id) {
        this.id = id;
        return this;
    }

    public PersonBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public PersonBuilder withAge(int age) {
        this.age = age;
        return this;
    }

    public PersonBuilder withPhones(List<String> phones) {
        this.phones = phones;
        return this;
    }

    public PersonBuilder withIgnore(String ignore) {
        this.ignore = ignore;
        return this;
    }

    public Person build() {
        return new Person(id, name, age, phones, ignore);
    }
}