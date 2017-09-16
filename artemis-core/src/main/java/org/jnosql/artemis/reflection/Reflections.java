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

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.List;

/**
 * Utilitarian class to reflection
 */
public interface Reflections {

    /**
     * Return The Object from the Column.
     *
     * @param object the object
     * @param field  the field to return object
     * @return - the field value in Object
     */
    Object getValue(Object object, Field field);

    /**
     * Set the field in the Object.
     *
     * @param object the object
     * @param field  the field to return object
     * @param value  the value to object
     * @return - if the operation was execute with success
     */
    boolean setValue(Object object, Field field, Object value);

    /**
     * Create new instance of this class.
     *
     * @param constructor the constructor
     * @param <T>         the instance type
     * @return the new instance that class
     */
    <T> T newInstance(Constructor constructor);


    /**
     * Create new instance of this class.
     *
     * @param clazz the clazz
     * @param <T>   the instance type
     * @return the new instance that class
     */
    <T> T newInstance(Class<T> clazz);

    /**
     * Find the Column from the name field.
     *
     * @param string the name of field
     * @param clazz  the class
     * @return the field from the name
     */
    Field getField(String string, Class<?> clazz);

    /**
     * returns the generic type of field.
     *
     * @param field the field
     * @return a generic type
     */
    Class<?> getGenericType(Field field);

    /**
     * return the key and value of field.
     *
     * @param field the field
     * @return the types of the type
     */
    KeyValueClass getGenericKeyValue(Field field);


    /**
     * Make the given field accessible, explicitly setting it accessible
     * if necessary. The setAccessible(true) method is only
     * called when actually necessary, to avoid unnecessary
     * conflicts with a JVM SecurityManager (if active).
     *
     * @param field field the field to make accessible
     */
    void makeAccessible(Field field);

    /**
     * Make the given a constructor class accessible, explicitly setting it accessible
     * if necessary. The setAccessible(true) method is only
     * called when actually necessary, to avoid unnecessary
     * conflicts with a JVM SecurityManager (if active).
     *
     * @param clazz the class constructor acessible
     * @throws ConstructorException when the constructor has public and default
     */
    Constructor makeAccessible(Class clazz) throws ConstructorException;

    String getEntityName(Class classEntity);

    List<Field> getFields(Class classEntity);

    boolean isMappedSuperclass(Class<?> classEntity);

    boolean isIdField(Field field);

    String getColumnName(Field field);

    String getIdName(Field field);

    /**
     * data structured to store key and value class to map collection.
     *
     * @author otaviojava
     */
    class KeyValueClass {
        private final Class<?> keyClass;
        private final Class<?> valueClass;

        KeyValueClass(Class<?> keyClass, Class<?> valueClass) {
            this.keyClass = keyClass;
            this.valueClass = valueClass;
        }

        public Class<?> getKeyClass() {
            return keyClass;
        }

        public Class<?> getValueClass() {
            return valueClass;
        }

    }


}
