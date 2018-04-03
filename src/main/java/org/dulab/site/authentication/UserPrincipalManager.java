package org.dulab.site.authentication;

import org.dulab.site.models.UserPrincipal;

import java.util.List;

public interface UserPrincipalManager {

    List<UserPrincipal> getUsers();

    void saveUser(UserPrincipal user);
}
