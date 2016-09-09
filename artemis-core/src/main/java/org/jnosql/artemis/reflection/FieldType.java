package org.jnosql.artemis.reflection;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * enum that contains kinds of annotations to fields on java.
 */
public enum FieldType {
    LIST, SET, MAP, COLLECTION, DEFAULT;

    /**
     * find you the kind of annotation on field and then define a enum type, follow the sequences:
     * <ul>
     * <li>List</li>
     * <li>Set</li>
     * <li>Map</li>
     * <li>Collection</li>
     * </ul>.
     *
     * @param field - the field with annotation
     * @return the type
     */
    public static FieldType of(Field field) {
        if (List.class.isAssignableFrom(field.getType())) {
            return LIST;
        }
        if (Set.class.isAssignableFrom(field.getType())) {
            return SET;
        }
        if (Collection.class.isAssignableFrom(field.getType())) {
            return COLLECTION;
        }
        if (Map.class.isAssignableFrom(field.getType())) {
            return MAP;
        }

        return DEFAULT;
    }

}
