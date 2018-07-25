package org.dulab.adapcompounddb.models.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;
import org.dulab.adapcompounddb.models.SubmissionCategoryType;
import org.dulab.adapcompounddb.validation.NotBlank;

@Entity
public class Submission implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final String SESSION_ATTRIBUTE_KEY = "submission";

    // *************************
    // ***** Entity Fields *****
    // *************************

    private long id;

    @NotBlank(message = "The field Name is required.")
    private String name;

    private String description;

    @NotNull(message = "Date/Time of submission is required.")
    private Date dateTime;

    @Valid
    private List<SubmissionCategory> categories;

    @Valid
    private List<SubmissionTag> tags;

    @NotNull(message = "Submission: File list is required.")
    @Valid
    private List<File> files;

    @NotNull(message = "You must log in to submit mass spectra to the library.")
    @Valid
    private UserPrincipal user;

    // *******************************
    // ***** Getters and Setters *****
    // *******************************

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name != null ? name : "New Submission";
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String desription) {
        this.description = desription;
    }

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "Submission2SubmissionCategory",
            joinColumns = {@JoinColumn(name = "SubmissionId")},
            inverseJoinColumns = {@JoinColumn(name = "SubmissionCategoryId")})
    public List<SubmissionCategory> getCategories() {
        return categories;
    }

    public void setCategories(List<SubmissionCategory> categories) {
        this.categories = categories;
    }

    public SubmissionCategory getCategory(SubmissionCategoryType type) {
        return getCategories().stream()
                .filter(c -> c.getCategoryType() == type)
                .findFirst()
                .orElse(null);
    }

    @OneToMany(
            mappedBy = "id.submission",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL
    )
    public List<SubmissionTag> getTags() {
        return tags;
    }

    public void setTags(List<SubmissionTag> tags) {
        this.tags = tags;
    }

    @OneToMany(
            mappedBy = "submission",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    public List<File> getFiles() {
        return files;
    }

    public void setFiles(List<File> files) {
        this.files = files;
    }

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "UserPrincipalId", referencedColumnName = "Id")
    public UserPrincipal getUser() {
        return user;
    }

    public void setUser(UserPrincipal user) {
        this.user = user;
    }

    @Temporal(TemporalType.TIMESTAMP)
    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    public boolean isAuthorized(UserPrincipal user) {
		boolean authorized = false;
		if(user != null && user.isAdmin()) {
			authorized = true;
		} else if(id != 0) {
    		authorized = StringUtils.equals(user.getUsername(), this.getUser().getUsername());
    	}

    	return authorized;
	}

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof Submission)) return false;
        return id == ((Submission) other).id;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }

    public static Submission from(HttpSession session) {
        return session == null ? null : (Submission) session.getAttribute(SESSION_ATTRIBUTE_KEY);
    }

    public static void assign(HttpSession session, Submission submission) {
        session.setAttribute(SESSION_ATTRIBUTE_KEY, submission);
    }

    public static void clear(HttpSession session) {
        session.removeAttribute(SESSION_ATTRIBUTE_KEY);
    }
}
