package org.dulab.adapcompounddb.models.entities;

import java.io.Serializable;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Transient;

import org.dulab.adapcompounddb.models.SubmissionCategoryType;

@Entity
public class DiversityIndex implements Serializable {

    private static final long serialVersionUID = 1L;

    @EmbeddedId
    private DiversityIndexId id;

    private double diversity;

    @Transient
    private SubmissionCategoryType categoryType;

    // *******************************
    // ***** Getters and Setters *****
    // *******************************

    public DiversityIndexId getId() {
        return id;
    }

    public void setId(final DiversityIndexId id) {
        this.id = id;
    }

    public double getDiversity() {
        return diversity;
    }

    public void setDiversity(final double diversity) {
        this.diversity = diversity;
    }

    public SubmissionCategoryType getCategoryType() {
        categoryType = id.getCategoryType();
        return categoryType;
    }

    public void setCategoryType(final SubmissionCategoryType categoryType) {
        this.categoryType = categoryType;
    }
}
