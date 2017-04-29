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

import javax.enterprise.inject.Vetoed;
import javax.inject.Inject;
import java.util.Objects;

class DefaultKeyValueRepositoryProducer implements KeyValueRepositoryProducer {

    @Inject
    private KeyValueEntityConverter converter;
    @Inject
    private KeyValueWorkflow flow;

    @Override
    public KeyValueTemplate get(BucketManager manager) throws NullPointerException {
        Objects.requireNonNull(manager, "manager is required");
        return new ProducerKeyValueTemplate(converter, flow, manager);
    }

    @Vetoed
    static class ProducerKeyValueTemplate extends AbstractKeyValueTemplate {

        private KeyValueEntityConverter converter;

        private KeyValueWorkflow flow;

        private BucketManager manager;

        ProducerKeyValueTemplate(KeyValueEntityConverter converter, KeyValueWorkflow flow, BucketManager manager) {
            this.converter = converter;
            this.flow = flow;
            this.manager = manager;
        }

        ProducerKeyValueTemplate() {
        }

        @Override
        protected KeyValueEntityConverter getConverter() {
            return converter;
        }

        @Override
        protected BucketManager getManager() {
            return manager;
        }

        @Override
        protected KeyValueWorkflow getFlow() {
            return flow;
        }
    }
}
