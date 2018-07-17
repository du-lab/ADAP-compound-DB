package org.dulab.adapcompounddb.models.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="user_role")
public class UserRole implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Integer userRoleId;

	private UserPrincipal userPrincipal;

	private Role role;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="user_role_id")
	public Integer getUserRoleId() {
		return userRoleId;
	}

	public void setUserRoleId(Integer userRoleId) {
		this.userRoleId = userRoleId;
	}

	@JoinColumn(name="user_principal_id")
	@ManyToOne
	public UserPrincipal getUserPrincipal() {
		return userPrincipal;
	}

	public void setUserPrincipal(UserPrincipal userPrincipal) {
		this.userPrincipal = userPrincipal;
	}

	@JoinColumn(name="role_id")
	@ManyToOne
	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

}
