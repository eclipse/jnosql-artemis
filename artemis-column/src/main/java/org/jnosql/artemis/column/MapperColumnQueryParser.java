package org.jnosql.artemis.column;

import org.jnosql.artemis.reflection.ClassInformationNotFoundException;
import org.jnosql.artemis.reflection.ClassRepresentations;
import org.jnosql.diana.api.column.ColumnEntity;
import org.jnosql.diana.api.column.ColumnFamilyManager;
import org.jnosql.diana.api.column.ColumnPreparedStatement;
import org.jnosql.diana.api.column.ColumnQueryParser;

import java.util.List;

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
        try {
            representations.findBySimpleClassName(entity);
        } catch (ClassInformationNotFoundException e) {
            return entity;
        }
    }

    public String fireField(String entity, String field) {
        return field;
    }

}
