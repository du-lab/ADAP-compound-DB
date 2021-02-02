package org.dulab.adapcompounddb.models.dto;

import org.dulab.adapcompounddb.models.entities.Submission;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SubmissionDTO implements Serializable {

    private static final String YYYY_MM_DD = "yyyy/MM/dd HH:mm:ss";

    private static final long serialVersionUID = 1L;

    // *************************
    // ***** Entity Fields *****
    // *************************

    private long id;

    private String name;

    private String description;

    private Date dateTime;

    private String tagsAsString;

    private String userName;

    private String userEMail;

    private String url;

    private boolean isPrivate;

    private boolean reference;

    private boolean clusterable;

    private String externalId;

    public SubmissionDTO(Submission submission, boolean reference, boolean clusterable) {
        this.id = submission.getId();
        this.name = submission.getName();
        this.description = submission.getDescription();
        this.dateTime = submission.getDateTime();
        this.tagsAsString = submission.getTagsAsString();
        this.userName = submission.getUser().getUsername();
        this.userEMail = submission.getUser().getEmail();
        this.url = submission.getReference();
        this.isPrivate = submission.isPrivate();
        this.reference = reference;
        this.clusterable = clusterable;
        this.externalId = submission.getExternalId();
    }

    public SubmissionDTO(Submission submission) {
        this(submission, false, false);
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

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(final Date dateTime) {
        this.dateTime = dateTime;
    }

    public String getTagsAsString() {
        return tagsAsString;
    }

    public void setTagsAsString(final String tagsAsString) {
        this.tagsAsString = tagsAsString;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEMail() {
        return userEMail;
    }

    public void setUserEMail(String userEMail) {
        this.userEMail = userEMail;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(final String url) {
        this.url = url;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setPrivate(boolean aPrivate) {
        isPrivate = aPrivate;
    }

    public boolean isReference() {
        return reference;
    }

    public void setReference(boolean reference) {
        this.reference = reference;
    }

    public boolean isClusterable() {
        return clusterable;
    }

    public void setClusterable(boolean clusterable) {
        this.clusterable = clusterable;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    // *************************
    // ***** Other methods *****
    // *************************

//    public boolean isAuthorized(final UserPrincipal user) {
//        boolean authorized = false;
//        if (user == null) {
//            authorized = false;
//        } else if (user.isAdmin()) {
//            authorized = true;
//        } else if (id != 0) {
//            authorized = StringUtils.equals(user.getUsername(), getUser().getUsername());
//        }
//
//        return authorized;
//    }

    public String getFormattedDate() {
        final SimpleDateFormat format = new SimpleDateFormat(YYYY_MM_DD);
        return format.format(dateTime);
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof SubmissionDTO)) {
            return false;
        }
        return id == ((SubmissionDTO) other).id;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }
}
