package org.dulab.site.repositories;

import org.dulab.models.UserPrincipal;
import org.springframework.data.repository.CrudRepository;

public interface UserPrincipalRepository extends CrudRepository<UserPrincipal, Long> {

    UserPrincipal getOneByUsername(String username);
}
