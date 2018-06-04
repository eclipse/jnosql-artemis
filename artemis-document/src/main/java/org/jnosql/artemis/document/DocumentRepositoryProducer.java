package org.jnosql.artemis.document;

import org.jnosql.artemis.Repository;
import org.jnosql.diana.api.document.DocumentCollectionManager;

/**
 * The producer of {@link Repository}
 *
 */
public interface DocumentRepositoryProducer {


    /**
     * Produces a Repository class from repository class and {@link DocumentCollectionManager}
     * @param repositoryClass the repository class
     * @param manager the manager
     * @param <E> the entity of repository
     * @param <ID> the ID of the entity
     * @param <T> the repository type
     * @return a {@link Repository} interface
     * @throws NullPointerException when there is null parameter
     */
    <E,ID, T extends Repository<E,ID>> T get(Class<T> repositoryClass, DocumentCollectionManager manager);

    /**
     * Produces a Repository class from repository class and {@link DocumentTemplate}
     * @param repositoryClass the repository class
     * @param template the template
     * @param <E> the entity of repository
     * @param <ID> the ID of the entity
     * @param <T> the repository type
     * @return a {@link Repository} interface
     * @throws NullPointerException when there is null parameter
     */
    <E,ID, T extends Repository<E,ID>> T get(Class<T> repositoryClass, DocumentTemplate template);

}
