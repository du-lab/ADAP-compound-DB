package org.dulab.adapcompounddb.site.repositories;

import org.dulab.adapcompounddb.models.entities.UserPrincipal;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

import org.springframework.stereotype.Repository;

@Repository
public interface UserPrincipalRepository extends CrudRepository<UserPrincipal, Long> {

    Optional<UserPrincipal> findUserPrincipalByUsername(String username);

    @Query("select u from UserPrincipal u join fetch u.roles where u.username = ?1")
    Optional<UserPrincipal> findUserPrincipalWithRolesByUsername(String username);

    UserPrincipal findByemail(String email);
    UserPrincipal findBypasswordResetToken(String token);
}
