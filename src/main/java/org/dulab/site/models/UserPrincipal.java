package org.dulab.site.models;

import org.dulab.site.validation.Email;
import org.dulab.site.validation.NotBlank;

import javax.persistence.*;
import javax.servlet.http.HttpSession;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.security.Principal;
import java.util.List;

@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(name = "UserPrincipal_Username", columnNames = "Username")
})
public class UserPrincipal implements Principal, Cloneable, Serializable {

    private static final long serialVersionUID = 1L;

    private static final String SESSION_ATTRIBUTE_KEY = "userPrincipal";

    private long id;

    @NotBlank(message = "The field Username is required.")
    private String username;

    @NotBlank(message = "The field Email is required.")
    @Email
    private String email;

    @NotNull(message = "Hashed password is required.")
    private byte[] hashedPassword;

    List<Submission> submissions;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Basic(optional = false)
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Basic(optional = false)
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Basic(optional = false)
    public byte[] getHashedPassword() {
        return hashedPassword;
    }

    public void setHashedPassword(byte[] password) {
        this.hashedPassword = password;
    }

    @OneToMany(
            targetEntity = Submission.class,
            mappedBy = "user",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL
    )
    public List<Submission> getSubmissions() {
        return submissions;
    }

    public void setSubmissions(List<Submission> submissions) {
        this.submissions = submissions;
    }

    @Override
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
    @SuppressWarnings("CloneDoesntDeclareCloneNotSupportedException")
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

    public static UserPrincipal getPrincipal(HttpSession session) {
        return session == null ? null : (UserPrincipal) session.getAttribute(SESSION_ATTRIBUTE_KEY);
    }

    public static void setPrincipal(HttpSession session, Principal principal) {
        session.setAttribute(SESSION_ATTRIBUTE_KEY, principal);
    }
}
