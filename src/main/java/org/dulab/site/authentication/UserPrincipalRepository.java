package org.dulab.site.authentication;

import org.dulab.site.data.GenericRepository;
import org.dulab.site.models.UserPrincipal;

public interface UserPrincipalRepository extends GenericRepository<Long, UserPrincipal> {

    UserPrincipal getByUsername(String username);
}
