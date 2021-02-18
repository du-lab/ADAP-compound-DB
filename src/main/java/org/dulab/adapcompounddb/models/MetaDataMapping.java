package org.dulab.adapcompounddb.models;

public class MetaDataMapping {
    private String nameField;
    private String externalIdField;
    private String precursorMzField;
    private String retTimeField;
    private String molecularWeight;


    public MetaDataMapping() {
        this(null, null, null, null, null);
    }

    public MetaDataMapping(String nameField, String externalIdField, String precursorMzField, String retTimeField,
                           String molecularWeight) {
        this.nameField = nameField;
        this.externalIdField = externalIdField;
        this.precursorMzField = precursorMzField;
        this.retTimeField = retTimeField;
        this.molecularWeight = molecularWeight;
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

    public String getRetTimeField() {
        return retTimeField;
    }

    public String getMolecularWeight() {
        return molecularWeight;
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

    public void setRetTimeField(String retTimeField) {
        this.retTimeField = retTimeField;
    }

    public void setMolecularWeight(String molecularWeight) {
        this.molecularWeight = molecularWeight;
    }
}
