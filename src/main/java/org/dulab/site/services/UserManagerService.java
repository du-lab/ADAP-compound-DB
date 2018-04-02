package org.dulab.site.services;

import org.dulab.site.models.User;

import java.util.List;

public interface UserManagerService {
    List<User> getUsers();
    void saveUser(User user);
}
