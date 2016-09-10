/*
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
package org.jnosql.artemis.reflection;


import java.io.Serializable;
import java.lang.reflect.Field;
import org.jnosql.diana.api.Value;

/**
 * This class represents the information from {@link Field}.
 * The strategy is do cache in all fields in a class to either read and writer faster from Field
 */
public interface FieldRepresentation extends Serializable {

    /**
     * Return the type of the field
     *
     * @return the {@link FieldType}
     */
    FieldType getType();

    /**
     * The {@link Field}
     *
     * @return the field
     */
    Field getField();

    /**
     * Returns the name of the field that can be eiher the field name
     * or {@link org.jnosql.artemis.Column#value()}
     *
     * @return
     */
    String getName();


    /**
     * Returns the object from the field type
     *
     * @param value       the value {@link Value}
     * @param reflections the entity to do reflection
     * @return the instance from the field type
     */
    Object getValue(Value value, Reflections reflections);

    /**
     * Creates the FieldRepresentationBuilder
     *
     * @return a new Builder instance
     */
    static FieldRepresentationBuilder builder() {
        return new FieldRepresentationBuilder();
    }

}
