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
package org.jnosql.artemis;

import javax.enterprise.inject.spi.ProcessProducer;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Class util for check some data during CDI Extensions for Artemis
 */
public class Databases {

    private static final Logger LOGGER = Logger.getLogger(Databases.class.getName());

    private Databases() {}

    /**
     * This method get the class from ProcessProduce, check if the object is a valid object for the database type
     * add it for a list passed for CDI's lifecycle.
     *
     * @param processProducer the {@link ProcessProducer} of CDI Exntesion
     * @param type type data which extension is scanning
     * @param databases list of objects which will be used by Artemis CDI Extension
     *
     * @see DatabaseType
     *
     */
    public static void addDatabase(final ProcessProducer processProducer, final DatabaseType type, final List<Database> databases) {
        LOGGER.info(String.format("Found the type %s to databases %s", type, databases));
        Set<Annotation> annotations = processProducer.getAnnotatedMember().getAnnotations();
        Optional<Database> databaseOptional = annotations.stream().filter(a -> a instanceof Database)
                .map(Database.class::cast).findFirst();
        if (databaseOptional.isPresent()) {
            Database database = databaseOptional.get();
            if (!type.equals(database.value())) {
                String simpleName = processProducer.getAnnotatedMember().getDeclaringType().getJavaClass().getSimpleName();
                throw new IllegalStateException(String.format("The %s is producing a wrong manager for %s type", simpleName, type));
            }
            databases.add(database);
        }
    }
}
