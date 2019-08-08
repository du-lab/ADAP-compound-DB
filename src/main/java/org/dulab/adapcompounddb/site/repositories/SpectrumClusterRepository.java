package org.dulab.adapcompounddb.site.repositories;

import org.dulab.adapcompounddb.models.entities.SpectrumCluster;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface

SpectrumClusterRepository extends JpaRepository<SpectrumCluster, Long> {

    void deleteByIdNotIn(List<Long> ids);

    @Modifying
    @Query("select c FROM SpectrumCluster c")
    Iterable<SpectrumCluster> getAllClusters();

    @Modifying
    @Query("DELETE FROM SpectrumCluster c WHERE 1 = 1")
    void deleteAllEmptyClusters();

    @Modifying
    @Query("DELETE FROM Spectrum c WHERE consensus = 1")
    void deleteAllConsensusSpectra();

    @Query(value = "select s from SpectrumCluster s "
            + "where "
            + "s.consensusSpectrum.name like %:search% "
            + "OR s.consensusSpectrum.chromatographyType like %:search%")
    Page<SpectrumCluster> findClusters(@Param("search") String searchStr, Pageable pageable);
}
