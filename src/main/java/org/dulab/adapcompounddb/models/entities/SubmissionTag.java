package org.dulab.adapcompounddb.models.entities;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class SubmissionTag implements Serializable {

    private static final long serialVersionUID = 1L;

    // *************************
    // ***** Entity Fields *****
    // *************************

    private SubmissionTagId id;

    // *******************************
    // ***** Getters and Setters *****
    // *******************************

    @EmbeddedId
    public SubmissionTagId getId() {
        return id;
    }

    public void setId(SubmissionTagId id) {
        this.id = id;
    }
}
