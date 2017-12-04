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


import org.jnosql.artemis.Repository;
import org.jnosql.artemis.document.DocumentTemplate;
import org.jnosql.artemis.reflection.ClassRepresentation;
import org.jnosql.diana.api.document.DocumentDeleteQuery;
import org.jnosql.diana.api.document.DocumentQuery;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import static org.jnosql.artemis.document.query.DocumentRepositoryType.getDeleteQuery;
import static org.jnosql.artemis.document.query.DocumentRepositoryType.getQuery;
import static org.jnosql.artemis.document.query.ReturnTypeConverterUtil.returnObject;
import static org.jnosql.diana.api.document.query.DocumentQueryBuilder.select;

/**
 * The template method to {@link org.jnosql.artemis.Repository} to Document
 *
 * @param <T> the class type
 */
public abstract class AbstractDocumentRepositoryProxy<T> implements InvocationHandler {


    protected abstract Repository getRepository();

    protected abstract DocumentQueryParser getQueryParser();

    protected abstract DocumentTemplate getTemplate();

    protected abstract DocumentQueryDeleteParser getDeleteParser();

    protected abstract ClassRepresentation getClassRepresentation();


    @Override
    public Object invoke(Object instance, Method method, Object[] args) throws Throwable {

        String methodName = method.getName();
        DocumentRepositoryType type = DocumentRepositoryType.of(method, args);
        Class<?> typeClass = getClassRepresentation().getClassInstance();

        switch (type) {
            case DEFAULT:
                return method.invoke(getRepository(), args);
            case FIND_BY:
                DocumentQuery query = getQueryParser().parse(methodName, args, getClassRepresentation());
                return returnObject(query, getTemplate(), typeClass, method);
            case FIND_ALL:
                return returnObject(select().from(getClassRepresentation().getName()).build(), getTemplate(),
                        typeClass, method);
            case DELETE_BY:
                getTemplate().delete(getDeleteParser().parse(methodName, args, getClassRepresentation()));
                return null;
            case QUERY:
                DocumentQuery documentQuery = getQuery(args).get();
                return returnObject(documentQuery, getTemplate(), typeClass, method);
            case QUERY_DELETE:
                DocumentDeleteQuery deleteQuery = getDeleteQuery(args).get();
                getTemplate().delete(deleteQuery);
                return Void.class;
            case OBJECT_METHOD:
                return method.invoke(this, args);
            default:
                return Void.class;
        }
    }
}
