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
package org.jnosql.artemis.key;


import org.jnosql.diana.api.Value;
import org.jnosql.diana.api.key.BucketManager;
import org.jnosql.diana.api.key.KeyValueEntity;

import java.time.Duration;
import java.util.Objects;
import java.util.Optional;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * This class provides a skeletal implementation of the {@link KeyValueTemplate} interface,
 * to minimize the effort required to implement this interface.
 */
public abstract class AbstractKeyValueTemplate implements KeyValueTemplate {

    protected abstract KeyValueEntityConverter getConverter();

    protected abstract BucketManager getManager();


    protected abstract KeyValueWorkflow getFlow();

    @Override
    public <T> T put(T entity) throws NullPointerException {
        UnaryOperator<KeyValueEntity<?>> putAction = k -> {
            getManager().put(k);
            return k;

        };
        return getFlow().flow(entity, putAction);
    }

    @Override
    public <T> T put(T entity, Duration ttl) throws NullPointerException, UnsupportedOperationException {
        UnaryOperator<KeyValueEntity<?>> putAction = k -> {
            getManager().put(k, ttl);
            return k;

        };
        return getFlow().flow(entity, putAction);
    }

    @Override
    public <K, T> Optional<T> get(K key, Class<T> clazz) throws NullPointerException {
        Optional<Value> value = getManager().get(key);
        return value.map(v -> getConverter().toEntity(clazz, v))
                .filter(Objects::nonNull);
    }

    @Override
    public <K, T> Iterable<T> get(Iterable<K> keys, Class<T> clazz) throws NullPointerException {
        return StreamSupport.stream(getManager()
                .get(keys).spliterator(), false)
                .map(v -> getConverter().toEntity(clazz, v))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public <K> void remove(K key) throws NullPointerException {
        getManager().remove(key);
    }

    @Override
    public <K> void remove(Iterable<K> keys) throws NullPointerException {
        getManager().remove(keys);
    }
}
