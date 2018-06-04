package org.jnosql.artemis.document;

import org.jnosql.artemis.Repository;
import org.jnosql.artemis.RepositoryAsync;
import org.jnosql.diana.api.document.DocumentCollectionManagerAsync;

/**
 * The producer of {@link RepositoryAsync}
 *
 */
public interface DocumentRepositoryAsyncProducer {


    /**
     * Produces a Repository class from repository class and {@link DocumentCollectionManagerAsync}
     * @param repositoryClass the repository class
     * @param manager the manager
     * @param <E> the entity of repository
     * @param <ID> the ID of the entity
     * @param <T> the repository type
     * @return a {@link Repository} interface
     * @throws NullPointerException when there is null parameter
     */
    <E,ID, T extends RepositoryAsync<E,ID>> T get(Class<T> repositoryClass, DocumentCollectionManagerAsync manager);

    /**
     * Produces a Repository class from repository class and {@link DocumentTemplateAsync}
     * @param repositoryClass the repository class
     * @param template the template
     * @param <E> the entity of repository
     * @param <ID> the ID of the entity
     * @param <T> the repository type
     * @return a {@link Repository} interface
     * @throws NullPointerException when there is null parameter
     */
    <E,ID, T extends RepositoryAsync<E,ID>> T get(Class<T> repositoryClass, DocumentTemplateAsync template);

}
