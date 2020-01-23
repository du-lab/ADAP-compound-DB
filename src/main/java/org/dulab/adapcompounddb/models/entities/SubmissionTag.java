package org.dulab.adapcompounddb.models.entities;

import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity
public class SubmissionTag implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final char TAG_DELIMITER = ':';

    // *************************
    // ***** Entity Fields *****
    // *************************

    private long id;

    @NotNull(message = "SubmissionTag: The field Submission is required.")
    @Valid
    private Submission submission;

    @NotBlank(message = "SubmissionTag: The field Key is required.")
    @Length(max = 100, message = "SubmissionTag: The field Key must be less than 100 characters.")
    private String tagKey;

    private String tagValue;

    public SubmissionTag() {}

    public SubmissionTag(Submission submission, String tagKey, String tagValue) {
        this.submission = submission;
        this.tagKey = tagKey.toLowerCase();
        this.tagValue = tagValue.toLowerCase();
    }

    public SubmissionTag(Submission submission, String tag) {
        this(submission,
                tag.substring(0, tag.indexOf(':')).trim(),
                tag.substring(tag.indexOf(':') + 1).trim());
    }

    // *******************************
    // ***** Getters and Setters *****
    // *******************************

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "SubmissionId", referencedColumnName = "Id")
    public Submission getSubmission() {
        return submission;
    }

    public void setSubmission(Submission submission) {
        this.submission = submission;
    }

    public String getTagKey() {
        return tagKey;
    }

    public void setTagKey(String tagKey) {
        this.tagKey = tagKey;
    }

    public String getTagValue() {
        return tagValue;
    }

    public void setTagValue(String tagValue) {
        this.tagValue = tagValue;
    }

    @Override
    public String toString() {
        if (tagKey != null && tagValue != null)
            return String.format("%s %c %s", tagKey, TAG_DELIMITER, tagValue);
        return super.toString();
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof SubmissionTag)) {
            return false;
        }
        return id == ((SubmissionTag) other).id;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }
}
