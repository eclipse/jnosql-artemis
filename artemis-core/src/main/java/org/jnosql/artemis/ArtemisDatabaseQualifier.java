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

public class ArtemisDatabaseQualifier extends AnnotationLiteral<ArtemisDatabase> implements ArtemisDatabase {

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


    public static ArtemisDatabaseQualifier ofDocument() {
        return new ArtemisDatabaseQualifier("", DatabaseType.DOCUMENT);
    }

    public static ArtemisDatabaseQualifier ofDocument(String provider) throws NullPointerException {
        return new ArtemisDatabaseQualifier(Objects.requireNonNull(provider, "provider is required"), DatabaseType.DOCUMENT);
    }
}