package org.jnosql.artemis.model;


import java.util.List;
import java.util.Set;
import org.jnosql.artemis.Entity;
import org.jnosql.artemis.Field;

@Entity("movie")
public class Movie {

    @Field
    private String name;

    @Field
    private long year;

    @Field
    private Set<String> races;


}
