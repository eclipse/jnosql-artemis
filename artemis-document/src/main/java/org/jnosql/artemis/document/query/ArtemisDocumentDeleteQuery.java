/*
 *  Copyright (c) 2017 Otávio Santana and others
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
package org.jnosql.artemis.document.query;

import org.jnosql.diana.api.document.DocumentCondition;
import org.jnosql.diana.api.document.DocumentDeleteQuery;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

class ArtemisDocumentDeleteQuery implements DocumentDeleteQuery {

    private final String documentCondition;

    private final DocumentCondition condition;

    ArtemisDocumentDeleteQuery(String documentCondition, DocumentCondition condition) {
        this.documentCondition = documentCondition;
        this.condition = condition;
    }

    @Override
    public String getDocumentCollection() {
        return documentCondition;
    }

    @Override
    public Optional<DocumentCondition> getCondition() {
        return Optional.ofNullable(condition);
    }

    @Override
    public List<String> getDocuments() {
        return Collections.emptyList();
    }
}
