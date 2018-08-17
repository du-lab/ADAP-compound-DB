package org.dulab.adapcompounddb.models;

public enum FileType implements EnumWithLabels {

    MSP("MSP: NIST text format of individual spectra");

    private final String label;

    FileType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
