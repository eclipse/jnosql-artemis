/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jnosql.artemis.model;


import org.jnosql.artemis.Column;
import org.jnosql.artemis.Entity;

import java.util.List;

@Entity
public class Director extends Person {

    @Column
    private Movie movie;


    public Director() {
    }

    public Director(long id, String name, int age, List<String> phones, String ignore, Movie movie) {
        super(id, name, age, phones, ignore);
        this.movie = movie;
    }

    public static DirectorBuilder builderDiretor() {
        return new DirectorBuilder();
    }

    public static class DirectorBuilder {
        private long id;
        private String name;
        private int age;
        private List<String> phones;
        private String ignore;
        private Movie movie;

        private DirectorBuilder() {
        }

        public DirectorBuilder withId(long id) {
            this.id = id;
            return this;
        }

        public DirectorBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public DirectorBuilder withAge(int age) {
            this.age = age;
            return this;
        }

        public DirectorBuilder withPhones(List<String> phones) {
            this.phones = phones;
            return this;
        }

        public DirectorBuilder withIgnore(String ignore) {
            this.ignore = ignore;
            return this;
        }

        public DirectorBuilder withMovie(Movie movie) {
            this.movie = movie;
            return this;
        }

        public Director build() {
            return new Director(id, name, age, phones, ignore, movie);
        }
    }

}
