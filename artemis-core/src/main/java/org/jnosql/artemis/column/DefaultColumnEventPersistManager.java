package org.jnosql.artemis.column;


import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import org.jnosql.artemis.EntityPostPersit;
import org.jnosql.artemis.EntityPrePersist;
import org.jnosql.diana.api.column.ColumnFamilyEntity;

@ApplicationScoped
class DefaultColumnEventPersistManager implements ColumnEventPersistManager {
    @Inject
    private Event<ColumnEntityPrePersist> documentEntityPrePersistEvent;

    @Inject
    private Event<ColumnEntityPostPersist> documentEntityPostPersistEvent;

    @Inject
    private Event<EntityPrePersist> entityPrePersistEvent;

    @Inject
    private Event<EntityPostPersit> entityPostPersitEvent;

    @Override
    public void firePreDocument(ColumnFamilyEntity entity) {
        documentEntityPrePersistEvent.fire(ColumnEntityPrePersist.of(entity));
    }

    @Override
    public void firePostDocument(ColumnFamilyEntity entity) {
        documentEntityPostPersistEvent.fire(ColumnEntityPostPersist.of(entity));
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
