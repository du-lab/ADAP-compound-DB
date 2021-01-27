package org.dulab.adapcompounddb.models.enums;

import org.dulab.adapcompounddb.models.enums.EnumWithLabels;

public enum FileType implements EnumWithLabels {

    MSP("MSP: NIST text format of individual spectra"),
    CSV("CSV: Comma-separated values");

    private final String label;

    FileType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
