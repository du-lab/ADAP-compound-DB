package org.dulab.adapcompounddb.site.repositories;

import org.dulab.adapcompounddb.models.ChromatographyType;
import org.dulab.adapcompounddb.models.entities.Spectrum;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface SpectrumRepository extends CrudRepository<Spectrum, Long>, SpectrumRepositoryCustom {

    @Query("SELECT s FROM Spectrum s WHERE s.matches IS EMPTY")
    Iterable<Spectrum> findAllByMatchesIsEmpty();

    @Query("SELECT s FROM Spectrum s WHERE s.matches IS EMPTY " +
            "AND s.consensus=FALSE AND s.reference=FALSE AND s.chromatographyType = ?1")
    Iterable<Spectrum> findUnmatchedByChromatographyType(ChromatographyType chromatographyType);

    @Query("SELECT COUNT(s) FROM Spectrum s WHERE s.matches IS EMPTY AND s.consensus=FALSE AND s.reference=FALSE")
    long countUnmatched();

    Iterable<Spectrum> findAllByConsensusFalseAndReferenceFalseAndChromatographyType(
            ChromatographyType chromatographyType);

    long countByConsensusIsFalse();

    long countByChromatographyTypeAndConsensusFalse(ChromatographyType chromatographyType);

    long countByChromatographyTypeAndConsensusTrue(ChromatographyType chromatographyType);

    @Query("SELECT COUNT(s) FROM Spectrum s WHERE s.matches IS EMPTY " +
            "AND s.consensus=FALSE AND s.chromatographyType = ?1")
    long countUnmatchedBySubmissionChromatographyType(ChromatographyType chromatographyType);
}
