package org.dulab.adapcompounddb.site.repositories;

import org.dulab.adapcompounddb.models.entities.UserParameter;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

import org.springframework.stereotype.Repository;

@Repository
public interface UserParameterRepository extends CrudRepository<UserParameter, Long> {

    Optional<UserParameter> findByUserPrincipalIdAndIdentifier(long userPrincipalId, String identifier);

    Optional<UserParameter> findByUserPrincipalIdIsNullAndIdentifier(String identifier);
}
