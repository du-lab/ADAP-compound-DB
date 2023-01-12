package org.dulab.adapcompounddb.site.repositories;

import org.dulab.adapcompounddb.models.entities.Spectrum;
import org.dulab.adapcompounddb.models.entities.SpectrumMatch;
import org.dulab.adapcompounddb.models.entities.UserPrincipal;
import org.dulab.adapcompounddb.models.enums.ChromatographyType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Repository;

@Repository
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

    @Query("SELECT sm FROM SpectrumMatch sm WHERE sm.querySpectrum.id in :ids AND sm.matchSpectrum.id in :ids")
    Iterable<SpectrumMatch> findBySpectrumIds(@Param("ids") Set<Long> ids);

    @Query("SELECT sm FROM SpectrumMatch sm WHERE sm.querySpectrum.id in :ids")
    Page<SpectrumMatch> findSpectrumMatchById(Pageable page, @Param("ids") List<Long> ids);

    @Modifying
    @Query("DELETE FROM SpectrumMatch sm WHERE sm.querySpectrum.id in :ids")
    void deleteByQuerySpectrums(@Param("ids") List<Long> ids);


    @Query("SELECT sm FROM SpectrumMatch sm WHERE (sm.userPrincipalId=:userid and sm.querySpectrum.id in :ids)")
    List<SpectrumMatch> findAllSpectrumMatchByUserIdAndQuerySpectrums(@Param("userid") Long userId, @Param("ids")List<Long> spectrumIds);

    @Modifying
    @Query("DELETE FROM SpectrumMatch sm WHERE (sm.userPrincipalId=:userid and sm.querySpectrum.id in :ids)")
    void deleteByQuerySpectrumsAndUserId(@Param("userid") long userId, @Param("ids")Set<Long> spectrumIds);
}
