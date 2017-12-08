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
import org.jnosql.artemis.Repository;
import org.jnosql.artemis.key.KeyValueTemplate;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class AbstractKeyValueRepositoryProxy<T> implements InvocationHandler {


    private static final List<Method> METHODS;

    static{
        METHODS = new ArrayList<>();
        METHODS.addAll(Arrays.asList(Object.class.getMethods()));
        METHODS.addAll(Arrays.asList(Repository.class.getMethods()));
    }

    AbstractKeyValueRepositoryProxy(Class<?> repositoryType, KeyValueTemplate repository) {
        Class<T> typeClass = Class.class.cast(ParameterizedType.class.cast(repositoryType.getGenericInterfaces()[0])
                .getActualTypeArguments()[0]);
    }

    protected abstract Repository getRepository();

    @Override
    public Object invoke(Object o, Method method, Object[] args) throws Throwable {

        if(METHODS.stream().anyMatch(method::equals)) {
            return method.invoke(getRepository(), args);
        } else {
            throw new DynamicQueryException("Key Value repository does not support query method");
        }
    }
}
