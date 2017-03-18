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

import java.math.BigDecimal;
import java.util.List;

public class PersonBuilder {

    private String name;

    private Integer age;

    private BigDecimal salary;

    private List<String> phones;

    public PersonBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public PersonBuilder withAge(Integer age) {
        this.age = age;
        return this;
    }

    public PersonBuilder withSalary(BigDecimal salary) {
        this.salary = salary;
        return this;
    }

    public PersonBuilder withPhones(List<String> phones) {
        this.phones = phones;
        return this;
    }

    public Person build() {
        return new Person(name, age, salary, phones);
    }
}