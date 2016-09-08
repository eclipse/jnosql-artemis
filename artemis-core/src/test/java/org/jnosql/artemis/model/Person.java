package org.jnosql.artemis.model;


import java.util.List;
import org.jnosql.artemis.Column;
import org.jnosql.artemis.Entity;
import org.jnosql.artemis.MappedSuperclass;

@Entity
@MappedSuperclass
public class Person {

    @Column("_id")
    private long id;

    @Column
    private String name;

    @Column
    private int age;

    @Column
    private List<String> phones;

    private String ignore;


}
