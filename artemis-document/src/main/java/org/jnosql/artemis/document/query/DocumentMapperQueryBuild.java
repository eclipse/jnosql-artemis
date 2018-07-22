package org.jnosql.artemis.document.query;

import org.jnosql.artemis.document.DocumentTemplate;
import org.jnosql.artemis.document.DocumentTemplateAsync;
import org.jnosql.diana.api.document.DocumentQuery;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;


/**
 * The last step to the build of {@link org.jnosql.diana.api.document.DocumentQuery}.
 * It either can return a new {@link org.jnosql.diana.api.document.DocumentQuery} instance or execute a query with
 * {@link DocumentTemplate}
 * and {@link DocumentTemplateAsync}
 */
public interface DocumentMapperQueryBuild {

    /**
     * Creates a new instance of {@link DocumentQuery}
     *
     * @return a new {@link DocumentQuery} instance
     */
    DocumentQuery build();

    /**
     * Executes {@link DocumentTemplate#select(DocumentQuery)}
     *
     * @param template the template to document
     * @return the result of {@link DocumentTemplate#select(DocumentQuery)}
     * @throws NullPointerException when manager is null
     */
    <T> List<T> execute(DocumentTemplate template);

    /**
     * Executes {@link DocumentTemplate#singleResult(DocumentQuery)}
     *
     * @param template the template to document
     * @return the result of {@link DocumentTemplate#singleResult(DocumentQuery)}
     * @throws NullPointerException when manager is null
     */
    <T> Optional<T> executeSingle(DocumentTemplate template);

    /**
     * Executes {@link DocumentTemplateAsync#select(DocumentQuery, Consumer)}
     *
     * @param templateAsync  the templateAsync
     * @param callback the callback
     * @throws NullPointerException when there is null parameter
     */
    <T> void execute(DocumentTemplateAsync templateAsync, Consumer<List<T>> callback);

    /**
     * Executes {@link DocumentTemplateAsync#singleResult(DocumentQuery, Consumer)}
     *
     * @param templateAsync  the templateAsync
     * @param callback the callback
     * @throws NullPointerException when there is null parameter
     */
    <T> void executeSingle(DocumentTemplateAsync templateAsync, Consumer<Optional<T>> callback);

}