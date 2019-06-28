package org.dulab.adapcompounddb.site.repositories;

import org.dulab.adapcompounddb.models.ChromatographyType;
import org.dulab.adapcompounddb.models.entities.Spectrum;
import org.dulab.adapcompounddb.models.entities.TagDistribution;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Date;


public interface DistributionRepository extends CrudRepository<TagDistribution, Long> {

    // get all the tag distributions from TagDistribution table where cluster ID is null
    Iterable<TagDistribution> getAllByClusterIdIsNull();

    @Query("SELECT t.tagDistribution FROM TagDistribution t WHERE t.cluster IS null "
            + "AND t.tagKey = ?1")
    String findTagDistributionByTagKey(String tagKey);

}
