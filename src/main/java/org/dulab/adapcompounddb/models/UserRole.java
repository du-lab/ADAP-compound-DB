package org.dulab.adapcompounddb.models;

public enum UserRole implements LabeledEnum {

	ADMIN("Administrator"),
    USER("Registered User");

    private final String label;

    UserRole(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
