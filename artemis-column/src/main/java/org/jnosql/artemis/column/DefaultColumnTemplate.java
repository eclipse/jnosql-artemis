/*
 *  Copyright (c) 2017 Otávio Santana and others
 *   All rights reserved. This program and the accompanying materials
 *   are made available under the terms of the Eclipse Public License v1.0
 *   and Apache License v2.0 which accompanies this distribution.
 *   The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 *   and the Apache License v2.0 is available at http://www.opensource.org/licenses/apache2.0.php.
 *
 *   You may elect to redistribute this code under either of these licenses.
 *
 *   Contributors:
 *
 *   Otavio Santana
 */
package org.jnosql.artemis.column;

import org.jnosql.artemis.reflection.ClassRepresentations;
import org.jnosql.diana.api.column.ColumnFamilyManager;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

/**
 * The default implementation of {@link ColumnTemplate}
 */
@SuppressWarnings("unchecked")
class DefaultColumnTemplate extends AbstractColumnTemplate {

    private ColumnEntityConverter converter;

    private Instance<ColumnFamilyManager> manager;

    private ColumnWorkflow flow;

    private ColumnEventPersistManager eventManager;

    private ClassRepresentations classRepresentations;

    @Inject
    DefaultColumnTemplate(ColumnEntityConverter converter, Instance<ColumnFamilyManager> manager,
                          ColumnWorkflow flow,
                          ColumnEventPersistManager eventManager,
                          ClassRepresentations classRepresentations) {
        this.converter = converter;
        this.manager = manager;
        this.flow = flow;
        this.eventManager = eventManager;
        this.classRepresentations = classRepresentations;
    }

    DefaultColumnTemplate() {
    }


    @Override
    protected ColumnEntityConverter getConverter() {
        return converter;
    }

    @Override
    protected ColumnFamilyManager getManager() {
        return manager.get();
    }

    @Override
    protected ColumnWorkflow getFlow() {
        return flow;
    }

    @Override
    protected ColumnEventPersistManager getEventManager() {
        return eventManager;
    }

    @Override
    protected ClassRepresentations getClassRepresentations() {
        return classRepresentations;
    }
}
