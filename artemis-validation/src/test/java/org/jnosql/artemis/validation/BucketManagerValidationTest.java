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
import org.jnosql.diana.api.key.BucketManager;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;

@RunWith(WeldJUnit4Runner.class)
public class BucketManagerValidationTest {

    @Inject
    private KeyValueRepository repository;


    @Test
    public void shouldValidate() {

        Person person = Person.builder()
                .withAge(10)
                .withName("Ada").build();
      repository.put(person);
    }
}
