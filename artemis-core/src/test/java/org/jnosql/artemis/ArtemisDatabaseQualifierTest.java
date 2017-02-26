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

import org.junit.Test;

import static org.jnosql.artemis.DatabaseType.COLUMN;
import static org.jnosql.artemis.DatabaseType.DOCUMENT;
import static org.junit.Assert.assertEquals;


public class ArtemisDatabaseQualifierTest {

    @Test
    public void shouldReturnDefaultColumn() {
        ArtemisDatabaseQualifier qualifier = ArtemisDatabaseQualifier.ofColumn();
        assertEquals("", qualifier.provider());
        assertEquals(COLUMN, qualifier.value());
    }

    @Test
    public void shouldReturnColumnProvider() {
        String provider = "provider";
        ArtemisDatabaseQualifier qualifier = ArtemisDatabaseQualifier.ofColumn(provider);
        assertEquals(provider, qualifier.provider());
        assertEquals(COLUMN, qualifier.value());
    }

    @Test(expected = NullPointerException.class)
    public void shouldReturnErrorWhenColumnNull() {
        ArtemisDatabaseQualifier.ofColumn(null);
    }

    //
    @Test
    public void shouldReturnDefaultDocument() {
        ArtemisDatabaseQualifier qualifier = ArtemisDatabaseQualifier.ofDocument();
        assertEquals("", qualifier.provider());
        assertEquals(DOCUMENT, qualifier.value());
    }

    @Test
    public void shouldReturnDocumentProvider() {
        String provider = "provider";
        ArtemisDatabaseQualifier qualifier = ArtemisDatabaseQualifier.ofDocument(provider);
        assertEquals(provider, qualifier.provider());
        assertEquals(DOCUMENT, qualifier.value());
    }

    @Test(expected = NullPointerException.class)
    public void shouldReturnErrorWhenDocumentNull() {
        ArtemisDatabaseQualifier.ofDocument(null);
    }

}