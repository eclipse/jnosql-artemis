/*
 * Copyright 2017 Otavio Santana and others
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jnosql.artemis;

import java.util.Objects;

/**
 * When an entity is either saved or updated it's the first event to fire
 */
public interface EntityPrePersist {

    /**
     * Return the entity whose gonna be either saved or updated
     *
     * @return Return the entity whose gonna be either save or update
     */
    Object getValue();

    /**
     * Created the default implementation of {@link EntityPrePersist}
     *
     * @param value the value
     * @return the new instance of {@link EntityPrePersist}
     * @throws NullPointerException when value is null
     */
    static EntityPrePersist of(Object value) throws NullPointerException {
        Objects.requireNonNull(value, "value is required");
        return new DefaultEntityPrePersist(value);
    }
}
