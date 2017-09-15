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
package org.jnosql.artemis;

import javax.enterprise.util.Nonbinding;
import javax.inject.Qualifier;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
@Qualifier
/**
 * Expresses a dependency to a configuration
 */
public @interface ConfigurationUnit {

    /**
     * The name by which the econfiguration is to be accessed in the environment referencing context;
     * not needed when dependency injection is used.
     *
     * @return the name
     */
    @Nonbinding
    String name() default "";

    /**
     * The name of the persistence unit as defined in the persistence.xml file.
     *
     * @return the unit name
     */
    @Nonbinding
    String unitName() default "persistence.json";
}
