package org.jnosql.artemis.column;

import org.jnosql.artemis.reflection.ClassRepresentation;
import org.jnosql.artemis.reflection.ClassRepresentations;
import org.jnosql.diana.api.column.query.DefaultColumnQueryParser;

import java.util.Optional;

final class MapperColumnQueryParser extends DefaultColumnQueryParser {


    private final ClassRepresentations representations;

    MapperColumnQueryParser(ClassRepresentations representations) {
        this.representations = representations;
    }

    public String fireEntity(String entity) {
        Optional<ClassRepresentation> classRepresentation = getClassRepresentation(entity);
        return classRepresentation.map(ClassRepresentation::getName).orElse(entity);
    }

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
