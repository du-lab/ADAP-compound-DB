package org.dulab.adapcompounddb.site.repositories;

import org.dulab.adapcompounddb.models.entities.TagDistribution;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;


public interface DistributionRepository extends CrudRepository<TagDistribution, Long> {
    @Modifying
    @Query("DELETE FROM TagDistribution c WHERE 1 = 1")
    void deleteAllTagKey();


}
