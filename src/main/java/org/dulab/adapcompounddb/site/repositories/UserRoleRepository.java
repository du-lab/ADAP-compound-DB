package org.dulab.adapcompounddb.site.repositories;

import java.util.List;

import org.dulab.adapcompounddb.models.entities.UserPrincipal;
import org.dulab.adapcompounddb.models.entities.UserRole;
import org.springframework.data.repository.CrudRepository;

public interface UserRoleRepository extends CrudRepository<UserRole, Long> {

	List<UserRole> findUserRoleByUserPrincipal(UserPrincipal userPrincipal);
}
