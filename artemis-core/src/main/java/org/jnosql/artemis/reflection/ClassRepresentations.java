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


/**
 * This class contains all the class in cached way to be used inside artemis.
 */
public interface ClassRepresentations {

    /**
     * Find a class in the cached way and return in a class representation,
     * if it's not found the class will be both, loaded and cached, when this method is called
     *
     * @param classEntity the class of entity
     * @return the class representation
     * @throws NullPointerException whend class entity is null
     */
    ClassRepresentation get(Class classEntity) throws NullPointerException;

    /**
     * Returns the {@link ClassRepresentation} instance from {@link ClassRepresentation#getName()} in ignore case
     *
     * @param name the name to find ah {@link ClassRepresentation} instance
     * @return the {@link ClassRepresentation} from name
     * @throws ClassInformationNotFoundException when the class is not loaded
     */
    ClassRepresentation findByName(String name) throws ClassInformationNotFoundException;

}
