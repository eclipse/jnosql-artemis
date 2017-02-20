package org.jnosql.artemis.model;


import org.jnosql.artemis.Column;

public class Machine {

    @Column
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
