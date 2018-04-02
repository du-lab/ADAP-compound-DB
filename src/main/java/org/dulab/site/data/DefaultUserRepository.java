package org.dulab.site.data;

import org.dulab.site.models.User;
import org.springframework.stereotype.Repository;

@Repository
public class DefaultUserRepository extends GenericJpaRepository<Long, User> implements UserRepository {

    public DefaultUserRepository() {
        super(Long.class, User.class);
    }
}
