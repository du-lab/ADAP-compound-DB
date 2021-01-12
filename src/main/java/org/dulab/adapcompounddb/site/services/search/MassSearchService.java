package org.dulab.adapcompounddb.site.services.search;

import org.dulab.adapcompounddb.models.QueryParameters;
import org.dulab.adapcompounddb.models.dto.SearchResultDTO;
import org.dulab.adapcompounddb.models.entities.Spectrum;
import org.dulab.adapcompounddb.models.entities.SpectrumMatch;
import org.dulab.adapcompounddb.site.repositories.SpectrumRepository;
import org.dulab.adapcompounddb.models.entities.views.MassSearchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MassSearchService implements SpectrumSearchService {

    private final SpectrumRepository spectrumRepository;

    @Autowired
    public MassSearchService(SpectrumRepository spectrumRepository) {
        this.spectrumRepository = spectrumRepository;
    }

    @Override
    public List<SpectrumMatch> search(Spectrum spectrum, QueryParameters parameters) {
        return null;
    }

    @Override
    public List<SearchResultDTO> searchConsensusSpectra(Spectrum querySpectrum, double scoreThreshold, double mzTolerance,
                                                        String species, String source, String disease) {

        List<SearchResultDTO> searchResults = new ArrayList<>();
        for (MassSearchResult massSearchResult : spectrumRepository.searchLibraryMasses(
                querySpectrum, mzTolerance, species, source, disease)) {

            SearchResultDTO searchResult = new SearchResultDTO(querySpectrum, massSearchResult);
            searchResults.add(searchResult);
        }
        return searchResults;
    }
}
