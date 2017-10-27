/*
 *  Copyright (c) 2017 Otávio Santana and others
 *   All rights reserved. This program and the accompanying materials
 *   are made available under the terms of the Eclipse Public License v1.0
 *   and Apache License v2.0 which accompanies this distribution.
 *   The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 *   and the Apache License v2.0 is available at http://www.opensource.org/licenses/apache2.0.php.
 *
 *   You may elect to redistribute this code under either of these licenses.
 *
 *   Contributors:
 *
 *   Otavio Santana
 */
package org.jnosql.artemis.column;


import org.jnosql.diana.api.ExecuteAsyncQueryException;
import org.jnosql.diana.api.NonUniqueResultException;
import org.jnosql.diana.api.column.ColumnDeleteQuery;
import org.jnosql.diana.api.column.ColumnQuery;

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.StreamSupport;

import static java.util.Objects.requireNonNull;

/**
 * This interface that represents the common operation between an entity
 * and {@link org.jnosql.diana.api.column.ColumnEntity} to do async operations
 *
 * @see org.jnosql.diana.api.column.ColumnFamilyManagerAsync
 */
public interface ColumnTemplateAsync {

    /**
     * Inserts an entity asynchronously
     *
     * @param entity entity to be saved
     * @param <T>    the instance type
     * @throws ExecuteAsyncQueryException    when there is a async error
     * @throws UnsupportedOperationException when the database does not have support to insert asynchronous
     * @throws NullPointerException          when entity is null
     */
    <T> void insert(T entity) throws ExecuteAsyncQueryException, UnsupportedOperationException, NullPointerException;

    /**
     * Inserts an entity asynchronously with time to live
     *
     * @param entity entity to be saved
     * @param ttl    the time to live
     * @param <T>    the instance type
     * @throws ExecuteAsyncQueryException    when there is a async error
     * @throws UnsupportedOperationException when the database does not have support to insert asynchronous
     * @throws NullPointerException          when either entity or ttl are null
     */
    <T> void insert(T entity, Duration ttl) throws ExecuteAsyncQueryException, UnsupportedOperationException, NullPointerException;

    /**
     * Inserts entities asynchronously, by default it's just run for each saving using
     * {@link ColumnTemplate#insert(Object)},
     * each NoSQL vendor might replace to a more appropriate one.
     *
     * @param entities entities to be saved
     * @param <T>      the instance type
     * @throws ExecuteAsyncQueryException    when there is a async error
     * @throws UnsupportedOperationException when the database does not have support to insert asynchronous
     * @throws NullPointerException          when entities is null
     */
    default <T> void insert(Iterable<T> entities) throws ExecuteAsyncQueryException, UnsupportedOperationException, NullPointerException {
        Objects.requireNonNull(entities, "entities is required");
        StreamSupport.stream(entities.spliterator(), false).forEach(this::insert);
    }

    /**
     * Inserts entities asynchronously with time to live, by default it's just run for each saving using
     * {@link ColumnTemplate#insert(Object, Duration)},
     * each NoSQL vendor might replace to a more appropriate one.
     *
     * @param entities entities to be saved
     * @param ttl      time to live
     * @param <T>      the instance type
     * @throws ExecuteAsyncQueryException    when there is a async error
     * @throws UnsupportedOperationException when the database does not have support to insert asynchronous
     * @throws NullPointerException          when either entities or ttl are null
     */
    default <T> void insert(Iterable<T> entities, Duration ttl) throws NullPointerException {
        Objects.requireNonNull(entities, "entities is required");
        Objects.requireNonNull(ttl, "ttl is required");
        StreamSupport.stream(entities.spliterator(), false).forEach(d -> insert(d, ttl));
    }

    /**
     * Inserts an entity asynchronously
     *
     * @param entity   entity to be saved
     * @param callBack the callback, when the process is finished will call this instance returning
     *                 the saved entity within parameters
     * @param <T>      the instance type
     * @throws ExecuteAsyncQueryException    when there is a async error
     * @throws UnsupportedOperationException when the database does not have support to insert asynchronous
     * @throws NullPointerException          when either entity or callback are null
     */
    <T> void insert(T entity, Consumer<T> callBack) throws
            ExecuteAsyncQueryException, UnsupportedOperationException, NullPointerException;


    /**
     * Inserts an entity asynchronously with time to live
     *
     * @param entity   entity to be saved
     * @param ttl      time to live
     * @param callBack the callback, when the process is finished will call this instance returning
     *                 the saved entity within parameters
     * @param <T>      the instance type
     * @throws ExecuteAsyncQueryException    when there is a async error
     * @throws UnsupportedOperationException when the database does not have support to insert asynchronous
     * @throws NullPointerException          when either entity or ttl or callback are null
     */
    <T> void insert(T entity, Duration ttl, Consumer<T> callBack) throws
            ExecuteAsyncQueryException, UnsupportedOperationException, NullPointerException;

    /**
     * Inserts an entity asynchronously
     *
     * @param entity entity to be updated
     * @param <T>    the instance type
     * @throws ExecuteAsyncQueryException    when there is a async error
     * @throws UnsupportedOperationException when the database does not have support to insert asynchronous
     * @throws NullPointerException          when entity is null
     */
    <T> void update(T entity) throws ExecuteAsyncQueryException, UnsupportedOperationException, NullPointerException;

    /**
     * Inserts entities asynchronously, by default it's just run for each saving using
     * {@link ColumnTemplate#update(Object)},
     * each NoSQL vendor might replace to a more appropriate one.
     *
     * @param entities entities to be saved
     * @param <T>      the instance type
     * @throws ExecuteAsyncQueryException    when there is a async error
     * @throws UnsupportedOperationException when the database does not have support to insert asynchronous
     * @throws NullPointerException          when entities is null
     */
    default <T> void update(Iterable<T> entities) throws ExecuteAsyncQueryException, UnsupportedOperationException, NullPointerException {
        Objects.requireNonNull(entities, "entities is required");
        StreamSupport.stream(entities.spliterator(), false).forEach(this::update);
    }

    /**
     * Inserts an entity asynchronously
     *
     * @param entity   entity to be updated
     * @param callBack the callback, when the process is finished will call this instance returning
     *                 the updated entity within parametersa
     * @param <T>      the instance type
     * @throws ExecuteAsyncQueryException    when there is a async error
     * @throws UnsupportedOperationException when the database does not have support to insert asynchronous
     * @throws NullPointerException          when either entity or callback are null
     */
    <T> void update(T entity, Consumer<T> callBack) throws
            ExecuteAsyncQueryException, UnsupportedOperationException, NullPointerException;

    /**
     * Deletes an entity asynchronously
     *
     * @param query query to delete an entity
     * @throws ExecuteAsyncQueryException    when there is a async error
     * @throws UnsupportedOperationException when the database does not have support to insert asynchronous
     * @throws NullPointerException          when query are null
     */
    void delete(ColumnDeleteQuery query) throws ExecuteAsyncQueryException, UnsupportedOperationException, NullPointerException;

    /**
     * Deletes an entity asynchronously
     *
     * @param query    query to delete an entity
     * @param callBack the callback, when the process is finished will call this instance returning
     *                 the null within parameters
     * @throws ExecuteAsyncQueryException    when there is a async error
     * @throws UnsupportedOperationException when the database does not have support to delete asynchronous
     * @throws NullPointerException          when either query or callback are null
     */
    void delete(ColumnDeleteQuery query, Consumer<Void> callBack) throws ExecuteAsyncQueryException,
            UnsupportedOperationException, NullPointerException;

    /**
     * Finds entities from query asynchronously
     *
     * @param query    query to select entities
     * @param <T>      the instance type
     * @param callBack the callback, when the process is finished will call this instance returning
     *                 the result of query within parameters
     * @throws ExecuteAsyncQueryException    when there is a async error
     * @throws UnsupportedOperationException when the database does not have support to insert asynchronous
     * @throws NullPointerException          when either query or callback are null
     */
    <T> void select(ColumnQuery query, Consumer<List<T>> callBack) throws
            ExecuteAsyncQueryException, UnsupportedOperationException, NullPointerException;

    /**
     * Execute a query to consume an unique result
     *
     * @param query    the query
     * @param callBack the callback
     * @param <T>      the type
     * @throws ExecuteAsyncQueryException    when there is a async error
     * @throws UnsupportedOperationException when the database does not have support to insert asynchronous
     * @throws NullPointerException          when either query or callback are null
     * @throws NonUniqueResultException      when it returns more than one result
     */
    default <T> void singleResult(ColumnQuery query, Consumer<Optional<T>> callBack) throws
            ExecuteAsyncQueryException, UnsupportedOperationException, NullPointerException, NonUniqueResultException {

        requireNonNull(callBack, "callBack is required");

        Consumer<List<T>> singleCallBack = entities -> {
            if (entities.isEmpty()) {
                callBack.accept(Optional.empty());
            } else if (entities.size() == 1) {
                callBack.accept(Optional.of(entities.get(0)));
            } else {
                throw new NonUniqueResultException("The query returns more than one entity, query: " + query);
            }
        };
        select(query, singleCallBack);

    }
}
