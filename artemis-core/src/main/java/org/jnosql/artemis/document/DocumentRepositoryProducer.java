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
package org.jnosql.artemis.document;


import org.jnosql.diana.api.document.DocumentCollectionManager;


/**
 * The producer of {@link DocumentRepository}
 *
 * @param <T> the DocumentRepository instance
 */
public interface DocumentRepositoryProducer<T extends DocumentRepository> {

    /**
     * creates a {@link DocumentRepository}
     *
     * @param collectionManager the collectionManager
     * @return a new instance
     * @throws NullPointerException when collectionManager is null
     */
    T get(DocumentCollectionManager collectionManager) throws NullPointerException;

}
