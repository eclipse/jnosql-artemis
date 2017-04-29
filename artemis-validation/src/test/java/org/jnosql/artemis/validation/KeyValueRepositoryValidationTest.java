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

import org.jnosql.artemis.key.KeyValueTemplate;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.math.BigDecimal;
import java.util.Set;

import static java.util.Collections.singletonList;

@RunWith(WeldJUnit4Runner.class)
public class KeyValueRepositoryValidationTest {

    @Inject
    private KeyValueTemplate repository;


    @Test
    public void shouldValidate() {

        Person person = Person.builder()
                .withAge(21)
                .withName("Ada")
                .withSalary(BigDecimal.ONE)
                .withPhones(singletonList("123131231"))
                .build();
        repository.put(person);
    }

    @Test(expected = ConstraintViolationException.class)
    public void shouldReturnValidationException() {
        Person person = Person.builder()
                .withAge(10)
                .withName("Ada")
                .withSalary(BigDecimal.ONE)
                .withPhones(singletonList("123131231"))
                .build();
        repository.put(person);
    }


    @Test
    public void shouldGetValidations() {

        Person person = Person.builder()
                .withAge(10)
                .withName("Ada")
                .withSalary(BigDecimal.valueOf(12991))
                .withPhones(singletonList("123131231"))
                .build();
        try {
            repository.put(person);
        } catch (ConstraintViolationException ex) {
            Set<ConstraintViolation<?>> violations = ex.getConstraintViolations();
            Assert.assertEquals(2, violations.size());
        }

    }
}
