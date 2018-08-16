package org.dulab.adapcompounddb.models.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.validation.constraints.NotBlank;

import org.apache.commons.lang3.StringUtils;
import org.dulab.adapcompounddb.models.SubmissionCategoryType;
import org.dulab.adapcompounddb.models.entities.File;
import org.dulab.adapcompounddb.models.entities.UserPrincipal;
import org.hibernate.validator.constraints.URL;

@Entity
public class SubmissionDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    // *************************
    // ***** Entity Fields *****
    // *************************

    private long id;

    @NotBlank(message = "The field Name is required.")
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

    // *******************************
    // ***** Getters and Setters *****
    // *******************************

    public SubmissionCategoryDTO getCategory(SubmissionCategoryType type) {
        return getCategories().stream()
                .filter(c -> c.getCategoryType() == type)
                .findFirst()
                .orElse(null);
    }

    public boolean isAuthorized(UserPrincipal user) {
        boolean authorized = false;
        if (user == null) {
            authorized = false;
        } else if (user.isAdmin()) {
            authorized = true;
        } else if (id != 0) {
            authorized = StringUtils.equals(user.getUsername(), this.getUser().getUsername());
        }

        return authorized;
    }

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

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public Date getDateTime() {
		return dateTime;
	}

	public void setDateTime(Date dateTime) {
		this.dateTime = dateTime;
	}

	public UserPrincipal getUser() {
		return user;
	}

	public void setUser(UserPrincipal user) {
		this.user = user;
	}

	public List<SubmissionCategoryDTO> getCategories() {
		return categories;
	}

	public void setCategories(List<SubmissionCategoryDTO> categories) {
		this.categories = categories;
	}

	public List<File> getFiles() {
		return files;
	}

	public void setFiles(List<File> files) {
		this.files = files;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

    public List<Long> getSubmissionCategoryIds() {
        return submissionCategoryIds;
    }

    public void setSubmissionCategoryIds(List<Long> submissionCategoryIds) {
        this.submissionCategoryIds = submissionCategoryIds;
    }
}
