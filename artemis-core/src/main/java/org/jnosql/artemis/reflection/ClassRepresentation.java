package org.jnosql.artemis.reflection;

import java.io.Serializable;
import java.util.List;

public class ClassRepresentation implements Serializable {

    private String name;

    private List<String> fieldsName;

    private Class<?> classInstance;


}
