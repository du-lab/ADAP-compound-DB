package org.dulab.adapcompounddb.site.repositories;

import org.dulab.adapcompounddb.models.QueryParameters;
import org.dulab.adapcompounddb.models.SearchType;
import org.dulab.adapcompounddb.models.entities.SpectrumMatch;
import org.dulab.adapcompounddb.models.search.CriteriaBlock;
import org.dulab.adapcompounddb.models.Hit;
import org.dulab.adapcompounddb.models.entities.Spectrum;

import java.util.List;

public interface SpectrumRepositoryCustom {

    List<SpectrumMatch> spectrumSearch(SearchType searchType, Spectrum querySpectrum, QueryParameters params);
}
