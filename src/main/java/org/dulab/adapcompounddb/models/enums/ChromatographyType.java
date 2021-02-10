package org.dulab.adapcompounddb.models.enums;

public enum ChromatographyType {

    GAS("GC", "resources/AdapCompoundDb/img/chromatography/icon_gc.svg", "GC-MS"),
    LIQUID_POSITIVE("LC Pseudo +", "resources/AdapCompoundDb/img/chromatography/icon_lc+.svg", "LC-MS<sup>+</sup>"),
    LIQUID_NEGATIVE("LC Pseudo -", "resources/AdapCompoundDb/img/chromatography/icon_lc-.svg", "LC-MS<sup>-</sup>"),
    LC_MSMS_POS("LC MS/MS +", "resources/AdapCompoundDb/img/chromatography/icon_lcmsms+.svg", "LC-MS/MS<sup>+</sup>"),
    LC_MSMS_NEG("LC MS/MS -", "resources/AdapCompoundDb/img/chromatography/icon_lcmsms-.svg", "LC-MS/MS<sup>-</sup>"),
    NONE("Mass", "resources/AdapCompoundDb/img/chromatography/icon_none.svg", "None");

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
