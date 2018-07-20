package org.dulab.adapcompounddb.site.controllers;

import org.dulab.adapcompounddb.models.dto.UserPrincipalDTO;
import org.dulab.adapcompounddb.models.entities.UserPrincipal;
import org.dulab.adapcompounddb.site.services.UserPrincipalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class BaseController {

	@Autowired
	UserPrincipalService userPrincipalService;
	@Autowired
	protected ObjectMapper jacksonObjectMapper;

	public SecurityContext getSecurityContext() {
		return SecurityContextHolder.getContext();
	}

    public UserPrincipalDTO getCurrentUserPrincipal() {
		return jacksonObjectMapper.convertValue(userPrincipalService.getUerByUsername(getCurrentUsername()), UserPrincipalDTO.class);
	}

	public Authentication getAuthentication() {
		return getSecurityContext().getAuthentication();
	}

	public String getCurrentUsername() {
		String username = null;
		Object user = getAuthentication().getPrincipal();

		if(user instanceof User && user != null) {
			username = ((User) user).getUsername();
		}

		return username;
	}

	public boolean isAuthenticated() {
		return getCurrentUsername() != null;
	}
}
