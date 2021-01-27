package org.dulab.adapcompounddb.models.enums;

public enum ChromatographyType {

    GAS("Gas Chromatography", "resources/AdapCompoundDb/img/chromatography/icon_gc.svg", "GC-MS"),
    LIQUID_POSITIVE("Liquid Chromatography (Positive)", "resources/AdapCompoundDb/img/chromatography/icon_lc+.svg", "LC-MS<sup>+</sup>"),
    LIQUID_NEGATIVE("Liquid Chromatography (Negative)", "resources/AdapCompoundDb/img/chromatography/icon_lc-.svg", "LC-MS<sup>-</sup>"),
    LC_MSMS_POS("LC MS/MS (Pos)", "resources/AdapCompoundDb/img/chromatography/icon_lcmsms+.svg", "LC-MS/MS<sup>+</sup>"),
    LC_MSMS_NEG("LC MS/MS (Neg)", "resources/AdapCompoundDb/img/chromatography/icon_lcmsms-.svg", "LC-MS/MS<sup>-</sup>"),
    NONE("None", "resources/AdapCompoundDb/img/chromatography/icon_none.svg", "None");

    private final String label;
    private final String iconPath;
    private final String html;

    ChromatographyType(String label, String iconPath, String html) {
        this.label = label;
        this.iconPath = iconPath;
        this.html = html;
    }

    public String getLabel() {
        return label;
    }

    public String getIconPath() {
        return iconPath;
    }

    public String getHtml() {
        return html;
    }
}
