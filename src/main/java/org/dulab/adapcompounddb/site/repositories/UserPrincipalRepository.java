package org.dulab.adapcompounddb.site.repositories;

import org.dulab.adapcompounddb.models.entities.UserPrincipal;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserPrincipalRepository extends CrudRepository<UserPrincipal, Long> {

//    UserPrincipal findUserPrincipalByUsername(String username);
    Optional<UserPrincipal> findUserPrincipalByUsername(String username);
}
