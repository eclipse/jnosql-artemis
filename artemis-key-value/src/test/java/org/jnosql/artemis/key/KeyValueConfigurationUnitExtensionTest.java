/*
 *  Copyright (c) 2019 Otávio Santana and others
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
package org.jnosql.artemis.key;

import org.jnosql.artemis.CDIExtension;
import org.jnosql.artemis.PersonRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.inject.Inject;

@ExtendWith(CDIExtension.class)
class KeyValueConfigurationUnitExtensionTest {



    @Inject
    private Amor instance;

    @Test
    public void shouldRepository() {
        System.out.println(instance);
        KeyRepositorySupplier<PersonRepository> repository = instance.getRepository();
        PersonRepository personRepository = repository.get();
        System.out.println(personRepository);
        //        Assertions.assertNotNull(repository);

    }


    public static class Instance {


    }
}