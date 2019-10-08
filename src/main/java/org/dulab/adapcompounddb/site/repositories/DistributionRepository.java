package org.dulab.adapcompounddb.site.repositories;

import org.dulab.adapcompounddb.models.ChromatographyType;
import org.dulab.adapcompounddb.models.entities.Spectrum;
import org.dulab.adapcompounddb.models.entities.TagDistribution;
import org.dulab.adapcompounddb.models.enums.MassSpectrometryType;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Date;


public interface DistributionRepository extends CrudRepository<TagDistribution, Long> {

    // get all the tag distributions from TagDistribution table where cluster ID is null
    Iterable<TagDistribution> getAllByClusterIdIsNull();

    @Query("SELECT t.distribution FROM TagDistribution t WHERE t.cluster IS null "
            + "AND t.label = ?1")
    String findTagDistributionByTagKey(String tagKey);

    @Query("SELECT t FROM TagDistribution t WHERE t.cluster IS null ")
    Iterable<TagDistribution> findAllDbTagDistribution();

    @Query("SELECT t FROM TagDistribution t WHERE t.cluster IS NULL AND t.massSpectrometryType = ?1")
    Iterable<TagDistribution> findAllDbTagDistributionsByMassSpectrometryType(MassSpectrometryType type);

    @Query("SELECT t FROM TagDistribution t"
            + " WHERE t.cluster is not null"
            + " AND t.label = ?1"
            + " AND t.cluster.id = ?2")
    TagDistribution findClusterTagDistributionByTagKey(String tagKey, long id);

    @Query("SELECT t FROM TagDistribution t"
            + " WHERE t.cluster.id = ?1")
    Iterable<TagDistribution> findClusterTagDistributionsByClusterId(long id);

    @Query("SELECT t.pValue FROM TagDistribution t"
            + " where t.label = ?1"
            + " and t.cluster.id = ?2")
    Double getClusterPvalue(String tagKey, long id);


}
