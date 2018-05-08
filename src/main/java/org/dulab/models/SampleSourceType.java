package org.dulab.models;

public enum SampleSourceType implements LabeledEnum {

    STD("Standards"),
    URINE("Human urine"),
    PLASMA("Human blood plasma");

    private final String label;

    SampleSourceType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
