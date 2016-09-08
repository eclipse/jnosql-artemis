package org.jnosql.artemis.model;


import java.util.Map;
import org.jnosql.artemis.Column;
import org.jnosql.artemis.Entity;

@Entity
public class Actor extends Person {

    @Column
    private Map<String, String> movieCharacter;

    @Column
    private Map<String, Integer> movierRating;
}
