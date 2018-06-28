package org.dulab.adapcompounddb.models.entities;

import org.dulab.adapcompounddb.models.SubmissionCategory;
import org.dulab.adapcompounddb.validation.NotBlank;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;

@Entity
public class SubmissionSource implements Serializable, SubmissionCategory {

    private static final long serialVersionUID = 1L;

    private long id;

    @NotBlank(message = "SubmissionSource: The field Name is required.")
    private String name;

    private String description;

    @Override
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long getId() {
        return id;
    }

    @Override
    public void setId(long id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof SubmissionSource)) return false;
        return id == ((SubmissionSource) other).id;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }
}
