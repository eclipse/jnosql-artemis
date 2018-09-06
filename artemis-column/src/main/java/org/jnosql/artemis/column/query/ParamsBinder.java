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
package org.jnosql.artemis.column.query;

import org.jnosql.artemis.Converters;
import org.jnosql.artemis.column.util.ConverterUtil;
import org.jnosql.artemis.reflection.ClassRepresentation;
import org.jnosql.artemis.reflection.FieldRepresentation;
import org.jnosql.query.Params;

import java.util.List;
import java.util.Optional;

class ParamsBinder {

    private final ClassRepresentation representation;

    private final Converters converters;


    ParamsBinder(ClassRepresentation representation, Converters converters) {
        this.representation = representation;
        this.converters = converters;
    }

    public void bind(Params params, Object[] args) {

        List<String> names = params.getNames();
        for (int index = 0; index < names.size(); index++) {
            String name = names.get(0);
            String fieldName = name.substring(0, name.lastIndexOf("_"));
            Optional<FieldRepresentation> field = this.representation.getFields().stream()
                    .filter(f -> f.getName().equals(fieldName)).findFirst();

            Object value;
            if (field.isPresent()) {
                value = ConverterUtil.getValue(args[index], converters, field.get());
            } else {
                value = args[index];
            }
            params.bind(name, value);
        }
    }
}
