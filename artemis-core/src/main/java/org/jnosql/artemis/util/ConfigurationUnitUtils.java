/*
 *  Copyright (c) 2018 Otávio Santana and others
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
package org.jnosql.artemis.util;

import org.jnosql.artemis.ConfigurationUnit;

import javax.enterprise.inject.spi.Annotated;
import javax.enterprise.inject.spi.InjectionPoint;
import java.util.Optional;

import static java.util.Optional.ofNullable;

/**
 * An utilitarian class to {@link ConfigurationUnit}
 */
public final class ConfigurationUnitUtils {

    private ConfigurationUnitUtils() {
    }

    /**
     * Returns a {@link ConfigurationUnit} instance from the {@link Annotated} instance
     * @param injectionPoint the injectionPoint
     * @param annotated the annotated
     * @return an {@link Optional} with {@link ConfigurationUnit} otherwise an {@link Optional#empty()}
     * @throws NullPointerException when injectionPoint is null
     */
    public static Optional<ConfigurationUnit> getConfigurationUnit(InjectionPoint injectionPoint, Annotated annotated) {

        if (annotated == null) {
            return injectionPoint.getQualifiers().stream()
                    .filter(annotation -> ConfigurationUnit.class.equals(annotation.annotationType()))
                    .map(ConfigurationUnit.class::cast)
                    .findFirst();
        }
        return ofNullable(annotated.getAnnotation(ConfigurationUnit.class));
    }
}
