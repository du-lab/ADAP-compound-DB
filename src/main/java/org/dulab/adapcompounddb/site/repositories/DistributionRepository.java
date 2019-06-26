package org.dulab.adapcompounddb.site.repositories;

import org.dulab.adapcompounddb.models.entities.TagDistribution;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;


public interface DistributionRepository extends CrudRepository<TagDistribution, Long> {

    // get all the tag distributions from TagDistribution table where cluster ID is null
    Iterable<TagDistribution>getAllByClusterIdIsNull();
}
