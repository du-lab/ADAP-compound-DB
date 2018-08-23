package org.dulab.adapcompounddb.site.controllers;

import org.dulab.adapcompounddb.models.entities.UserPrincipal;
import org.dulab.adapcompounddb.site.services.UserPrincipalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

public abstract class BaseController {

    protected static final String ACCESS_DENIED_MESSAGE = "Sorry you do not have access to this page";

    @Autowired
    UserPrincipalService userPrincipalService;

    public SecurityContext getSecurityContext() {
        return SecurityContextHolder.getContext();
    }

    public UserPrincipal getCurrentUserPrincipal() {
        return userPrincipalService.findUserByUsername(getCurrentUsername());
    }

    public Authentication getAuthentication() {
        return getSecurityContext().getAuthentication();
    }

    public String getCurrentUsername() {
        String username = null;
        final Object user = getAuthentication().getPrincipal();

        if (user instanceof User && user != null) {
            username = ((User) user).getUsername();
        }

        return username;
    }

    public boolean isAuthenticated() {
        return getCurrentUsername() != null;
    }
}
