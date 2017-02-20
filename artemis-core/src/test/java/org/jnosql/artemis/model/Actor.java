/*
 * Copyright 2017 Otavio Santana and others
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

    public Map<String, String> getMovieCharacter() {
        return movieCharacter;
    }

    public Map<String, Integer> getMovieRating() {
        return movieRating;
    }

    public static ActorBuilder actorBuilder() {
        return new ActorBuilder();
    }


}
