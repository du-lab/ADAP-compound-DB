package org.dulab.adapcompounddb.site.repositories;

import org.dulab.adapcompounddb.models.ChromatographyType;
import org.dulab.adapcompounddb.models.entities.Spectrum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface SpectrumRepository extends CrudRepository<Spectrum, Long>, SpectrumRepositoryCustom {

    @Query("SELECT s FROM Spectrum s WHERE s.matches IS EMPTY")
    Iterable<Spectrum> findAllByMatchesIsEmpty();

    @Query("SELECT s FROM Spectrum s WHERE s.matches IS EMPTY "
            + "AND s.consensus=FALSE AND s.reference=FALSE AND s.chromatographyType = ?1")
    Iterable<Spectrum> findUnmatchedByChromatographyType(ChromatographyType chromatographyType);

    @Query("SELECT COUNT(s) FROM Spectrum s WHERE s.matches IS EMPTY AND s.consensus=FALSE AND s.reference=FALSE")
    long countUnmatched();

    Iterable<Spectrum> findAllByConsensusFalseAndReferenceFalseAndChromatographyType(
            ChromatographyType chromatographyType);

    @Query("SELECT s FROM Spectrum s WHERE " +
            "(SIZE(s.matches) > 1 OR SIZE(s.matches2) > 1) " +
            "AND s.consensus=FALSE " +
            "AND s.reference=FALSE " +
            "AND s.chromatographyType=?1")
    Iterable<Spectrum> findSpectraForClustering(ChromatographyType type);

    long countByConsensusIsFalse();

    long countByChromatographyTypeAndConsensusFalse(ChromatographyType chromatographyType);

    long countByChromatographyTypeAndConsensusTrue(ChromatographyType chromatographyType);

    @Query(value = "select s from Spectrum s " + "inner join s.file f " + "inner join f.submission sub "
            + "where sub.id = ?1 " + "and (s.name like %?2% or f.name like %?2%)")
    Page<Spectrum> findSpectrumBySubmissionId(Long submissionId, String searchStr, Pageable pageable);

    @Query("SELECT COUNT(s) FROM Spectrum s WHERE s.matches IS EMPTY "
            + "AND s.consensus=FALSE AND s.chromatographyType = ?1")
    long countUnmatchedBySubmissionChromatographyType(ChromatographyType chromatographyType);

    long countByConsensusTrue();

    long countByReferenceTrue();
}
