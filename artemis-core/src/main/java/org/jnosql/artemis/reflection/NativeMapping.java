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
package org.jnosql.artemis.reflection;

import java.util.Objects;

class NativeMapping {

    private final String nativeField;

    private final FieldRepresentation fieldRepresentation;

    NativeMapping(String nativeField, FieldRepresentation fieldRepresentation) {
        this.nativeField = nativeField;
        this.fieldRepresentation = fieldRepresentation;
    }

    public String getNativeField() {
        return nativeField;
    }

    public FieldRepresentation getFieldRepresentation() {
        return fieldRepresentation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        NativeMapping that = (NativeMapping) o;
        return Objects.equals(nativeField, that.nativeField) &&
                Objects.equals(fieldRepresentation, that.fieldRepresentation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nativeField, fieldRepresentation);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("NativeMapping{");
        sb.append("nativeField='").append(nativeField).append('\'');
        sb.append(", fieldRepresentation=").append(fieldRepresentation);
        sb.append('}');
        return sb.toString();
    }

    public static NativeMapping of(String nativeField, FieldRepresentation field) {
        return new NativeMapping(nativeField, field);
    }
}
