/*
 * Copyright 2017 Otavio Santana and others
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jnosql.artemis.key;

import org.jnosql.diana.api.key.BucketManager;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;


class DefaultKeyValueRepository extends AbstractKeyValueRepository {

    private KeyValueEntityConverter converter;

    private Instance<BucketManager> manager;


    private KeyValueWorkflow flow;

    @Inject
    DefaultKeyValueRepository(KeyValueEntityConverter converter, Instance<BucketManager> manager, KeyValueWorkflow flow) {
        this.converter = converter;
        this.manager = manager;
        this.flow = flow;
    }

    DefaultKeyValueRepository() {
    }

    @Override
    protected KeyValueEntityConverter getConverter() {
        return converter;
    }

    @Override
    protected BucketManager getManager() {
        return manager.get();
    }

    @Override
    protected KeyValueWorkflow getFlow() {
        return flow;
    }
}
