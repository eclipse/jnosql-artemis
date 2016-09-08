package org.jnosql.artemis.reflection;


import org.jnosql.diana.api.document.Document;

public final class FieldValue {

    private final Object value;

    private final FieldRepresentation field;


    public FieldValue(Object value, FieldRepresentation field) {
        this.value = value;
        this.field = field;
    }

    public Object getValue() {
        return value;
    }

    public FieldRepresentation getField() {
        return field;
    }

    public boolean isNotEmpty() {
        return value != null;
    }

    public Document toDocument() {
        return Document.of(field.getName(), value);
    }

}
