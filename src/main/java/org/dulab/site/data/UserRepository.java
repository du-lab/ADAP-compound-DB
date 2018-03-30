package org.dulab.site.data;

import org.dulab.site.models.User;

public interface UserRepository {

    Iterable<User> getAll();

    User get(long id);

    void add(User user);

    void update(User user);

    void delete(User user);

    void delete(long id);
}
