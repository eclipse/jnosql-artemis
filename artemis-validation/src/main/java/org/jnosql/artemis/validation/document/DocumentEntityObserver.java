package org.jnosql.artemis.validation.document;


import org.jnosql.artemis.column.ColumnEntityPrePersist;

import javax.enterprise.event.Observes;
public interface DocumentEntityObserver {

    void validate(@Observes ColumnEntityPrePersist entity);
}
