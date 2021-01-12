package org.dulab.adapcompounddb.site.services.search;

import org.dulab.adapcompounddb.models.ChromatographyType;
import org.dulab.adapcompounddb.models.QueryParameters;
import org.dulab.adapcompounddb.models.dto.SearchResultDTO;
import org.dulab.adapcompounddb.models.entities.Spectrum;
import org.dulab.adapcompounddb.models.entities.SpectrumMatch;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface SpectrumSearchService {

    List<SpectrumMatch> search(Spectrum spectrum, QueryParameters parameters);

    List<SearchResultDTO> searchConsensusSpectra(Spectrum querySpectrum, double scoreThreshold, double mzTolerance,
                                                 String species, String source, String disease);
}
