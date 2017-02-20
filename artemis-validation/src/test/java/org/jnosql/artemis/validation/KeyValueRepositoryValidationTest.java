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

import org.jnosql.artemis.key.KeyValueRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import java.math.BigDecimal;
import java.util.Set;

import static java.util.Collections.singletonList;

@RunWith(WeldJUnit4Runner.class)
public class KeyValueRepositoryValidationTest {

    @Inject
    private KeyValueRepository repository;


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

    @Test(expected = ArtemisValidationException.class)
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
        } catch (ArtemisValidationException ex) {
            Set<ConstraintViolation<?>> violations = ex.getViolations();
            Assert.assertEquals(2, violations.size());
        }

    }
}
