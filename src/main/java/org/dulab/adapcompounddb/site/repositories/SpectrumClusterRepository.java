package org.dulab.adapcompounddb.site.repositories;

import org.dulab.adapcompounddb.models.entities.SpectrumCluster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SpectrumClusterRepository extends JpaRepository<SpectrumCluster, Long> {

    void deleteByIdNotIn(List<Long> ids);

    @Modifying
    @Query("DELETE FROM SpectrumCluster c WHERE SIZE(c.spectra) = 1")
    void deleteAllEmptyClusters();
}
