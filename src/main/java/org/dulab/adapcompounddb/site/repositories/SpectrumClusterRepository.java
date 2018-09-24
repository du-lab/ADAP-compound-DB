package org.dulab.adapcompounddb.site.repositories;

import java.util.List;

import org.dulab.adapcompounddb.models.entities.SpectrumCluster;
import org.dulab.adapcompounddb.models.entities.views.ClusterPage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SpectrumClusterRepository extends JpaRepository<SpectrumCluster, Long> {

    void deleteByIdNotIn(List<Long> ids);

    @Modifying
    @Query("DELETE FROM SpectrumCluster c WHERE SIZE(c.spectra) = 1")
    void deleteAllEmptyClusters();

    @Query(value="select s from ClusterPage s "
            + "where "
            + "s.spectrumCluster.consensusSpectrum.name like %:search% "
            + "OR s.spectrumCluster.consensusSpectrum.chromatographyType like %:search%" )
    Page<ClusterPage> findClusters(@Param("search") String searchStr, Pageable pageable);
}
