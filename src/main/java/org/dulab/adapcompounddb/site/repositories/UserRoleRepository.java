package org.dulab.adapcompounddb.site.repositories;

import org.dulab.adapcompounddb.models.entities.UserPrincipal;
import org.dulab.adapcompounddb.models.entities.UserRole;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRoleRepository extends CrudRepository<UserRole, Long> {

//    UserPrincipal findUserPrincipalByUsername(String username);
}
