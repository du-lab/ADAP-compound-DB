package org.dulab.adapcompounddb.models;

public enum SubmissionCategoryType implements EnumWithLabels {

    SOURCE("Source"),
    SPECIMEN("Specimen"),
    TREATMENT("Treatment");

    private final String label;

    SubmissionCategoryType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
