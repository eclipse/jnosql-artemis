package org.jnosql.artemis.document.query;

import org.jnosql.artemis.Converters;
import org.jnosql.artemis.Repository;
import org.jnosql.artemis.document.DocumentRepositoryProducer;
import org.jnosql.artemis.document.DocumentTemplate;
import org.jnosql.artemis.document.DocumentTemplateProducer;
import org.jnosql.artemis.reflection.ClassRepresentations;
import org.jnosql.artemis.reflection.Reflections;
import org.jnosql.diana.api.document.DocumentCollectionManager;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.lang.reflect.Proxy;
import java.util.Objects;

@ApplicationScoped
class DefaultDocumentRepositoryProducer implements DocumentRepositoryProducer {

    @Inject
    private ClassRepresentations classRepresentations;
    @Inject
    private Reflections reflections;
    @Inject
    private Converters converters;
    @Inject
    private DocumentTemplateProducer producer;

    @Override
    public <E, ID, T extends Repository<E, ID>> T get(Class<T> repositoryClass, DocumentCollectionManager manager) {
        Objects.requireNonNull(repositoryClass, "repository class is required");
        Objects.requireNonNull(manager, "manager class is required");
        DocumentTemplate template = producer.get(manager);
        return get(repositoryClass, template);
    }

    @Override
    public <E, ID, T extends Repository<E, ID>> T get(Class<T> repositoryClass, DocumentTemplate template) {
        Objects.requireNonNull(repositoryClass, "repository class is required");
        Objects.requireNonNull(template, "template class is required");

        DocumentRepositoryProxy<T> handler = new DocumentRepositoryProxy<>(template,
                classRepresentations, repositoryClass, reflections, converters);
        return (T) Proxy.newProxyInstance(repositoryClass.getClassLoader(),
                new Class[]{repositoryClass},
                handler);
    }
}
