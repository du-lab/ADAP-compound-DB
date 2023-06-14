package org.dulab.adapcompounddb.models.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

import com.google.gson.Gson;
import org.dulab.adapcompounddb.models.dto.SearchParametersDTO;
import org.dulab.adapcompounddb.models.enums.UserRole;
import org.dulab.adapcompounddb.validation.Email;

@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(name = "UserPrincipal_Username", columnNames = "Username")
})
public class UserPrincipal implements /*Principal, Cloneable,*/ Serializable {

    private static final long serialVersionUID = 1L;

    public static final String SESSION_ATTRIBUTE_KEY = "userPrincipal";

    private long id;

    @NotBlank(message = "The field Username is required.")
    private String username;

    @NotBlank(message = "The field Email is required.")
    @Email
    private String email;

//    @NotNull(message = "Hashed password is required.")
    private String hashedPassword;

//    private List<Submission> submissions;
    private int peakCapacity = 15000000;
    private int peakNumber = 0;
    private String searchParameters;

    private String passwordResetToken;

    private Date passwordExpirationDate;
    private Set<UserRole> roles;

    private boolean isOrganization;

    private Long organizationId;

    private UserPrincipal organizationUser;

    private List<UserPrincipal> members;

    private String organizationRequestToken;

    private Date organizationRequestExpirationDate;

    public String getOrganizationRequestToken() {
        return organizationRequestToken;
    }

    public void setOrganizationRequestToken(String organizationRequestToken) {
        this.organizationRequestToken = organizationRequestToken;
    }

    public Date getOrganizationRequestExpirationDate() {
        return organizationRequestExpirationDate;
    }

    public void setOrganizationRequestExpirationDate(Date organizationRequestExpirationDate) {
        this.organizationRequestExpirationDate = organizationRequestExpirationDate;
    }

    @ManyToOne
    @JoinColumn(name="organizationId",referencedColumnName="id", insertable=false, updatable=false)
    public UserPrincipal getOrganizationUser() {
        return organizationUser;
    }

    public void setOrganizationUser(UserPrincipal organizationUser) {
        this.organizationUser = organizationUser;
    }

    @OneToMany(targetEntity=UserPrincipal.class, mappedBy="organizationId", fetch=FetchType.EAGER)
    public List<UserPrincipal> getMembers() {
        return members;
    }

    public void setMembers(List<UserPrincipal> members) {
        this.members = members;
    }

    @Column(name = "is_organization", columnDefinition = "BIT")
    public boolean isOrganization() {
        return isOrganization;
    }

    public void setOrganization(boolean organization) {
        isOrganization = organization;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

//    @Basic(optional = false, fetch = FetchType.EAGER)
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

//    @Basic(optional = false, fetch = FetchType.EAGER)
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

//    @Basic(optional = false)
//    @Basic(fetch = FetchType.EAGER)
    public String getHashedPassword() {
        return hashedPassword;
    }

    public void setHashedPassword(String password) {
        this.hashedPassword = password;
    }

    @ElementCollection(targetClass= UserRole.class, fetch=FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name="UserRole", joinColumns= {@JoinColumn(name="userPrincipalId")})
    @Column(name="roleName")
    public Set<UserRole> getRoles() {
        return roles;
    }

    public void setRoles(Set<UserRole> roles) {
        this.roles = roles;
    }

    //    @Override


    public int getPeakCapacity() {
        return peakCapacity;
    }

    public void setPeakCapacity(int peakCapacity) {
        this.peakCapacity = peakCapacity;
    }

    public String getPasswordResetToken() {
        return passwordResetToken;
    }

    public void setPasswordResetToken(String passwordResetToken) {
        this.passwordResetToken = passwordResetToken;
    }

    public Date getPasswordExpirationDate() {
        return passwordExpirationDate;
    }

    public void setPasswordExpirationDate(Date passwordExpirationDate) {
        this.passwordExpirationDate = passwordExpirationDate;
    }

    public int getPeakNumber() {
        return peakNumber;
    }

    public void setPeakNumber(int peakNumber) {
        this.peakNumber = peakNumber;
    }


    @Transient
    public boolean isAdmin() {
        boolean isAdmin = false;
        if(roles != null && roles.contains(UserRole.ADMIN)) {
            isAdmin = true;
        }
        return isAdmin;
    }

    public void assignDefaultRole() {
        if(roles == null || roles.isEmpty()) {
            roles = new HashSet<>();
            roles.add(UserRole.USER);
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

    public String getSearchParameters() {
        return searchParameters;
    }

    public void setSearchParameters(String searchParameters) {
        this.searchParameters = searchParameters;
    }

    @Transient
    public SearchParametersDTO getSearchParametersDTO() {
        SearchParametersDTO chromatographySearchParameters = new Gson().fromJson(getSearchParameters()
                ,SearchParametersDTO.class);
        if (chromatographySearchParameters == null) {
            return new SearchParametersDTO();
        }
        return chromatographySearchParameters;
    }

    public void setSearchParameters(SearchParametersDTO chromatographySearchParameters) {
        setSearchParameters(new Gson().toJson(chromatographySearchParameters));
    }

    @Transient
    public String getFullUserName() {
        return this.isOrganization() ? this.username + " (Organization Account)" :
                this.username;
    }
}
