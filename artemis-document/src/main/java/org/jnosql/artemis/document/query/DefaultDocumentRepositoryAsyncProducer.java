package org.jnosql.artemis.document.query;

import org.jnosql.artemis.Converters;
import org.jnosql.artemis.Repository;
import org.jnosql.artemis.RepositoryAsync;
import org.jnosql.artemis.document.DocumentRepositoryAsyncProducer;
import org.jnosql.artemis.document.DocumentRepositoryProducer;
import org.jnosql.artemis.document.DocumentTemplate;
import org.jnosql.artemis.document.DocumentTemplateAsync;
import org.jnosql.artemis.document.DocumentTemplateAsyncProducer;
import org.jnosql.artemis.document.DocumentTemplateProducer;
import org.jnosql.artemis.reflection.ClassRepresentations;
import org.jnosql.artemis.reflection.Reflections;
import org.jnosql.diana.api.document.DocumentCollectionManager;
import org.jnosql.diana.api.document.DocumentCollectionManagerAsync;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.lang.reflect.Proxy;
import java.util.Objects;

@ApplicationScoped
class DefaultDocumentRepositoryAsyncProducer implements DocumentRepositoryAsyncProducer {

    @Inject
    private ClassRepresentations classRepresentations;
    @Inject
    private Reflections reflections;
    @Inject
    private Converters converters;
    @Inject
    private DocumentTemplateAsyncProducer producer;

    @Override
    public <E, ID, T extends RepositoryAsync<E, ID>> T get(Class<T> repositoryClass, DocumentCollectionManagerAsync manager) {
        Objects.requireNonNull(repositoryClass, "repository class is required");
        Objects.requireNonNull(manager, "manager class is required");
        DocumentTemplateAsync template = producer.get(manager);
        return get(repositoryClass, template);
    }

    @Override
    public <E, ID, T extends RepositoryAsync<E, ID>> T get(Class<T> repositoryClass, DocumentTemplateAsync template) {
        Objects.requireNonNull(repositoryClass, "repository class is required");
        Objects.requireNonNull(template, "template class is required");

        DocumentRepositoryAsyncProxy<T> handler = new DocumentRepositoryAsyncProxy<>(template,
                classRepresentations, repositoryClass, reflections, converters);
        return (T) Proxy.newProxyInstance(repositoryClass.getClassLoader(),
                new Class[]{repositoryClass},
                handler);
    }
}
