package org.dulab.adapcompounddb.models.entities;

import org.dulab.adapcompounddb.models.RoleType;
import org.dulab.adapcompounddb.validation.Email;
import org.dulab.adapcompounddb.validation.NotBlank;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.context.annotation.Role;

import javax.persistence.*;
import javax.servlet.http.HttpSession;
import java.io.Serializable;

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


    @MapKeyEnumerated
    private RoleType role = RoleType.Normal; //Default value set as Normal

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public RoleType getRole() {
        return role;
    }

    public void setRole(RoleType role) {
        this.role = role;
    }



    @NotBlank(message = "The field Email is required.")
    @Email
    private String email;

//    @NotNull(message = "Hashed password is required.")
    private byte[] hashedPassword;

//    private List<Submission> submissions;

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
    public byte[] getHashedPassword() {
        return hashedPassword;
    }

    public void setHashedPassword(byte[] password) {
        this.hashedPassword = password;
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

    public static UserPrincipal from(HttpSession session) {
        return session == null ? null : (UserPrincipal) session.getAttribute(SESSION_ATTRIBUTE_KEY);
    }

    public static void assign(HttpSession session, UserPrincipal principal) {
        session.setAttribute(SESSION_ATTRIBUTE_KEY, principal);
    }
}
