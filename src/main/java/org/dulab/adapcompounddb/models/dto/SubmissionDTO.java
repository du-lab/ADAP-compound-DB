package org.dulab.adapcompounddb.models.dto;

import org.apache.commons.lang3.StringUtils;
import org.dulab.adapcompounddb.models.entities.UserPrincipal;

import javax.persistence.Entity;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Entity
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

    private List<SubmissionCategoryDTO> categories;

    private String tagsAsString;

    private UserPrincipal user;

    private String reference;

    private Integer allSpectrumReference;

    private String externalId;

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

    public List<SubmissionCategoryDTO> getCategories() {
        return categories;
    }

    public void setCategories(final List<SubmissionCategoryDTO> categories) {
        this.categories = categories;
    }

    public String getTagsAsString() {
        return tagsAsString;
    }

    public void setTagsAsString(final String tagsAsString) {
        this.tagsAsString = tagsAsString;
    }

    public UserPrincipal getUser() {
        return user;
    }

    public void setUser(final UserPrincipal user) {
        this.user = user;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(final String reference) {
        this.reference = reference;
    }

    public Integer getAllSpectrumReference() {
        return allSpectrumReference;
    }

    public void setAllSpectrumReference(final Integer allSpectrumReference) {
        this.allSpectrumReference = allSpectrumReference;
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
