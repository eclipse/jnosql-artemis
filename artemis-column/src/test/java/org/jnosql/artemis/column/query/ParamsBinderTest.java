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

import org.jnosql.aphrodite.antlr.method.SelectMethodFactory;
import org.jnosql.artemis.CDIExtension;
import org.jnosql.artemis.Converters;
import org.jnosql.artemis.model.Person;
import org.jnosql.artemis.reflection.ClassRepresentation;
import org.jnosql.artemis.reflection.ClassRepresentations;
import org.jnosql.diana.api.column.query.ColumnSelectQuery;
import org.jnosql.diana.api.column.query.SelectQueryConverter;
import org.jnosql.query.SelectQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.inject.Inject;

import java.lang.reflect.Method;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(CDIExtension.class)
class ParamsBinderTest {


    @Inject
    private ClassRepresentations representations;

    @Inject
    private Converters converters;

    private ParamsBinder paramsBinder;

    @BeforeEach
    public void setUp() {

    }

    @Test
    public void shouldConvert() throws NoSuchMethodException {

        Method method = PersonRepository.class.getMethod("findByAge");
        ClassRepresentation classRepresentation = representations.get(Person.class);
        RepositoryColumnObserverParser parser = new RepositoryColumnObserverParser(classRepresentation);
        paramsBinder = new ParamsBinder(classRepresentation, converters);

        SelectMethodFactory selectMethodFactory = SelectMethodFactory.get();
        SelectQuery selectQuery = selectMethodFactory.apply(method, classRepresentation.getName());
        SelectQueryConverter converter = SelectQueryConverter.get();
        ColumnSelectQuery columnSelectQuery = converter.apply(selectQuery, parser);

    }


    interface PersonRepository {

        List<Person> findByAge(Integer age);
    }


}