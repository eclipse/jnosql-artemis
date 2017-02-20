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
package org.jnosql.artemis.key;

import org.jnosql.artemis.ArtemisException;

/**
 * When The Entity is converted to {@link org.jnosql.diana.api.key.KeyValueEntity},
 * this entity must have a field with {@link org.jnosql.artemis.Key} annotation. If this entity
 * hasn't this information an exception will be launch.
 */
public class KeyNotFoundException extends ArtemisException {

    /**
     * New exception instance with the exception message
     *
     * @param message the exception message
     */
    public KeyNotFoundException(String message) {
        super(message);
    }


    static KeyNotFoundException newInstance(Class<?> clazz) {
        String message = "The entity " + clazz + " must have a field annoted with @Key";
        return new KeyNotFoundException(message);
    }
}
