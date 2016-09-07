package org.jnosql.artemis.reflection;

import java.lang.reflect.Field;

/**
 * enum that contains kinds of annotations to fields on java.
 */
public enum FieldType {
    ENUM, LIST, SET, MAP, COLLECTION, DEFAULT, EMBEDDED;

    /**
     * find you the kind of annotation on field and then define a enum type, follow the sequences:
     * <ul>
     * <li>Enum</li>
     * <li>List</li>
     * <li>Set</li>
     * <li>Map</li>
     * <li>Collection</li>
     * <li>Custom</li>
     * <li>Default - if there is not a annotation</li>
     * </ul>.
     *
     * @param field - the field with annotation
     * @return the type
     */
    public static FieldType getTypeByField(Field field) {

        return null;
    }

}
