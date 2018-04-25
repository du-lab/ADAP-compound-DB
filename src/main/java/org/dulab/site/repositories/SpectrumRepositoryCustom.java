package org.dulab.site.repositories;

import org.dulab.models.search.CriteriaBlock;
import org.dulab.models.Hit;
import org.dulab.models.Spectrum;
import org.dulab.models.UserParameter;

public interface SpectrumRepositoryCustom {

    Iterable<Hit> searchSpectra(Spectrum querySpectrum, CriteriaBlock criteriaBlock,
                                float mzTolerance, int numHits, float scoreThreshold);
}
