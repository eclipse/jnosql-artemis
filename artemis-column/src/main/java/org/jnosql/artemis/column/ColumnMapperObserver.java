package org.jnosql.artemis.column;

import org.jnosql.artemis.reflection.ClassRepresentation;
import org.jnosql.artemis.reflection.ClassRepresentations;
import org.jnosql.diana.api.column.ColumnObserverParser;

import java.util.Optional;

final class ColumnMapperObserver implements ColumnObserverParser {


    private final ClassRepresentations representations;

    ColumnMapperObserver(ClassRepresentations representations) {
        this.representations = representations;
    }


    @Override
    public String fireEntity(String entity) {
        Optional<ClassRepresentation> classRepresentation = getClassRepresentation(entity);
        return classRepresentation.map(ClassRepresentation::getName).orElse(entity);
    }

    @Override
    public String fireField(String entity, String field) {
        Optional<ClassRepresentation> classRepresentation = getClassRepresentation(entity);
        return classRepresentation.map(c -> c.getColumnField(field)).orElse(field);
    }

    private Optional<ClassRepresentation> getClassRepresentation(String entity) {
        Optional<ClassRepresentation> bySimpleName = representations.findBySimpleName(entity);
        if (bySimpleName.isPresent()) {
            return bySimpleName;
        }
        Optional<ClassRepresentation> byClassName = representations.findByClassName(entity);
        if (byClassName.isPresent()) {
            return byClassName;
        }
        return Optional.empty();
    }

}
