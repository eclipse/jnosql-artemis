package org.jnosql.artemis.model;


import java.util.List;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
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


    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public List<String> getPhones() {
        return phones;
    }

    public String getIgnore() {
        return ignore;
    }

    public Person() {
    }

    Person(long id, String name, int age, List<String> phones, String ignore) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.phones = phones;
        this.ignore = ignore;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("id", id)
                .append("name", name)
                .append("age", age)
                .append("phones", phones)
                .append("ignore", ignore)
                .toString();
    }

    public static PersonBuilder builder() {
        return new PersonBuilder();
    }
}
