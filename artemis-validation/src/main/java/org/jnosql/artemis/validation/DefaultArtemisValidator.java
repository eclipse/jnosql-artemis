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
package org.jnosql.artemis.validation;


import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

class DefaultArtemisValidator implements ArtemisValidator {

    @Inject
    private Instance<ValidatorFactory> validatorFactories;

    @Inject
    private Instance<Validator> validators;

    public void validate(Object bean) {
        Validator validator = getValidator();
        Set<ConstraintViolation<Object>> violations = validator.validate(bean);

        if (!violations.isEmpty()) {
            throw new ArtemisValidationException(violations.stream().collect(toSet()));
        }

    }


    private Validator getValidator() {
        if (!validators.isUnsatisfied()) {
            return validators.get();
        } else if (!validatorFactories.isUnsatisfied()) {
            ValidatorFactory validatorFactory = validatorFactories.get();
            return validatorFactory.getValidator();
        } else {
            ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
            return factory.getValidator();
        }
    }
}
