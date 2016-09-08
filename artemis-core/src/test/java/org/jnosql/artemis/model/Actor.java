package org.jnosql.artemis.model;


import java.util.List;
import java.util.Map;
import org.jnosql.artemis.Column;
import org.jnosql.artemis.Entity;

@Entity
public class Actor extends Person {

    @Column
    private Map<String, String> movieCharacter;

    @Column
    private Map<String, Integer> movieRating;

    Actor(long id, String name, int age, List<String> phones, String ignore, Map<String, String> movieCharacter, Map<String, Integer> movieRating) {
        super(id, name, age, phones, ignore);
        this.movieCharacter = movieCharacter;
        this.movieRating = movieRating;
    }

    Actor() {
    }

    public static ActorBuilder actorBuilder() {
        return new ActorBuilder();
    }
}
