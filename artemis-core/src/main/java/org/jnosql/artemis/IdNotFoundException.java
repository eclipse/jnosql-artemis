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
package org.jnosql.artemis;

/**
 * When The Entity is converted to {@link org.jnosql.diana.api.key.KeyValueEntity},
 * this entity must have a field with {@link org.jnosql.artemis.Id} annotation. If this entity
 * hasn't this information an exception will be launch.
 */
public class IdNotFoundException extends ArtemisException {

    /**
     * New exception instance with the exception message
     *
     * @param message the exception message
     */
    public IdNotFoundException(String message) {
        super(message);
    }


    static IdNotFoundException newInstance(Class<?> clazz) {
        String message = "The entity " + clazz + " must have a field annoted with @Id";
        return new IdNotFoundException(message);
    }
}
