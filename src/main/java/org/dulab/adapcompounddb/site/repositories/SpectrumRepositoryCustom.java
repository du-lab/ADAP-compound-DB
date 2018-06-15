package org.dulab.adapcompounddb.site.repositories;

import org.dulab.adapcompounddb.models.QueryParameters;
import org.dulab.adapcompounddb.models.entities.SpectrumMatch;
import org.dulab.adapcompounddb.models.search.CriteriaBlock;
import org.dulab.adapcompounddb.models.Hit;
import org.dulab.adapcompounddb.models.entities.Spectrum;

import java.util.List;

public interface SpectrumRepositoryCustom {

    Iterable<Hit> searchSpectra(Spectrum querySpectrum, CriteriaBlock criteriaBlock,
                                float mzTolerance, int numHits, float scoreThreshold);

    Iterable<Hit> findSimilarSpectra(Spectrum querySpectrum, float mzTolerance, float scoreThreshold);

//    List<Hit> retTimePrecursorMsMsSearch(Spectrum querySpectrum, QueryParameters params);
//
//    List<Hit> retTimePrecursorSearch(Spectrum querySpectrum, QueryParameters params);
//
//    List<Hit> precursorMsMsSearch(Spectrum querySpectrum, QueryParameters params);
//
//    List<Hit> precursorSearch(Spectrum querySpectrum, QueryParameters params);

    List<SpectrumMatch> spectrumSearch(Spectrum querySpectrum, QueryParameters params);
}
