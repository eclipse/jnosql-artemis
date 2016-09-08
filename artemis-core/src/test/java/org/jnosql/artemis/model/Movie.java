package org.jnosql.artemis.model;


import java.util.Set;
import org.jnosql.artemis.Column;
import org.jnosql.artemis.Entity;

@Entity("movie")
public class Movie {

    @Column
    private String name;

    @Column
    private long year;

    @Column
    private Set<String> actors;


}
