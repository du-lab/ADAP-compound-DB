package org.dulab.adapcompounddb.models.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotBlank;

import org.apache.commons.lang3.StringUtils;
import org.dulab.adapcompounddb.models.SubmissionCategoryType;
import org.dulab.adapcompounddb.models.entities.File;
import org.dulab.adapcompounddb.models.entities.UserPrincipal;
import org.hibernate.validator.constraints.URL;

public class SubmissionDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    // *************************
    // ***** Entity Fields *****
    // *************************

    private long id;

    @NotBlank(message = "The field 'Name' is required.")
    private String name;

    private String description;

    @URL(message = "The field Reference must be a valid URL.")
    private String reference;

    private Date dateTime;

    private UserPrincipal user;

    private List<SubmissionCategoryDTO> categories;

    private List<File> files;

    private String tags;

    private List<Long> submissionCategoryIds;

    public SubmissionCategoryDTO getCategory(final SubmissionCategoryType type) {
        return getCategories().stream().filter(c -> c.getCategoryType() == type).findFirst().orElse(null);
    }

    public boolean isAuthorized(final UserPrincipal user) {
        boolean authorized = false;
        if (user == null) {
            authorized = false;
        } else if (user.isAdmin()) {
            authorized = true;
        } else if (id != 0) {
            authorized = StringUtils.equals(user.getUsername(), getUser().getUsername());
        }

        return authorized;
    }

    // *******************************
    // ***** Getters and Setters *****
    // *******************************

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

    public String getReference() {
        return reference;
    }

    public void setReference(final String reference) {
        this.reference = reference;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(final Date dateTime) {
        this.dateTime = dateTime;
    }

    public UserPrincipal getUser() {
        return user;
    }

    public void setUser(final UserPrincipal user) {
        this.user = user;
    }

    public List<SubmissionCategoryDTO> getCategories() {
        return categories;
    }

    public void setCategories(final List<SubmissionCategoryDTO> categories) {
        this.categories = categories;
    }

    public List<File> getFiles() {
        return files;
    }

    public void setFiles(final List<File> files) {
        this.files = files;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(final String tags) {
        this.tags = tags;
    }

    public List<Long> getSubmissionCategoryIds() {
        return submissionCategoryIds;
    }

    public void setSubmissionCategoryIds(final List<Long> submissionCategoryIds) {
        this.submissionCategoryIds = submissionCategoryIds;
    }
}
