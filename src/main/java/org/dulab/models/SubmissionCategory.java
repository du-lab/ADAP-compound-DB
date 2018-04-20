package org.dulab.models;

import org.dulab.validation.NotBlank;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity
public class SubmissionCategory implements Serializable {

    private static final long serialVersionUID = 1L;

    private long id;

    @NotBlank(message = "The field Name is required.")
    private String name;

    @NotBlank(message = "The field Description is required.")
    private String description;

    @NotNull(message = "You must log in to create a new category.")
    @Valid
    protected UserPrincipal user;

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

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "UserPrincipalId", referencedColumnName = "Id")
    public UserPrincipal getUser() {
        return user;
    }

    public void setUser(UserPrincipal user) {
        this.user = user;
    }
}
