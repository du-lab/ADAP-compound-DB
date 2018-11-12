package org.dulab.adapcompounddb.models.entities;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.validation.constraints.NotBlank;

import org.dulab.adapcompounddb.models.SubmissionCategoryType;

@Entity
@Deprecated
public class SubmissionCategory implements Serializable {

    private static final long serialVersionUID = 1L;

    private long id;

    @NotBlank(message = "SubmissionCategory: The field Name is required.")
    private String name;

    private String description;

    private SubmissionCategoryType categoryType;

    private List<Submission> submissions;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long getId() {
        return id;
    }

    public void setId(final long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    @Enumerated(EnumType.STRING)
    public SubmissionCategoryType getCategoryType() {
        return categoryType;
    }

    public void setCategoryType(final SubmissionCategoryType categoryType) {
        this.categoryType = categoryType;
    }

    @ManyToMany(
            fetch = FetchType.LAZY,
            mappedBy = "categories",
            cascade = CascadeType.ALL)
    public List<Submission> getSubmissions() {
        return submissions;
    }

    public void setSubmissions(final List<Submission> submissions) {
        this.submissions = submissions;
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof SubmissionCategory)) {
            return false;
        }
        return id == ((SubmissionCategory) other).id;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }

    @Override
    public String toString() {
        return name;
    }
}
