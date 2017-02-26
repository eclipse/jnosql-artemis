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
package org.jnosql.artemis;


import javax.enterprise.util.AnnotationLiteral;
import java.util.Objects;

import static org.jnosql.artemis.DatabaseType.COLUMN;
import static org.jnosql.artemis.DatabaseType.DOCUMENT;

/**
 * Utilitarian class to select the {@link ArtemisDatabase}
 */
public class ArtemisDatabaseQualifier extends AnnotationLiteral<ArtemisDatabase> implements ArtemisDatabase {

    private static final ArtemisDatabaseQualifier DEFAULT_DOCUMENT_PROVIDER = new ArtemisDatabaseQualifier("", DOCUMENT);

    private static final ArtemisDatabaseQualifier DEFAULT_COLUMN_PROVIDER = new ArtemisDatabaseQualifier("", COLUMN);

    private final String provider;

    private final DatabaseType type;

    ArtemisDatabaseQualifier(String provider, DatabaseType type) {
        this.provider = provider;
        this.type = type;
    }

    @Override
    public DatabaseType value() {
        return type;
    }

    @Override
    public String provider() {
        return provider;
    }


    /**
     * Returns the qualifier filter with document type {@link DatabaseType#DOCUMENT}
     * and the nosql provider default
     *
     * @return the default document provider
     */
    public static ArtemisDatabaseQualifier ofDocument() {
        return DEFAULT_DOCUMENT_PROVIDER;
    }

    /**
     * Returns the qualifier filter with document type {@link DatabaseType#DOCUMENT} and the
     * nosql provider defined
     *
     * @param provider the provider
     * @return the qualifier filter instance
     * @throws NullPointerException when provider is null
     */
    public static ArtemisDatabaseQualifier ofDocument(String provider) throws NullPointerException {
        return new ArtemisDatabaseQualifier(Objects.requireNonNull(provider, "provider is required"), DOCUMENT);
    }

    /**
     * Returns the qualifier filter with document type {@link DatabaseType#COLUMN}
     * and the nosql provider default
     *
     * @return the default column provider
     */
    public static ArtemisDatabaseQualifier ofColumn() {
        return DEFAULT_COLUMN_PROVIDER;
    }

    /**
     * Returns the qualifier filter with document type {@link DatabaseType#COLUMN} and the
     * nosql provider defined
     *
     * @param provider the provider
     * @return the qualifier filter instance
     * @throws NullPointerException when provider is null
     */
    public static ArtemisDatabaseQualifier ofColumn(String provider) throws NullPointerException {
        return new ArtemisDatabaseQualifier(Objects.requireNonNull(provider, "provider is required"), DOCUMENT);
    }
}