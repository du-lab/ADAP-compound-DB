package org.dulab.site.repositories;

import org.dulab.models.UserParameter;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserParameterRepository extends CrudRepository<UserParameter, Long> {

    Optional<UserParameter> findByUserPrincipalIdAndIdentifier(long userPrincipalId, String identifier);

    Optional<UserParameter> findByUserPrincipalIdIsNullAndIdentifier(String identifier);
}
