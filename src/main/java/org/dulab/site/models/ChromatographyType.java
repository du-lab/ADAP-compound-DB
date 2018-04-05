package org.dulab.site.models;

public enum ChromatographyType implements LabeledEnum {

    GAS("Gas Chromatography"),
    LIQUID_POSITIVE("Liquid Chromatography (Positive)"),
    LIQUID_NEGATIVE("Liquid Chromatography (Negative)");

    private final String label;

    ChromatographyType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
