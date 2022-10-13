package org.dulab.adapcompounddb.models.enums;

public enum FileType implements EnumWithLabels {

    MSP("MSP/MSL", "MSP: NIST text format of individual spectra", 10, "msp", "msl"),
    CSV("CSV", "CSV: Comma-separated values", 1, "csv"),
    RAW("Raw", "RAW: raw mass spectrometry data", 0, "mzML", "mzXML", "cdf"),
    MGF("MGF", "MGF: mascot generic format of individual spectra", 9, "mgf");

    private final String name;
    private final String label;
    private final int priority;
    private final String[] extensions;


    FileType(String name, String label, int priority, String... extensions) {
        this.name = name;
        this.label = label;
        this.priority = priority;
        this.extensions = extensions;
    }

    public String getName() {
        return name;
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
