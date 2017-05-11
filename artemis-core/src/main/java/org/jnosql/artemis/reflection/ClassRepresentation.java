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
package org.jnosql.artemis.reflection;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * This class is a representation of {@link Class} in cached way
 */
public interface ClassRepresentation extends Serializable {


    /**
     * @return the Entity name
     */
    String getName();

    /**
     * @return the fields name
     */
    List<String> getFieldsName();

    /**
     * @return The class
     */
    Class<?> getClassInstance();

    /**
     * @return The fields from this class
     */
    List<FieldRepresentation> getFields();

    /**
     * @return The constructor
     */
    Constructor getConstructor();


    /**
     * Gets javaField from Column
     *
     * @param javaField the java field
     * @return the column name or column
     * @throws NullPointerException when javaField is null
     */
    String getColumnField(String javaField) throws NullPointerException;

    /**
     * Returns a Fields grouped by the name
     *
     * @return a {@link FieldRepresentation} grouped by
     * {@link FieldRepresentation#getName()}
     * @see FieldRepresentation#getName()
     */
    Map<String, FieldRepresentation> getFieldsGroupByName();


    /**
     * Returns the field that has {@link org.jnosql.artemis.Id} annotation
     *
     * @return the field with ID annotation
     */
    Optional<FieldRepresentation> getId();
}
