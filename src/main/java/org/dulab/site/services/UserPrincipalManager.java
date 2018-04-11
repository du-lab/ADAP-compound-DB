package org.dulab.site.services;

import org.dulab.models.UserPrincipal;

import java.util.List;

public interface UserPrincipalManager {

    List<UserPrincipal> getUsers();

    void saveUser(UserPrincipal user);
}
