package org.dulab.adapcompounddb.models.entities;

import org.hibernate.validator.constraints.Length;

import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class SubmissionTagId implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "SubmissionTag: The field Submission is required.")
    @Valid
    private Submission submission;

    @NotBlank(message = "SubmissionTag: The field Name is required.")
    @Length(max = 100, message = "SubmissionTag: The field Name must be less than 100 characters.")
    private String name;

    public SubmissionTagId() {}

    public SubmissionTagId(Submission submission, String name) {
        this.submission = submission;
        this.name = name;
    }

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "SubmissionId", referencedColumnName = "Id")
    public Submission getSubmission() {
        return submission;
    }

    public void setSubmission(Submission submission) {
        this.submission = submission;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof SubmissionTagId)) return false;
        SubmissionTagId that = (SubmissionTagId) other;
        return Objects.equals(this.submission, that.submission)
                && this.name.equalsIgnoreCase(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(submission, name);
    }
}
