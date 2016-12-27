package org.jnosql.artemis.key;

import org.jnosql.diana.api.Value;
import org.jnosql.diana.api.key.BucketManager;
import org.jnosql.diana.api.key.KeyValueEntity;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.time.Duration;
import java.util.Objects;
import java.util.Optional;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

class DefaultKeyValueCRUDOperation implements KeyValueCRUDOperation {

    private KeyValueEntityConverter converter;

    private Instance<BucketManager> manager;


    private KeyValueWorkflow flow;

    @Inject
    DefaultKeyValueCRUDOperation(KeyValueEntityConverter converter, Instance<BucketManager> manager, KeyValueWorkflow flow) {
        this.converter = converter;
        this.manager = manager;
        this.flow = flow;
    }

    DefaultKeyValueCRUDOperation() {
    }

    @Override
    public <T> T put(T entity) throws NullPointerException {
        UnaryOperator<KeyValueEntity<?>> putAction = k -> {
            manager.get().put(k);
            return k;

        };
        return flow.flow(entity, putAction);
    }

    @Override
    public <T> T put(T entity, Duration ttl) throws NullPointerException, UnsupportedOperationException {
        UnaryOperator<KeyValueEntity<?>> putAction = k -> {
            manager.get().put(k, ttl);
            return k;

        };
        return flow.flow(entity, putAction);
    }

    @Override
    public <K, T> Optional<T> get(K key, Class<T> clazz) throws NullPointerException {
        Optional<Value> value = manager.get().get(key);
        return value.map(v -> converter.toEntity(clazz, v))
                .filter(Objects::nonNull)
                .map(t -> Optional.ofNullable(t))
                .orElse(Optional.empty());
    }

    @Override
    public <K, T> Iterable<T> get(Iterable<K> keys, Class<T> clazz) throws NullPointerException {
        return StreamSupport.stream(manager.get()
                .get(keys).spliterator(), false)
                .map(v -> converter.toEntity(clazz, v))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public <K> void remove(K key) throws NullPointerException {
        manager.get().remove(key);
    }

    @Override
    public <K> void remove(Iterable<K> keys) throws NullPointerException {
        manager.get().remove(keys);
    }
}
