package org.jnosql.artemis.model;


import java.util.List;
import org.jnosql.artemis.Entity;
import org.jnosql.artemis.Field;

@Entity
public class Person {

    @Field("_id")
    private long id;

    @Field
    private String name;

    @Field
    private int age;

    @Field
    private List<String> phones;


}
