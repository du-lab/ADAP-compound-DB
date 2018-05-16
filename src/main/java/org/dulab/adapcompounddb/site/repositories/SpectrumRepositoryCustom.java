package org.dulab.adapcompounddb.site.repositories;

import org.dulab.adapcompounddb.models.search.CriteriaBlock;
import org.dulab.adapcompounddb.models.Hit;
import org.dulab.adapcompounddb.models.entities.Spectrum;

public interface SpectrumRepositoryCustom {

    Iterable<Hit> searchSpectra(Spectrum querySpectrum, CriteriaBlock criteriaBlock,
                                float mzTolerance, int numHits, float scoreThreshold);

    Iterable<Hit> findSimilarSpectra(Spectrum querySpectrum, float mzTolerance, float scoreThreshold);
}
