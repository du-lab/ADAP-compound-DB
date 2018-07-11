package org.dulab.adapcompounddb.models.entities;

import org.dulab.adapcompounddb.models.SubmissionCategoryType;
import org.dulab.adapcompounddb.validation.NotBlank;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
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

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Enumerated(EnumType.STRING)
    public SubmissionCategoryType getCategoryType() {
        return categoryType;
    }

    public void setCategoryType(SubmissionCategoryType categoryType) {
        this.categoryType = categoryType;
    }

    @ManyToMany(
            fetch = FetchType.LAZY,
            mappedBy = "categories",
            cascade = CascadeType.ALL)
    public List<Submission> getSubmissions() {
        return submissions;
    }

    public void setSubmissions(List<Submission> submissions) {
        this.submissions = submissions;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof SubmissionCategory)) return false;
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
