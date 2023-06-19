package org.dulab.adapcompounddb.models.enums;

public enum ChromatographyType {

    GAS("GC", "resources/AdapCompoundDb/img/chromatography/icon_gc.svg", "GC-MS", ChromatographyGroup.GAS),
    LIQUID_POSITIVE("LC Pseudo +", "resources/AdapCompoundDb/img/chromatography/icon_lc+.svg", "LC-MS<sup>+</sup>", ChromatographyGroup.LIQUID),
    LIQUID_NEGATIVE("LC Pseudo -", "resources/AdapCompoundDb/img/chromatography/icon_lc-.svg", "LC-MS<sup>-</sup>", ChromatographyGroup.LIQUID),
    LC_MSMS_POS("LC MS/MS +", "resources/AdapCompoundDb/img/chromatography/icon_lcmsms+.svg", "LC-MS/MS<sup>+</sup>", ChromatographyGroup.LIQUID),
    LC_MSMS_NEG("LC MS/MS -", "resources/AdapCompoundDb/img/chromatography/icon_lcmsms-.svg", "LC-MS/MS<sup>-</sup>", ChromatographyGroup.LIQUID),
    NONE("Mass", "resources/AdapCompoundDb/img/chromatography/icon_none.svg", "None", ChromatographyGroup.OTHER);

    private final String label;
    private final String iconPath;
    private final String html;

    private final ChromatographyGroup chromatographyGroup;

    ChromatographyType(String label, String iconPath, String html, ChromatographyGroup chromatographyGroup) {
        this.label = label;
        this.iconPath = iconPath;
        this.html = html;
        this.chromatographyGroup = chromatographyGroup;
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

    public ChromatographyGroup getChromatographyGroup() {
        return chromatographyGroup;
    }
}
