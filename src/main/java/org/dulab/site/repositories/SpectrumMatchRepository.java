package org.dulab.site.repositories;

import org.dulab.models.ChromatographyType;
import org.dulab.models.entities.SpectrumMatch;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface SpectrumMatchRepository extends CrudRepository<SpectrumMatch, Long> {

    List<SpectrumMatch> findAllByQuerySpectrumId(long querySpectrumId);

    List<SpectrumMatch> findAllByQuerySpectrumSubmissionChromatographyType(ChromatographyType chromatographyType);

    @Query("SELECT COUNT(DISTINCT sm.querySpectrum.id) FROM SpectrumMatch sm")
    int countDistinctQuerySpectrum();
}
