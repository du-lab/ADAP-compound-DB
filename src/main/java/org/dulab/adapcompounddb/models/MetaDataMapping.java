package org.dulab.adapcompounddb.models;

public class MetaDataMapping {
    private String nameField;
    private String externalIdField;
    private String precursorMzField;
    private String precursorTypeField;
    private String retTimeField;
    private String massField;


    public MetaDataMapping() {
        this(null, null, null, null, null, null);
    }

    public MetaDataMapping(String nameField, String externalIdField, String precursorMzField, String precursorTypeField,
                           String retTimeField, String massField) {
        this.nameField = nameField;
        this.externalIdField = externalIdField;
        this.precursorMzField = precursorMzField;
        this.retTimeField = retTimeField;
        this.massField = massField;
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
}
