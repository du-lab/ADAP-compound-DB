package org.dulab.adapcompounddb.site.services.search;

import org.dulab.adapcompounddb.site.services.admin.QueryParameters;
import org.dulab.adapcompounddb.models.dto.SearchResultDTO;
import org.dulab.adapcompounddb.models.entities.Spectrum;
import org.dulab.adapcompounddb.models.entities.SpectrumMatch;
import org.dulab.adapcompounddb.models.entities.UserPrincipal;

import java.util.List;

public interface IndividualSearchService {

    List<SpectrumMatch> search(Spectrum spectrum, QueryParameters parameters);

    List<SearchResultDTO> searchConsensusSpectra(
            UserPrincipal user, Spectrum querySpectrum, SearchParameters parameters, boolean withOntologyLevels);
}
