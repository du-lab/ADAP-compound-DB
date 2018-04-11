package org.dulab.site.repositories;

import org.dulab.site.data.GenericRepository;
import org.dulab.models.UserPrincipal;

public interface UserPrincipalRepository extends GenericRepository<Long, UserPrincipal> {

    UserPrincipal getByUsername(String username);
}
