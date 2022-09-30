package org.dulab.adapcompounddb.models;

public class FormField {
    private String id;

    public FormField(String id, String labelText) {
        this.id = id;
        this.labelText = labelText;
    }

    private String labelText;


    public String getLabelText() {
        return labelText;
    }

    public void setLabelText(String labelText) {
        this.labelText = labelText;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
