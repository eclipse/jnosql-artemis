package org.jnosql.artemis.document;


import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.jnosql.diana.api.ExecuteAsyncQueryException;
import org.jnosql.diana.api.NonUniqueResultException;
import org.jnosql.diana.api.TTL;
import org.jnosql.diana.api.document.DocumentCollectionEntity;
import org.jnosql.diana.api.document.DocumentCollectionManager;
import org.jnosql.diana.api.document.DocumentQuery;

public interface DocumentCrudOperation {


    /**
     * Saves document collection entity
     *
     * @param entity entity to be saved
     * @return the entity saved
     * @throws NullPointerException when document is null
     */
    <T> T save(T entity) throws NullPointerException;

    /**
     * Saves an entity asynchronously
     *
     * @param entity entity to be saved
     * @throws ExecuteAsyncQueryException    when there is a async error
     * @throws UnsupportedOperationException when the database does not have support to save asynchronous
     */
    <T> void saveAsync(T entity) throws ExecuteAsyncQueryException, UnsupportedOperationException;

    /**
     * Saves document collection entity with time to live
     *
     * @param entity entity to be saved
     * @param ttl    the time to live
     * @return the entity saved
     */
    <T> T save(T entity, TTL ttl);

    /**
     * Saves an entity asynchronously with time to live
     *
     * @param entity entity to be saved
     * @param ttl    the time to live
     * @throws ExecuteAsyncQueryException    when there is a async error
     * @throws UnsupportedOperationException when the database does not have support to save asynchronous
     */
    <T> void saveAsync(T entity, TTL ttl) throws ExecuteAsyncQueryException, UnsupportedOperationException;

    /**
     * Saves documents collection entity, by default it's just run for each saving using
     * {@link DocumentCollectionManager#save(DocumentCollectionEntity)},
     * each NoSQL vendor might replace to a more appropriate one.
     *
     * @param entities entities to be saved
     * @return the entity saved
     * @throws NullPointerException when entities is null
     */
     default <T> Iterable<T> save(Iterable<T> entities) throws NullPointerException {
        Objects.requireNonNull(entities, "entities is required");
        return StreamSupport.stream(entities.spliterator(), false).map(this::save).collect(Collectors.toList());
    }

    /**
     * Saves entities asynchronously, by default it's just run for each saving using
     * {@link DocumentCollectionManager#saveAsync(DocumentCollectionEntity)},
     * each NoSQL vendor might replace to a more appropriate one.
     *
     * @param entities entities to be saved
     * @throws ExecuteAsyncQueryException    when there is a async error
     * @throws UnsupportedOperationException when the database does not have support to save asynchronous
     */
    default <T> void saveAsync(Iterable<T> entities) throws ExecuteAsyncQueryException, UnsupportedOperationException {
        Objects.requireNonNull(entities, "entities is required");
        StreamSupport.stream(entities.spliterator(), false).forEach(this::saveAsync);
    }

    /**
     * Saves documents collection entity with time to live, by default it's just run for each saving using
     * {@link DocumentCollectionManager#save(DocumentCollectionEntity, TTL)},
     * each NoSQL vendor might replace to a more appropriate one.
     *
     * @param entities entities to be saved
     * @param ttl      time to live
     * @return the entity saved
     * @throws NullPointerException when entities is null
     */
    default <T> Iterable<T> save(Iterable<T> entities, TTL ttl) throws NullPointerException {
        Objects.requireNonNull(entities, "entities is required");
        Objects.requireNonNull(ttl, "ttl is required");
        return StreamSupport.stream(entities.spliterator(), false).map(d -> save(d, ttl)).collect(Collectors.toList());
    }

    /**
     * Saves entities asynchronously with time to live, by default it's just run for each saving using
     * {@link DocumentCollectionManager#saveAsync(DocumentCollectionEntity, TTL)},
     * each NoSQL vendor might replace to a more appropriate one.
     *
     * @param entities entities to be saved
     * @param ttl      time to live
     * @throws ExecuteAsyncQueryException    when there is a async error
     * @throws UnsupportedOperationException when the database does not have support to save asynchronous
     */
    default <T> void saveAsync(Iterable<T> entities, TTL ttl) {
        Objects.requireNonNull(entities, "entities is required");
        Objects.requireNonNull(ttl, "ttl is required");
        StreamSupport.stream(entities.spliterator(), false).forEach(d -> saveAsync(d, ttl));
    }

    /**
     * Saves an entity asynchronously
     *
     * @param entity   entity to be saved
     * @param callBack the callback, when the process is finished will call this instance returning
     *                 the saved entity within parameters
     * @throws ExecuteAsyncQueryException    when there is a async error
     * @throws UnsupportedOperationException when the database does not have support to save asynchronous
     */
    <T> void saveAsync(T entity, Consumer<T> callBack) throws
            ExecuteAsyncQueryException, UnsupportedOperationException;

    /**
     * Saves an entity asynchronously with time to live
     *
     * @param entity   entity to be saved
     * @param ttl      time to live
     * @param callBack the callback, when the process is finished will call this instance returning
     *                 the saved entity within parameters
     * @throws ExecuteAsyncQueryException    when there is a async error
     * @throws UnsupportedOperationException when the database does not have support to save asynchronous
     */
    <T> void saveAsync(T entity, TTL ttl, Consumer<T> callBack) throws
            ExecuteAsyncQueryException, UnsupportedOperationException;

    /**
     * Updates a entity
     *
     * @param entity entity to be updated
     * @return the entity updated
     */
    <T> T update(T entity);

    /**
     * Updates an entity asynchronously
     *
     * @param entity entity to be updated
     * @throws ExecuteAsyncQueryException    when there is a async error
     * @throws UnsupportedOperationException when the database does not have support to save asynchronous
     */
    <T> void updateAsync(T entity) throws ExecuteAsyncQueryException, UnsupportedOperationException;

    /**
     * Updates an entity asynchronously
     *
     * @param entity   entity to be updated
     * @param callBack the callback, when the process is finished will call this instance returning
     *                 the updated entity within parametersa
     * @throws ExecuteAsyncQueryException    when there is a async error
     * @throws UnsupportedOperationException when the database does not have support to save asynchronous
     */
    <T> void updateAsync(T entity, Consumer<T> callBack) throws
            ExecuteAsyncQueryException, UnsupportedOperationException;

    /**
     * Deletes an entity
     *
     * @param query query to delete an entity
     */
     void delete(DocumentQuery query);

    /**
     * Deletes an entity asynchronously
     *
     * @param query query to delete an entity
     * @throws ExecuteAsyncQueryException    when there is a async error
     * @throws UnsupportedOperationException when the database does not have support to save asynchronous
     */
    void deleteAsync(DocumentQuery query) throws ExecuteAsyncQueryException, UnsupportedOperationException;

    /**
     * Deletes an entity asynchronously
     *
     * @param query    query to delete an entity
     * @param callBack the callback, when the process is finished will call this instance returning
     *                 the null within parameters
     * @throws ExecuteAsyncQueryException    when there is a async error
     * @throws UnsupportedOperationException when the database does not have support to delete asynchronous
     */
    void deleteAsync(DocumentQuery query, Consumer<Void> callBack) throws ExecuteAsyncQueryException,
            UnsupportedOperationException;

    /**
     * Finds {@link DocumentCollectionEntity} from query
     *
     * @param query - query to figure out entities
     * @return entities found by query
     * @throws NullPointerException when query is null
     */
    <T> List<T> find(DocumentQuery query) throws NullPointerException;

    /**
     * Returns a single entity from query
     *
     * @param query - query to figure out entities
     * @return an entity on {@link Optional} or {@link Optional#empty()} when the result is not found.
     * @throws NonUniqueResultException when the result has more than 1 entity
     * @throws NullPointerException  when query is null
     */
    default<T>  Optional<T> singleResult(DocumentQuery query) throws NonUniqueResultException {
        List<T> entities = find(query);
        if (entities.isEmpty()) {
            return Optional.empty();
        }
        if (entities.size() == 1) {
            return Optional.of(entities.get(0));
        }

        throw new NonUniqueResultException("The query returns more than one entity, query: " + query);
    }

    /**
     * Finds {@link DocumentCollectionEntity} from query asynchronously
     *
     * @param query    query to find entities
     * @param callBack the callback, when the process is finished will call this instance returning
     *                 the result of query within parameters
     * @throws ExecuteAsyncQueryException    when there is a async error
     * @throws UnsupportedOperationException when the database does not have support to save asynchronous
     */
    <T> void findAsync(DocumentQuery query, Consumer<List<T>> callBack) throws
            ExecuteAsyncQueryException, UnsupportedOperationException;

    /**
     * Executes a native query from database, this query may be difference between kind of database.
     *
     * @param query query to be executed
     * @return the result of query
     * @throws UnsupportedOperationException when the database does not have support to run native query
     */
    <T> List<T> nativeQuery(String query) throws UnsupportedOperationException;

    /**
     * Executes a native query from database, this query may be difference between kind of database and run it
     * asynchronously.
     *
     * @param query    query to be executed
     * @param callBack the callback, when the process is finished will call this instance returning
     *                 the result of query within parameters
     * @throws ExecuteAsyncQueryException    when there is a async error
     * @throws UnsupportedOperationException when the database does not have support to run native query async.
     */
    <T> void nativeQueryAsync(String query, Consumer<List<T>> callBack) throws
            ExecuteAsyncQueryException, UnsupportedOperationException;

}
