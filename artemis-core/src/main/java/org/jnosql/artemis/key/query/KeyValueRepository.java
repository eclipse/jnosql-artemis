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
package org.jnosql.artemis.key.query;


import java.time.Duration;
import java.util.Optional;

/**
 * Interface to generic CRUD operations to key value type.
 * @param <T>  the bean type.
 */
public interface KeyValueRepository<T> {

    /**
     * Saves the entity
     *
     * @param entity the entity to be insert
     * @throws NullPointerException when entity is null
     */
     T put(T entity) throws NullPointerException;

    /**
     * Saves the entity with time to live
     *
     * @param entity the entity to be insert
     * @param ttl    the defined time to live
     * @throws NullPointerException          when entity is null
     * @throws UnsupportedOperationException when expired time is not supported
     */
    T put(T entity, Duration ttl) throws NullPointerException, UnsupportedOperationException;

    /**
     * Saves the {@link Iterable} of entities
     *
     * @param entities keys to be insert
     * @throws NullPointerException when the iterable is null
     */
      Iterable<T> put(Iterable<T> entities) throws NullPointerException;

    /**
     * Saves the {@link Iterable} of entities with a defined time to live
     *
     * @param entities entities to be insert
     * @param ttl      the time to entity expire
     * @throws NullPointerException          when the iterable is null
     * @throws UnsupportedOperationException when expired time is not supported
     */
    Iterable<T> put(Iterable<T> entities, Duration ttl) throws NullPointerException, UnsupportedOperationException;

    /**
     * Finds the Value from a key
     *
     * @param key the key
     * @param <K> the key type
     * @return the {@link Optional} when is not found will return a {@link Optional#empty()}
     * @throws NullPointerException when the key is null
     */
    <K> Optional<T> get(K key) throws NullPointerException;

    /**
     * Finds a list of values from keys
     *
     * @param keys the keys to be used in this query
     * @param <K>  the key type
     * @return the list of result
     * @throws NullPointerException when either the keys or the entities values are null
     */
    <K> Iterable<T> get(Iterable<K> keys) throws NullPointerException;

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
