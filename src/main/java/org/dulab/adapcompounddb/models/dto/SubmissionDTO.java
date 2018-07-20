package org.dulab.adapcompounddb.models.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class SubmissionDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final String SESSION_ATTRIBUTE_KEY = "submission";

    // *************************
    // ***** Entity Fields *****
    // *************************

    private long id;

    private String name;

    private String description;

    private Date dateTime;

    private List<SubmissionCategoryDTO> categories;

//    @Valid
//    private SubmissionSource source;
//
//    @Valid
//    private SubmissionSpecimen specimen;
//
//    @Valid
//    private SubmissionDisease disease;

    private List<FileDTO> files;

    private UserPrincipalDTO user;

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

	public Date getDateTime() {
		return dateTime;
	}

	public void setDateTime(Date dateTime) {
		this.dateTime = dateTime;
	}

	public List<SubmissionCategoryDTO> getCategories() {
		return categories;
	}

	public void setCategories(List<SubmissionCategoryDTO> categories) {
		this.categories = categories;
	}

	public List<FileDTO> getFiles() {
		return files;
	}

	public void setFiles(List<FileDTO> files) {
		this.files = files;
	}

	public UserPrincipalDTO getUser() {
		return user;
	}

	public void setUser(UserPrincipalDTO user) {
		this.user = user;
	}

    // *******************************
    // ***** Getters and Setters *****
    // *******************************

}
