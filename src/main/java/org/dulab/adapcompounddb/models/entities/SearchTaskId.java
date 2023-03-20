package org.dulab.adapcompounddb.models.entities;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class SearchTaskId implements Serializable {
    private Long userId;
    private Long submissionId;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getSubmissionId() {
        return submissionId;
    }

    public void setSubmissionId(Long submissionId) {
        this.submissionId = submissionId;
    }

    public SearchTaskId(Long userId, Long submissionId) {
        this.userId = userId;
        this.submissionId = submissionId;
    }
}
