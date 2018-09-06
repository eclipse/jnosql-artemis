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
import org.jnosql.diana.api.TypeReference;
import org.jnosql.diana.api.TypeSupplier;
import org.jnosql.diana.api.Value;
import org.jnosql.diana.api.column.Column;
import org.jnosql.diana.api.column.ColumnCondition;
import org.jnosql.diana.api.column.ColumnQuery;
import org.jnosql.diana.api.column.query.ColumnSelectQuery;
import org.jnosql.diana.api.column.query.SelectQueryConverter;
import org.jnosql.query.Params;
import org.jnosql.query.SelectQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.inject.Inject;

import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;

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
    public void shouldConvert() {

        Method method = PersonRepository.class.getMethods()[0];
        ClassRepresentation classRepresentation = representations.get(Person.class);
        RepositoryColumnObserverParser parser = new RepositoryColumnObserverParser(classRepresentation);
        paramsBinder = new ParamsBinder(classRepresentation, converters);

        SelectMethodFactory selectMethodFactory = SelectMethodFactory.get();
        SelectQuery selectQuery = selectMethodFactory.apply(method, classRepresentation.getName());
        SelectQueryConverter converter = SelectQueryConverter.get();
        ColumnSelectQuery columnSelectQuery = converter.apply(selectQuery, parser);
        Params params = columnSelectQuery.getParams();
        paramsBinder.bind(params, new Object[]{10});
        ColumnQuery query = columnSelectQuery.getQuery();
        ColumnCondition columnCondition = query.getCondition().get();
        Value value = columnCondition.getColumn().getValue();
        assertEquals(10, value.get());

    }

    @Test
    public void shouldConvert2() {

        Method method = PersonRepository.class.getMethods()[1];
        ClassRepresentation classRepresentation = representations.get(Person.class);
        RepositoryColumnObserverParser parser = new RepositoryColumnObserverParser(classRepresentation);
        paramsBinder = new ParamsBinder(classRepresentation, converters);

        SelectMethodFactory selectMethodFactory = SelectMethodFactory.get();
        SelectQuery selectQuery = selectMethodFactory.apply(method, classRepresentation.getName());
        SelectQueryConverter converter = SelectQueryConverter.get();
        ColumnSelectQuery columnSelectQuery = converter.apply(selectQuery, parser);
        Params params = columnSelectQuery.getParams();
        paramsBinder.bind(params, new Object[]{10L, "Ada"});
        ColumnQuery query = columnSelectQuery.getQuery();
        ColumnCondition columnCondition = query.getCondition().get();
        List<ColumnCondition> conditions = columnCondition.getColumn().get(new TypeReference<List<ColumnCondition>>() {
        });
        List<Object> values = conditions.stream().map(ColumnCondition::getColumn)
                .map(Column::getValue)
                .map(Value::get).collect(Collectors.toList());
        assertEquals(10, values.get(0));
        assertEquals("Ada", values.get(1));

    }


    interface PersonRepository {

        List<Person> findByAge(Integer age);

        List<Person> findByAgeAndName(Long age, String name);
    }


}