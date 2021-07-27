package org.dulab.adapcompounddb.models.enums;

public enum FileType implements EnumWithLabels {

    MSP("MSP: NIST text format of individual spectra", 10),
    CSV("CSV: Comma-separated values", 1);

    private final String label;
    private final int priority;

    FileType(String label, int priority) {
        this.label = label;
        this.priority = priority;
    }

    public String getLabel() {
        return label;
    }

    public int getPriority() {
        return priority;
    }
}
