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
package org.jnosql.artemis.key;


import java.time.Duration;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.toList;


/**
 * This interface that represents the common operation between an entity and KeyValueEntity
 */
public interface KeyValueRepository {
    /**
     * Saves the entity
     *
     * @param entity the entity to be save
     * @param <T>    the entity type
     * @throws NullPointerException when entity is null
     */
    <T> T put(T entity) throws NullPointerException;

    /**
     * Saves the entity with time to live
     *
     * @param entity the entity to be save
     * @param ttl    the defined time to live
     * @param <T>    the entity type
     * @throws NullPointerException          when entity is null
     * @throws UnsupportedOperationException when expired time is not supported
     */
    <T> T put(T entity, Duration ttl) throws NullPointerException, UnsupportedOperationException;

    /**
     * Saves the {@link Iterable} of entities
     *
     * @param entities keys to be save
     * @param <T>      the entity type
     * @throws NullPointerException when the iterable is null
     */
    default <T> Iterable<T> put(Iterable<T> entities) throws NullPointerException {
        Objects.requireNonNull(entities, "entities is required");
        return StreamSupport.stream(entities.spliterator(), false).map(this::put).collect(Collectors.toList());
    }

    /**
     * Saves the {@link Iterable} of entities with a defined time to live
     *
     * @param entities entities to be save
     * @param ttl      the time to entity expire
     * @param <T>      the entity type
     * @throws NullPointerException          when the iterable is null
     * @throws UnsupportedOperationException when expired time is not supported
     */
    default<T> Iterable<T> put(Iterable<T> entities, Duration ttl) throws NullPointerException, UnsupportedOperationException {
        Objects.requireNonNull(entities, "entities is required");
        Objects.requireNonNull(ttl, "ttl is required");
        return StreamSupport.stream(entities.spliterator(), false).map(d -> put(d, ttl)).collect(toList());
    }

    /**
     * Finds the Value from a key
     *
     * @param key the key
     * @param <K> the key type
     * @param <T> the entity type
     * @return the {@link Optional} when is not found will return a {@link Optional#empty()}
     * @throws NullPointerException when the key is null
     */
    <K, T> Optional<T> get(K key, Class<T> clazz) throws NullPointerException;

    /**
     * Finds a list of values from keys
     *
     * @param keys the keys to be used in this query
     * @param <K>  the key type
     * @param <T>  the entity type
     * @return the list of result
     * @throws NullPointerException when either the keys or the entities values are null
     */
    <K, T> Iterable<T> get(Iterable<K> keys, Class<T> clazz) throws NullPointerException;

    /**
     * Removes an entity from key
     *
     * @param key the key bo be used
     * @param <K> the key type
     * @throws NullPointerException when the key is null
     */
    <K> void remove(K key) throws NullPointerException;

    /**
     * Removes entities from keys
     *
     * @param keys the keys to be used
     * @param <K>  the key type
     * @throws NullPointerException when the key is null
     */
    <K> void remove(Iterable<K> keys) throws NullPointerException;

}
