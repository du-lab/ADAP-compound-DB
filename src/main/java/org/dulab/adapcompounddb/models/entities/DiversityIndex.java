package org.dulab.adapcompounddb.models.entities;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import java.io.Serializable;

@Entity
public class DiversityIndex implements Serializable {

    private static final long serialVersionUID = 1L;

    @EmbeddedId
    private DiversityIndexId id;

    private double diversity;

    // *******************************
    // ***** Getters and Setters *****
    // *******************************

    public DiversityIndexId getId() {
        return id;
    }

    public void setId(DiversityIndexId id) {
        this.id = id;
    }

    public double getDiversity() {
        return diversity;
    }

    public void setDiversity(double diversity) {
        this.diversity = diversity;
    }
}
