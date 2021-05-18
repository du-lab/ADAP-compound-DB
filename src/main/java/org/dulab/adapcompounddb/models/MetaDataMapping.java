package org.dulab.adapcompounddb.models;

import org.dulab.adapcompounddb.models.entities.Spectrum;
import org.dulab.adapcompounddb.models.entities.SpectrumProperty;
import org.dulab.adapcompounddb.site.services.utils.MappingUtils;

import static org.dulab.adapcompounddb.site.services.utils.MappingUtils.parseDouble;

public class MetaDataMapping {
    private String nameField;
    private String externalIdField;
    private String precursorMzField;
    private String precursorTypeField;
    private String retTimeField;
    private String massField;
    private String formulaField;


    public MetaDataMapping() {
        this(null, null, null, null, null, null, null);
    }

    public MetaDataMapping(String nameField, String externalIdField, String precursorMzField, String precursorTypeField,
                           String retTimeField, String massField, String formulaField) {
        this.nameField = nameField;
        this.externalIdField = externalIdField;
        this.precursorMzField = precursorMzField;
        this.retTimeField = retTimeField;
        this.massField = massField;
        this.formulaField = formulaField;
    }

    public String getNameField() {
        return nameField;
    }

    public String getExternalIdField() {
        return externalIdField;
    }

    public String getPrecursorMzField() {
        return precursorMzField;
    }

    public String getPrecursorTypeField() {
        return precursorTypeField;
    }

    public String getRetTimeField() {
        return retTimeField;
    }

    public String getMassField() {
        return massField;
    }

    public String getFormulaField() {
        return formulaField;
    }

    public void setNameField(String nameField) {
        this.nameField = nameField;
    }

    public void setExternalIdField(String externalIdField) {
        this.externalIdField = externalIdField;
    }

    public void setPrecursorMzField(String precursorMzField) {
        this.precursorMzField = precursorMzField;
    }

    public void setPrecursorTypeField(String precursorTypeField) {
        this.precursorTypeField = precursorTypeField;
    }

    public void setRetTimeField(String retTimeField) {
        this.retTimeField = retTimeField;
    }

    public void setMassField(String massField) {
        this.massField = massField;
    }

    public void setFormulaField(String formulaField) {
        this.formulaField = formulaField;
    }

    public void map(SpectrumProperty property, Spectrum spectrum) {
        String propertyName = property.getName();
        String propertyValue = property.getValue();
        if (propertyName.equalsIgnoreCase(this.getNameField()))
            spectrum.setName(propertyValue);
        else if (propertyName.equalsIgnoreCase(this.getExternalIdField()))
            spectrum.setExternalId(propertyValue);
        else if (propertyName.equalsIgnoreCase(this.getPrecursorMzField()))
            spectrum.setPrecursor(parseDouble(propertyValue));
        else if (propertyName.equalsIgnoreCase(this.getPrecursorTypeField()))
            spectrum.setPrecursorType(propertyValue);
        else if (propertyName.equalsIgnoreCase(this.getRetTimeField()))
            spectrum.setRetentionTime(parseDouble(propertyValue));
        else if (propertyName.equalsIgnoreCase(this.getMassField()))
            spectrum.setMass(parseDouble(propertyValue));
        else if (propertyName.equalsIgnoreCase(this.getFormulaField()))
            spectrum.setFormula(propertyValue);
    }
}
