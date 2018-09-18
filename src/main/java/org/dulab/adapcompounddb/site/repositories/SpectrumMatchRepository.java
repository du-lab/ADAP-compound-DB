package org.dulab.adapcompounddb.site.repositories;

import org.dulab.adapcompounddb.models.entities.SpectrumMatch;
import org.dulab.adapcompounddb.models.ChromatographyType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SpectrumMatchRepository extends JpaRepository<SpectrumMatch, Long> {

    List<SpectrumMatch> findAllByQuerySpectrumId(long querySpectrumId);

    List<SpectrumMatch> findAllByQuerySpectrumChromatographyType(ChromatographyType chromatographyType);

    @Query("SELECT COUNT(DISTINCT sm.querySpectrum.id) FROM SpectrumMatch sm")
    int countDistinctQuerySpectrum();

    long countByQuerySpectrumChromatographyType(ChromatographyType type);

    @Query("SELECT sm FROM SpectrumMatch sm " +
            "WHERE NOT sm.matchSpectrum.id = sm.querySpectrum.id AND sm.querySpectrum.chromatographyType = ?1 " +
            "ORDER BY sm.score DESC, sm.querySpectrum.id ASC, sm.matchSpectrum.id ASC")
    Page<SpectrumMatch> findByChromatographyType(ChromatographyType type, Pageable pageable);
}
