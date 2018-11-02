package org.dulab.adapcompounddb.models;

public enum SubmissionCategoryType implements EnumWithLabels {

    SOURCE("Source"),
    SPECIMEN("Specimen"),
    TREATMENT("Treatment"),
    MIN("Minimum"),
    MAX("Maximum"),
    AVERAGE("Average");

    private final String label;

    SubmissionCategoryType(final String label) {
        this.label = label;
    }

    @Override
    public String getLabel() {
        return label;
    }
}
