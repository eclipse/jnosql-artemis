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

/**
 * Class util for check some data during CDI Extensions for Artemis
 */
public class Databases {

    private Databases() {}

    /**
     * This method get the class from ProcessProduce, check if the object is a valid object for the database type
     * add it for a list passed for CDI's lifecycle.
     *
     * @param pp the {@link ProcessProducer} of CDI Exntesion
     * @param databaseType type data which extension is scanning
     * @param dataBaseList list of objects which will be used by Artemis CDI Extension
     *
     * @see DatabaseType
     *
     */
    public static void addDatabase(final ProcessProducer pp, final DatabaseType databaseType, final List<Database> dataBaseList) {
        Set<Annotation> annotations = pp.getAnnotatedMember().getAnnotations();
        Optional<Database> databaseOptional = annotations.stream().filter(a -> a instanceof Database)
                .map(Database.class::cast).findFirst();
        if (databaseOptional.isPresent()) {
            Database database = databaseOptional.get();
            if (!databaseType.equals(database.value())) {
                String simpleName = pp.getAnnotatedMember().getDeclaringType().getJavaClass().getSimpleName();
                throw new IllegalStateException(String.format("The %s is producing a wrong manager for %s type", simpleName, databaseType));
            }
            dataBaseList.add(database);
        }
    }
}
