package org.dulab.adapcompounddb.models.enums;

public enum FileType implements EnumWithLabels {

    MSP("MSP: NIST text format of individual spectra", 10, "msp"),
    CSV("CSV: Comma-separated values", 1, "csv"),
    RAW("RAW: raw mass spectrometry data", 0, "mzML", "mzXML", "cdf");


    private final String label;
    private final int priority;
    private final String[] extensions;


    FileType(String label, int priority, String... extensions) {
        this.label = label;
        this.priority = priority;
        this.extensions = extensions;
    }

    public String getLabel() {
        return label;
    }

    public int getPriority() {
        return priority;
    }

    public String[] getExtensions() {
        return extensions;
    }
}
