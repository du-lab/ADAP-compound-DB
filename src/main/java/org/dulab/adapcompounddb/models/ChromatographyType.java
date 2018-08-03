package org.dulab.adapcompounddb.models;

public enum ChromatographyType implements EnumWithLabelsAndIcons {

    GAS("Gas Chromatography", "/resources/AdapCompoundDb/img/chromatography/icon_gc_64.png"),
    LIQUID_POSITIVE("Liquid Chromatography (Positive)", "/resources/AdapCompoundDb/img/chromatography/icon_lc+_64.png"),
    LIQUID_NEGATIVE("Liquid Chromatography (Negative)", "/resources/AdapCompoundDb/img/chromatography/icon_lc-_64.png");

    private final String label;
    private final String iconPath;

    ChromatographyType(String label, String iconPath) {
        this.label = label;
        this.iconPath = iconPath;
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public String getIconPath() {
        return iconPath;
    }
}
