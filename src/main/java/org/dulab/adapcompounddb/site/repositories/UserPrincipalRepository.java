package org.dulab.adapcompounddb.site.repositories;

import org.dulab.adapcompounddb.models.entities.UserPrincipal;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
public interface UserPrincipalRepository extends CrudRepository<UserPrincipal, Long> {

    Optional<UserPrincipal> findUserPrincipalByUsername(String username);

    @Query("select u from UserPrincipal u join fetch u.roles where u.username = ?1")
    Optional<UserPrincipal> findUserPrincipalWithRolesByUsername(String username);

    UserPrincipal findByemail(String email);
    UserPrincipal findBypasswordResetToken(String token);

    UserPrincipal findByOrganizationRequestToken(String token);
    UserPrincipal findByEmailOrUsername(String email, String username);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("UPDATE UserPrincipal u SET u.organizationId = ?1 WHERE u.id in ?2")
    void addUsersToOrganization(final Long organizationUserId, final List<Long> userId);
}
