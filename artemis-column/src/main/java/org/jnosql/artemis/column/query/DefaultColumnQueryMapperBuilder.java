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
package org.jnosql.artemis.column.query;

import org.jnosql.artemis.Converters;
import org.jnosql.artemis.column.ColumnQueryMapperBuilder;
import org.jnosql.artemis.reflection.ClassRepresentation;
import org.jnosql.artemis.reflection.ClassRepresentations;
import org.jnosql.diana.api.column.query.ColumnFrom;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import static java.util.Objects.requireNonNull;

@ApplicationScoped
class DefaultColumnQueryMapperBuilder implements ColumnQueryMapperBuilder {

    @Inject
    private Instance<ClassRepresentations> classRepresentations;

    @Inject
    private Instance<Converters> converters;

    @Override
    public <T> ColumnFrom selectFrom(Class<T> entityClass) throws NullPointerException {
        requireNonNull(entityClass, "entity is required");
        ClassRepresentation representation = classRepresentations.get().get(entityClass);
        return new DefaultMapperColumnFrom(representation, converters.get());
    }

    @Override
    public <T> ColumnFrom deleteFrom(Class<T> entityClass) throws NullPointerException {
        requireNonNull(entityClass, "entity is required");
        return null;
    }
}
