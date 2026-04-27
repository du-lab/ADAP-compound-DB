package org.dulab.adapcompounddb.site.repositories;

import org.dulab.adapcompounddb.models.entities.TempSpectrumMatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface TempSpectrumMatchRepository extends JpaRepository<TempSpectrumMatch, Long> {

    List<TempSpectrumMatch> findBySessionIdAndFileIndexAndSpectrumIndex(
            String sessionId, int fileIndex, int spectrumIndex);

    @Query("SELECT DISTINCT t.querySpectrumName FROM TempSpectrumMatch t " +
            "WHERE t.sessionId = :sessionId " +
            "AND (:showMatchesOnly = 0 OR t.matchSpectrum IS NOT NULL) " +
            "AND (:ontologyLevel = '' OR t.ontologyLevel = :ontologyLevel) " +
            "AND (:scoreThreshold IS NULL OR t.score > :scoreThreshold) " +
            "AND (:massError IS NULL OR t.massError < :massError) " +
            "AND (:retTimeError IS NULL OR t.retTimeError < :retTimeError)")
    List<String> findDistinctQueryNames(@Param("sessionId") String sessionId,
                                        @Param("showMatchesOnly") int showMatchesOnly,
                                        @Param("ontologyLevel") String ontologyLevel,
                                        @Param("scoreThreshold") Double scoreThreshold,
                                        @Param("massError") Double massError,
                                        @Param("retTimeError") Double retTimeError);

    @Query("SELECT t FROM TempSpectrumMatch t JOIN FETCH t.matchSpectrum " +
            "WHERE t.sessionId = :sessionId AND t.querySpectrumName = :name " +
            "AND (:showMatchesOnly = 0 OR t.matchSpectrum IS NOT NULL) " +
            "AND (:ontologyLevel = '' OR t.ontologyLevel = :ontologyLevel) " +
            "AND (:scoreThreshold IS NULL OR t.score > :scoreThreshold) " +
            "AND (:massError IS NULL OR t.massError < :massError) " +
            "AND (:retTimeError IS NULL OR t.retTimeError < :retTimeError)")
    List<TempSpectrumMatch> findMatchesBySessionAndQueryName(
            @Param("sessionId") String sessionId,
            @Param("name") String querySpectrumName,
            @Param("showMatchesOnly") int showMatchesOnly,
            @Param("ontologyLevel") String ontologyLevel,
            @Param("scoreThreshold") Double scoreThreshold,
            @Param("massError") Double massError,
            @Param("retTimeError") Double retTimeError);

    @Modifying
    @Transactional
    @Query("DELETE FROM TempSpectrumMatch t WHERE t.sessionId = :sessionId")
    void deleteBySessionId(@Param("sessionId") String sessionId);

    @Query("SELECT DISTINCT t.ontologyLevel FROM TempSpectrumMatch t " +
            "WHERE t.sessionId = :sessionId AND t.ontologyLevel IS NOT NULL")
    List<String> findDistinctOntologyLevelsBySessionId(@Param("sessionId") String sessionId);
}
