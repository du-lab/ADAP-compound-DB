package org.dulab.adapcompounddb.site.controllers.forms;

import org.dulab.adapcompounddb.models.entities.Adduct;
import org.dulab.adapcompounddb.models.enums.ChromatographyType;

import java.util.List;

public class CompoundSearchForm extends FilterForm {
    private String Identifier;
    private Double NeutralMass;
    private Double PrecursorMZ;
    private String Spectrum;

    private ChromatographyType chromatographyType;


    private String adducts;

//    public CompoundSearchForm(String identifier, Double neutralMass, Double precursorMZ, String spectrum, ChromatographyType chromatographyTypes) {
//        Identifier = identifier;
//        NeutralMass = neutralMass;
//        PrecursorMZ = precursorMZ;
//        Spectrum = spectrum;
//        this.chromatographyType = chromatographyTypes;
//    }

    public String getIdentifier() {
        return Identifier;
    }

    public Double getNeutralMass() {
        return NeutralMass;
    }

    public Double getPrecursorMZ() {
        return PrecursorMZ;
    }

    public String getSpectrum() {
        return Spectrum;
    }

    public void setIdentifier(String identifier) {
        Identifier = identifier;
    }

    public void setNeutralMass(Double neutralMass) {
        NeutralMass = neutralMass;
    }

    public void setPrecursorMZ(Double precursorMZ) {
        PrecursorMZ = precursorMZ;
    }

    public void setSpectrum(String spectrum) {
        Spectrum = spectrum;
    }

    public ChromatographyType getChromatographyType() {
        return chromatographyType;
    }

    public void setChromatographyType(ChromatographyType chromatographyType) {
        this.chromatographyType = chromatographyType;
    }

    public String getAdducts() {
        return adducts;
    }

    public void setAdducts(String adducts) {
        this.adducts = adducts;
    }




}
