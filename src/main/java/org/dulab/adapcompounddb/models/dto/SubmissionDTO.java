package org.dulab.adapcompounddb.models.dto;

import org.dulab.adapcompounddb.models.entities.Submission;
import org.dulab.adapcompounddb.models.entities.SubmissionTag;

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
    private String externalId;

    private String source;
    private Date dateTime;
    private String tagsAsString;
    private String userName;
    private String userEMail;
    private String url;
    private boolean isPrivate;
    private boolean reference;
    private boolean clusterable;
    private boolean isLibrary;
    private boolean isInHouseLibrary;
    private String[] tags;

    public SubmissionDTO(Submission submission, boolean isLibrary, boolean isInHouseLibrary, boolean clusterable) {
        this.id = submission.getId();
        this.name = submission.getName();
        this.description = submission.getDescription();
        this.dateTime = submission.getDateTime();
        this.tagsAsString = submission.getTagsAsString();
        this.tags = submission.getTags().stream()
                .map(SubmissionTag::toString)
                .toArray(String[]::new);
        this.userName = submission.getUser().getUsername();
        this.userEMail = submission.getUser().getEmail();
        this.url = submission.getUrl();
        this.isPrivate = submission.isPrivate();
        this.reference = isLibrary;
        this.clusterable = clusterable;
        this.isLibrary = isLibrary;
        this.isInHouseLibrary = isInHouseLibrary;
        this.externalId = submission.getExternalId();
        this.source = submission.getSource();
    }

//    public SubmissionDTO(Submission submission) {
//        this(submission, false, false, false);
//    }

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

    public void setPrivate(boolean isPrivate) {
        this.isPrivate = isPrivate;
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

    public boolean isLibrary() {
        return isLibrary;
    }

    public void setLibrary(boolean library) {
        isLibrary = library;
    }

    public boolean isInHouseLibrary() {
        return isInHouseLibrary;
    }

    public void setInHouseLibrary(boolean inHouseLibrary) {
        isInHouseLibrary = inHouseLibrary;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String[] getTags() {
        return tags;
    }

    public void setTags(String[] tags) {
        this.tags = tags;
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
