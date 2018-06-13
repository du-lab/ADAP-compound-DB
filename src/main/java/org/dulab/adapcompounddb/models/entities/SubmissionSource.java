package org.dulab.adapcompounddb.models.entities;

import org.dulab.adapcompounddb.validation.NotBlank;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;

@Entity
public class SubmissionSource implements Serializable {

    private static final long serialVersionUID = 1L;

    private long id;

    @NotBlank(message = "SubmissionSource: The field Name is required.")
    private String name;

    private String description;

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
}
