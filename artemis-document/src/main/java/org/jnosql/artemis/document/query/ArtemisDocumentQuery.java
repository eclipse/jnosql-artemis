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

import org.jnosql.diana.api.Sort;
import org.jnosql.diana.api.document.DocumentCondition;
import org.jnosql.diana.api.document.DocumentQuery;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

class ArtemisDocumentQuery implements DocumentQuery {
    private final List<Sort> sorts;
    private final long limit;
    private final long start;
    private final DocumentCondition condition;
    private final String documentCollection;

    ArtemisDocumentQuery(List<Sort> sorts, long limit, long start, DocumentCondition condition, String documentCollection) {
        this.sorts = sorts;
        this.limit = limit;
        this.start = start;
        this.condition = condition;
        this.documentCollection = documentCollection;
    }

    @Override
    public long getMaxResults() {
        return limit;
    }

    @Override
    public long getFirstResult() {
        return start;
    }

    @Override
    public String getDocumentCollection() {
        return documentCollection;
    }

    @Override
    public Optional<DocumentCondition> getCondition() {
        return Optional.ofNullable(condition);
    }

    @Override
    public List<Sort> getSorts() {
        return sorts;
    }

    @Override
    public List<String> getDocuments() {
        return Collections.emptyList();
    }
}

