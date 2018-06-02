package org.jnosql.artemis.column;

import org.jnosql.artemis.reflection.ClassInformationNotFoundException;
import org.jnosql.artemis.reflection.ClassRepresentation;
import org.jnosql.artemis.reflection.ClassRepresentations;
import org.jnosql.diana.api.column.ColumnEntity;
import org.jnosql.diana.api.column.ColumnFamilyManager;
import org.jnosql.diana.api.column.ColumnPreparedStatement;
import org.jnosql.diana.api.column.ColumnQueryParser;

import java.util.List;
import java.util.Optional;

final class MapperColumnQueryParser implements ColumnQueryParser {

    private final ColumnQueryParser parser = ColumnQueryParser.getParser();

    private final ClassRepresentations representations;

    MapperColumnQueryParser(ClassRepresentations representations) {
        this.representations = representations;
    }

    @Override
    public List<ColumnEntity> query(String query, ColumnFamilyManager manager) {
        return parser.query(query, manager);
    }

    @Override
    public ColumnPreparedStatement prepare(String query, ColumnFamilyManager manager) {
        return parser.prepare(query, manager);
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
