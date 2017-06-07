/*
 *  Copyright (c) 2017 Otávio Santana and others
 *   All rights reserved. This program and the accompanying materials
 *   are made available under the terms of the Eclipse Public License v1.0
 *   and Apache License v2.0 which accompanies this distribution.
 *   The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 *   and the Apache License v2.0 is available at http://www.opensource.org/licenses/apache2.0.php.
 *
 *   You may elect to redistribute this code under either of these licenses.
 *
 *   Contributors:
 *
 *   Otavio Santana
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
     * @param name the name to select ah {@link ClassRepresentation} instance
     * @return the {@link ClassRepresentation} from name
     * @throws ClassInformationNotFoundException when the class is not loaded
     */
    ClassRepresentation findByName(String name) throws ClassInformationNotFoundException;

}
