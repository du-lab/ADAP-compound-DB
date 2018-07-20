package org.dulab.adapcompounddb.models.dto;

import java.io.Serializable;

public class UserPrincipalDTO implements /*Principal, Cloneable,*/ Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long id;
    private String username;
    private String email;

	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}

}
