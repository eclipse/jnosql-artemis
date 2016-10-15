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

    public ActorBuilder withId() {
        this.id = (long) 12;
        return this;
    }

    public ActorBuilder withName() {
        this.name = "Otavio";
        return this;
    }

    public ActorBuilder withAge() {
        this.age = 10;
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