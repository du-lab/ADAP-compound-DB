package org.dulab.adapcompounddb.site.services;

import org.dulab.adapcompounddb.models.QueryParameters;
import org.dulab.adapcompounddb.models.dto.ClusterDTO;
import org.dulab.adapcompounddb.models.entities.Spectrum;
import org.dulab.adapcompounddb.models.entities.SpectrumMatch;

import java.util.List;

public interface SpectrumSearchService {

    List<SpectrumMatch> search(Spectrum spectrum, QueryParameters parameters);

    List<ClusterDTO> searchConsensusSpectra(Spectrum querySpectrum);
}
