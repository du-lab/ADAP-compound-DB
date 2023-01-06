package org.dulab.adapcompounddb.site.repositories;

import org.dulab.adapcompounddb.models.entities.SearchParameters;
import org.dulab.adapcompounddb.models.entities.UserPrincipal;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;

public interface SearchParameterRepository extends CrudRepository<SearchParameters, Integer> {

    @Query("SELECT sp FROM SearchParameters sp WHERE sp.userPrimaryId = :userId")
    List<SearchParameters> findAllByUserId(@Param("userId") final long userId);
}
