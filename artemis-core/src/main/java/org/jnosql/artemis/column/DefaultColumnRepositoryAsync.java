package org.jnosql.artemis.column;


import org.jnosql.diana.api.ExecuteAsyncQueryException;
import org.jnosql.diana.api.column.ColumnEntity;
import org.jnosql.diana.api.column.ColumnFamilyManagerAsync;
import org.jnosql.diana.api.column.ColumnQuery;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.time.Duration;
import java.util.List;
import java.util.function.Consumer;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;

/**
 * The default implementation of {@link ColumnRepositoryAsync}
 */
@SuppressWarnings("unchecked")
@ApplicationScoped
@ColumnRepositoryInterceptor
class DefaultColumnRepositoryAsync implements ColumnRepositoryAsync {

    private ColumnEntityConverter converter;

    private Instance<ColumnFamilyManagerAsync> manager;


    @Inject
    DefaultColumnRepositoryAsync(ColumnEntityConverter converter, Instance<ColumnFamilyManagerAsync> manager) {
        this.converter = converter;
        this.manager = manager;
    }

    DefaultColumnRepositoryAsync() {
    }

    @Override
    public <T> void save(T entity) throws ExecuteAsyncQueryException, UnsupportedOperationException, NullPointerException {
        save(entity, t -> {
        });
    }

    @Override
    public <T> void save(T entity, Duration ttl) throws ExecuteAsyncQueryException, UnsupportedOperationException, NullPointerException {
        save(entity, ttl, t -> {
        });
    }

    @Override
    public <T> void save(T entity, Consumer<T> callBack) throws ExecuteAsyncQueryException, UnsupportedOperationException, NullPointerException {
        requireNonNull(entity, "entity is required");
        requireNonNull(callBack, "callBack is required");
        Consumer<ColumnEntity> dianaCallBack = c -> callBack.accept((T) converter.toEntity(entity.getClass(), c));
        manager.get().save(converter.toColumn(entity), dianaCallBack);
    }

    @Override
    public <T> void save(T entity, Duration ttl, Consumer<T> callBack) throws ExecuteAsyncQueryException, UnsupportedOperationException, NullPointerException {
        requireNonNull(entity, "entity is required");
        requireNonNull(ttl, "ttl is required");
        requireNonNull(callBack, "callBack is required");
        Consumer<ColumnEntity> dianaCallBack = c -> callBack.accept((T) converter.toEntity(entity.getClass(), c));
        manager.get().save(converter.toColumn(entity), ttl, dianaCallBack);
    }

    @Override
    public <T> void update(T entity) throws ExecuteAsyncQueryException, UnsupportedOperationException, NullPointerException {
        requireNonNull(entity, "entity is required");
        update(entity, t -> {
        });
    }

    @Override
    public <T> void update(T entity, Consumer<T> callBack) throws ExecuteAsyncQueryException, UnsupportedOperationException, NullPointerException {
        requireNonNull(entity, "entity is required");
        requireNonNull(callBack, "callBack is required");
        Consumer<ColumnEntity> dianaCallBack = c -> callBack.accept((T) converter.toEntity(entity.getClass(), c));
        manager.get().update(converter.toColumn(entity), dianaCallBack);
    }

    @Override
    public void delete(ColumnQuery query) throws ExecuteAsyncQueryException, UnsupportedOperationException, NullPointerException {
        requireNonNull(query, "query is required");
        manager.get().delete(query);
    }

    @Override
    public void delete(ColumnQuery query, Consumer<Void> callBack) throws ExecuteAsyncQueryException, UnsupportedOperationException, NullPointerException {
        requireNonNull(query, "query is required");
        requireNonNull(callBack, "callBack is required");
        manager.get().delete(query);
    }

    @Override
    public <T> void find(ColumnQuery query, Consumer<List<T>> callBack) throws ExecuteAsyncQueryException, UnsupportedOperationException, NullPointerException {
        requireNonNull(query, "query is required");
        requireNonNull(callBack, "callBack is required");

        Consumer<List<ColumnEntity>> dianaCallBack = d -> {
            callBack.accept(
                    d.stream()
                            .map(converter::toEntity)
                            .map(o -> (T) o)
                            .collect(toList()));
        };
        manager.get().find(query, dianaCallBack);
    }
}
