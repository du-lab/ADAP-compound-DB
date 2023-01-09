package org.dulab.adapcompounddb.models.dto;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;

import org.dulab.adapcompounddb.models.SubmissionCategoryType;

@Entity
public class SubmissionCategoryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private long id;

    private String name;

    private String description;

    private SubmissionCategoryType categoryType;

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

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof SubmissionCategoryDTO)) return false;
        return id == ((SubmissionCategoryDTO) other).id;
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
