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
package org.jnosql.artemis;


import org.jnosql.artemis.document.DocumentRepository;

import java.time.Duration;

/**
 * Interface for generic CRUD operations on a repository for a specific type.
 *
 * @param <T> the bean
 */
public interface CrudRepository<T> {

    /**
     * Saves entity
     *
     * @param entity entity to be saved
     * @return the entity saved
     * @throws NullPointerException when document is null
     */
    T save(T entity) throws NullPointerException;

    /**
     * Saves entity with time to live
     *
     * @param entity entity to be saved
     * @param ttl    the time to live
     * @return the entity saved
     */
    T save(T entity, Duration ttl);

    /**
     * Saves entity, by default it's just run for each saving using
     * {@link DocumentRepository#save(Object)},
     * each NoSQL vendor might replace to a more appropriate one.
     *
     * @param entities entities to be saved
     * @return the entity saved
     * @throws NullPointerException when entities is null
     */
    Iterable<T> save(Iterable<T> entities) throws NullPointerException;

    /**
     * Saves documents collection entity with time to live, by default it's just run for each saving using
     * {@link DocumentRepository#save(Object, Duration)},
     * each NoSQL vendor might replace to a more appropriate one.
     *
     * @param entities entities to be saved
     * @param ttl      time to live
     * @return the entity saved
     * @throws NullPointerException when entities is null
     */
    Iterable<T> save(Iterable<T> entities, Duration ttl) throws NullPointerException;

    /**
     * Updates a entity
     *
     * @param entity entity to be updated
     * @return the entity updated
     */
    T update(T entity);

    /**
     * Updates entity, by default it's just run for each saving using
     * {@link DocumentRepository#update(Object)},
     * each NoSQL vendor might replace to a more appropriate one.
     *
     * @param entities entities to be saved
     * @return the entity saved
     * @throws NullPointerException when entities is null
     */
    Iterable<T> update(Iterable<T> entities) throws NullPointerException;


}
