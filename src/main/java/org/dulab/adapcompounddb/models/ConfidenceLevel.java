package org.dulab.adapcompounddb.models;

public enum ConfidenceLevel implements EnumWithLabels {
    LEVEL_1A("1A. Highly confident match"),
    LEVEL_1B("1B. Highly confident match"),
    LEVEL_2("2. Confident match"),
    LEVEL_5("5. Precursor match");

    private final String label;

    ConfidenceLevel(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
