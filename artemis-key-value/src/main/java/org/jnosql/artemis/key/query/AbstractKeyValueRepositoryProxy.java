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
package org.jnosql.artemis.key.query;

import org.jnosql.artemis.DynamicQueryException;
import org.jnosql.artemis.Param;
import org.jnosql.artemis.PreparedStatement;
import org.jnosql.artemis.Query;
import org.jnosql.artemis.Repository;
import org.jnosql.artemis.key.KeyValueTemplate;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public abstract class AbstractKeyValueRepositoryProxy<T> implements InvocationHandler {


    private static final List<Method> METHODS;

    static {
        METHODS = new ArrayList<>();
        METHODS.addAll(Arrays.asList(Object.class.getMethods()));
        METHODS.addAll(Arrays.asList(Repository.class.getMethods()));
    }


    protected abstract Repository getRepository();

    protected abstract KeyValueTemplate getTemplate();

    protected abstract Class<T> getEntityClass();

    @Override
    public Object invoke(Object instance, Method method, Object[] args) throws Throwable {

        if (METHODS.stream().anyMatch(method::equals)) {
            return method.invoke(getRepository(), args);
        }

        Optional<String> query = getQuery(method);
        if (query.isPresent()) {
            return executeQuery(method, args, query.get());
        } else {
            throw new DynamicQueryException("Key Value repository does not support query method");
        }
    }

    private Object executeQuery(Method method, Object[] args, String query) {
        Map<String, Object> params = getParams(method, args);
        List<T> entities;
        if (params.isEmpty()) {
            entities = getTemplate().query(query, getEntityClass());
        } else {
            PreparedStatement prepare = getTemplate().prepare(query, getEntityClass());
            params.entrySet().stream().forEach(e -> prepare.bind(e.getKey(), e.getValue()));
            entities = prepare.getResultList();
        }
        return ReturnTypeConverterUtil.returnObject(entities, getEntityClass(), method);
    }

    private Map<String, Object> getParams(Method method, Object[] args) {
        Map<String, Object> params = new HashMap<>();

        Parameter[] parameters = method.getParameters();
        for (int index = 0; index < parameters.length; index++) {
            Parameter parameter = parameters[index];
            Param param = parameter.getAnnotation(Param.class);
            if (Objects.nonNull(param)) {
                params.put(param.value(), args[index]);
            }
        }
        return params;
    }

    private Optional<String> getQuery(Method method) {
        return Optional.ofNullable(method.getAnnotation(Query.class)).map(Query::value);
    }
}
