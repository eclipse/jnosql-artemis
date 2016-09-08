package org.jnosql.artemis.model;

import java.util.List;
import java.util.Map;

public class ActorBuilder {
    private long id;
    private String name;
    private int age;
    private List<String> phones;
    private String ignore;
    private Map<String, String> movieCharacter;
    private Map<String, Integer> movierRating;

    ActorBuilder() {
    }

    public ActorBuilder withId(long id) {
        this.id = id;
        return this;
    }

    public ActorBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public ActorBuilder withAge(int age) {
        this.age = age;
        return this;
    }

    public ActorBuilder withPhones(List<String> phones) {
        this.phones = phones;
        return this;
    }

    public ActorBuilder withIgnore(String ignore) {
        this.ignore = ignore;
        return this;
    }

    public ActorBuilder withMovieCharacter(Map<String, String> movieCharacter) {
        this.movieCharacter = movieCharacter;
        return this;
    }

    public ActorBuilder withMovierRating(Map<String, Integer> movierRating) {
        this.movierRating = movierRating;
        return this;
    }

    public Actor build() {
        return new Actor(id, name, age, phones, ignore, movieCharacter, movierRating);
    }
}