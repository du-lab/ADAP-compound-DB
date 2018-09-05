package org.dulab.adapcompounddb.models;

public enum ChromatographyType implements EnumWithLabelsAndIcons {

    GAS("Gas Chromatography", "resources/AdapCompoundDb/img/chromatography/icon_gc.svg"),
    LIQUID_POSITIVE("Liquid Chromatography (Positive)", "resources/AdapCompoundDb/img/chromatography/icon_lc+.svg"),
    LIQUID_NEGATIVE("Liquid Chromatography (Negative)", "resources/AdapCompoundDb/img/chromatography/icon_lc-.svg"),
    LC_MSMS_POS("LC MS/MS (Pos)", null),
    LC_MSMS_NEG("LC MS/MS (Neg)", null);

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
