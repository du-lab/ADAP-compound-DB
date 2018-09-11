package org.dulab.adapcompounddb.models.dto;

import java.io.Serializable;

import org.dulab.adapcompounddb.models.SubmissionCategoryType;

public class DiversityIndexDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private SubmissionCategoryType categoryType;

    private double diversity;

    // *******************************
    // ***** Getters and Setters *****
    // *******************************


    public double getDiversity() {
        return diversity;
    }

    public void setDiversity(final double diversity) {
        this.diversity = diversity;
    }

    public SubmissionCategoryType getCategoryType() {
        return categoryType;
    }

    public void setCategoryType(final SubmissionCategoryType categoryType) {
        this.categoryType = categoryType;
    }
}
