package org.dulab.site.repositories;

import org.dulab.models.ChromatographyType;
import org.dulab.models.entities.Spectrum;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface SpectrumRepository extends CrudRepository<Spectrum, Long>, SpectrumRepositoryCustom {

    @Query("SELECT s FROM Spectrum s WHERE s.matches IS EMPTY")
    Iterable<Spectrum> findAllByMatchesIsEmpty();

    @Query("SELECT s FROM Spectrum s WHERE s.matches IS EMPTY " +
            "AND s.consensus=FALSE AND s.submission.chromatographyType = ?1")
    Iterable<Spectrum> findUnmatchedByChromatographyType(ChromatographyType chromatographyType);

    Iterable<Spectrum> findAllByConsensusFalseAndSubmissionChromatographyType(ChromatographyType chromatographyType);

    long countByConsensusIsFalse();

    long countBySubmissionChromatographyTypeAndConsensus(ChromatographyType chromatographyType, boolean consensus);

    @Query("SELECT COUNT(s) FROM Spectrum s WHERE s.matches IS EMPTY " +
            "AND s.consensus=FALSE AND s.submission.chromatographyType = ?1")
    long countUnmatchedBySubmissionChromatographyType(ChromatographyType chromatographyType);
}
