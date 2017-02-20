package org.jnosql.artemis.validation;


import org.jnosql.artemis.EntityPrePersist;

import javax.enterprise.event.Observes;
import javax.inject.Inject;

class EntityObserver {

    @Inject
    private ArtemisValidator validator;

    void validate(@Observes EntityPrePersist entity) {
        validator.validate(entity.getValue());
    }
}
