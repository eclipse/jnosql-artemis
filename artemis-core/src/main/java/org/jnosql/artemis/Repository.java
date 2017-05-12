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
package org.jnosql.artemis;


import java.time.Duration;
import java.util.Optional;

/**
 * Interface to generic CRUD operations on a repository for a specific type.
 * The query builder mechanism built into Artemis repository infrastructure is useful for building constraining queries
 * over entities of the repository. The mechanism strips the prefixes is defined by:
 * <p>findBy: to select any information T</p>
 * <p>deleteBy: To delete any information T</p>
 * Artemis has some keywords on method:
 * <p><b>And</b></p>
 * <p><b>Or</b></p>
 * <p><b>Between</b></p>
 * <p><b>LessThan</b></p>
 * <p><b>GreaterThan</b></p>
 * <p><b>LessThanEqual</b></p>
 * <p><b>GreaterThanEqual</b></p>
 * <p><b>Like</b></p>
 * <p><b>OrderBy</b></p>
 * <p><b>OrderBy____Desc</b></p>
 * <p><b>OrderBy_____ASC</b></p>
 *
 * @param <T>  the bean type
 * @param <ID> the ID type
 */
public interface Repository<T, ID> {

    /**
     * Saves entity
     *
     * @param entity entity to be saved
     * @return the entity saved
     * @throws NullPointerException when document is null
     */
    T save(T entity) throws NullPointerException;

    /**
     * Saves entity
     * each NoSQL vendor might replace to a more appropriate one.
     *
     * @param entities entities to be saved
     * @return the entity saved
     * @throws NullPointerException when entities is null
     */
    Iterable<T> save(Iterable<T> entities) throws NullPointerException;

    /**
     * Deletes the entity with the given id.
     *
     * @param id the id
     * @throws NullPointerException when id is null
     */
    void deleteById(ID id) throws NullPointerException;

    /**
     * Deletes the entity with the given ids.
     *
     * @param ids the ids
     * @throws NullPointerException when either ids or same element is null
     */
    void deleteById(Iterable<ID> ids) throws NullPointerException;

    /**
     * Deletes the given entities.
     *
     * @param entities the entities
     * @throws NullPointerException when entities is null
     */
    void delete(Iterable<T> entities) throws NullPointerException;

    /**
     * Deletes the entity given the entity
     *
     * @param entity the entity
     * @throws NullPointerException when entity is null
     */
    void delete(T entity) throws NullPointerException;

    /**
     * Finds an entity given the id
     *
     * @param id the id
     * @return the entity given the ID
     * @throws NullPointerException when id is null
     */
    Optional<T> findById(ID id) throws NullPointerException;

    /**
     * Finds the entities given ids
     *
     * @param ids the ids
     * @return the entities from ids
     * @throws NullPointerException when the id is null
     */
    Iterable<T> findById(Iterable<ID> ids) throws NullPointerException;

    /**
     * Returns whether an entity with the given id exists.
     *
     * @param id the id
     * @return if the entity does exist or not
     * @throws NullPointerException when id is null
     */
    boolean existsById(ID id) throws NullPointerException;


}
