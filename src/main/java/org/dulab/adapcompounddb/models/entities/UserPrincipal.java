package org.dulab.adapcompounddb.models.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.servlet.http.HttpSession;

import org.apache.commons.collections.CollectionUtils;
import org.dulab.adapcompounddb.models.UserRoles;
import org.dulab.adapcompounddb.validation.Email;
import org.dulab.adapcompounddb.validation.NotBlank;

@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(name = "UserPrincipal_Username", columnNames = "Username")
})
public class UserPrincipal implements /*Principal, Cloneable,*/ Serializable {

    private static final long serialVersionUID = 1L;

    private static final String SESSION_ATTRIBUTE_KEY = "userPrincipal";

    private long id;

    @NotBlank(message = "The field Username is required.")
    private String username;

    @NotBlank(message = "The field Email is required.")
    @Email
    private String email;

//    @NotNull(message = "Hashed password is required.")
    private String hashedPassword;

//    private List<Submission> submissions;

    private List<UserRoles> roles;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Basic(optional = false, fetch = FetchType.EAGER)
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Basic(optional = false, fetch = FetchType.EAGER)
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

//    @Basic(optional = false)
    @Basic(fetch = FetchType.EAGER)
    public String getHashedPassword() {
        return hashedPassword;
    }

    public void setHashedPassword(String password) {
        this.hashedPassword = password;
    }

    @ElementCollection(targetClass=UserRoles.class, fetch=FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name="UserRole", joinColumns= {@JoinColumn(name="userPrincipalId")})
    @Column(name="roleName")
    public List<UserRoles> getRoles() {
		return roles;
	}

	public void setRoles(List<UserRoles> roles) {
		this.roles = roles;
	}

//    @OneToMany(
//            targetEntity = Submission.class,
//            mappedBy = "user",
//            cascade = CascadeType.ALL,
//            orphanRemoval = true
//    )
//    public List<Submission> getSubmissions() {
//        return submissions;
//    }
//
//    public void setSubmissions(List<Submission> submissions) {
//        this.submissions = submissions;
//    }
//
//    public void addSubmission(Submission submission) {
//        if (submissions == null)
//            submissions = new ArrayList<>();
//
//        submissions.add(submission);
//        submission.setUser(this);
//    }
//
//    public void removeSubmission(Submission submission) {
//        if (submissions != null)
//            submissions.remove(submission);
//        submission.setUser(null);
//    }

	//    @Override

    @Transient
	public boolean isAdmin() {
		boolean isAdmin = false;
		if(roles != null && roles.contains(UserRoles.ADMIN)) {
			isAdmin = true;
		}
		return isAdmin;
	}

	public void assignDefaultRole() {
		if(roles == null || roles.isEmpty()) {
			roles = new ArrayList<>();
			roles.add(UserRoles.USER);
		}
	}

    @Transient
    public String getName() {
        return username;
    }

    @Override
    public int hashCode() {
        return username.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof UserPrincipal
                && ((UserPrincipal) other).username.equals(this.username);
    }

    @Override
    protected UserPrincipal clone() {
        try {
            return (UserPrincipal) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e); // not possible
        }
    }

    @Override
    public String toString() {
        return username;
    }

    public static UserPrincipal from(HttpSession session) {
        return session == null ? null : (UserPrincipal) session.getAttribute(SESSION_ATTRIBUTE_KEY);
    }

    public static void assign(HttpSession session, UserPrincipal principal) {
        session.setAttribute(SESSION_ATTRIBUTE_KEY, principal);
    }
}
