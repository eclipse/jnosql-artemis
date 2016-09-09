package org.jnosql.artemis.document;


import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import org.jnosql.artemis.EntityPostPersit;
import org.jnosql.artemis.EntityPrePersist;
import org.jnosql.diana.api.document.DocumentCollectionEntity;

@ApplicationScoped
class DefaultDocumentPersistManager implements DocumentPersistManager {

    @Inject
    private Event<DocumentEntityPrePersist> documentEntityPrePersistEvent;

    @Inject
    private Event<DocumentEntityPostPersist> documentEntityPostPersistEvent;

    @Inject
    private Event<EntityPrePersist> entityPrePersistEvent;

    @Inject
    private Event<EntityPostPersit> entityPostPersitEvent;

    @Override
    public void firePreDocument(DocumentCollectionEntity entity) {
        documentEntityPrePersistEvent.fire(DocumentEntityPrePersist.of(entity));
    }

    @Override
    public void firePostDocument(DocumentCollectionEntity entity) {
        documentEntityPostPersistEvent.fire(DocumentEntityPostPersist.of(entity));
    }

    @Override
    public <T> void firePreEntity(T entity) {
        entityPrePersistEvent.fire(EntityPrePersist.of(entity));
    }

    @Override
    public <T> void firePostEntity(T entity) {
        entityPostPersitEvent.fire(EntityPostPersit.of(entity));
    }
}
