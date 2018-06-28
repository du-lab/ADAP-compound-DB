package org.dulab.adapcompounddb.models;

public enum RoleType{

    Admin("Administrator Role"),
    Normal("For Common User");


    private final String label;

    RoleType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}

