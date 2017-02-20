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
package org.jnosql.artemis.validation;

import javax.validation.ConstraintViolation;
import javax.validation.ValidationException;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

/**
 * Base exception of all Bean Validation "unexpected" problems at Artemis.
 */
public class ArtemisValidationException extends ValidationException {

    private final Set<ConstraintViolation<?>> violations;


    /**
     * The creates a new exception
     *
     * @param violations the validation
     * @throws NullPointerException when violations is null
     */
    public ArtemisValidationException(Set<ConstraintViolation<?>> violations) throws NullPointerException {
        super(requireNonNull(violations, "validation is required")
                .stream()
                .map(c -> c.getPropertyPath() + ": " + c.getMessage())
                .collect(Collectors.joining("\n")));
        this.violations = violations;
    }



    /**
     * Getter of violations
     *
     * @return the violations
     */
    public Set<ConstraintViolation<?>> getViolations() {
        return violations;
    }

}
