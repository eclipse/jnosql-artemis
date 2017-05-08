/*
 * Copyright 2017 Otavio Santana and others
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jnosql.artemis.model;


import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.jnosql.artemis.Column;
import org.jnosql.artemis.Entity;
import org.jnosql.artemis.Id;
import org.jnosql.artemis.MappedSuperclass;

@Entity
@MappedSuperclass
public class Person {

    @Id
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

    Person() {
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
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Person person = (Person) o;
        return id == person.id &&
                age == person.age &&
                Objects.equals(name, person.name) &&
                Objects.equals(phones, person.phones);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, age, phones, ignore);
    }

    public static PersonBuilder builder() {
        return new PersonBuilder();
    }
}
