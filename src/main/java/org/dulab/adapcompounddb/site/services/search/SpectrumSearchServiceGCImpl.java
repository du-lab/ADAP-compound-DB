package org.dulab.adapcompounddb.site.services.search;

import org.dulab.adapcompounddb.models.MatchType;
import org.dulab.adapcompounddb.models.QueryParameters;
import org.dulab.adapcompounddb.models.SearchType;
import org.dulab.adapcompounddb.models.dto.SearchResultDTO;
import org.dulab.adapcompounddb.models.entities.Spectrum;
import org.dulab.adapcompounddb.models.entities.SpectrumMatch;
import org.dulab.adapcompounddb.models.entities.views.SpectrumClusterView;
import org.dulab.adapcompounddb.site.controllers.ControllerUtils;
import org.dulab.adapcompounddb.site.repositories.SpectrumClusterRepository;
import org.dulab.adapcompounddb.site.repositories.SpectrumRepository;
import org.dulab.adapcompounddb.site.repositories.SubmissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Service
public class SpectrumSearchServiceGCImpl implements SpectrumSearchService {

    private final SpectrumRepository spectrumRepository;
    private final SpectrumClusterRepository spectrumClusterRepository;
    private final SubmissionRepository submissionRepository;

    @Autowired
    public SpectrumSearchServiceGCImpl(SpectrumRepository spectrumRepository,
                                       SpectrumClusterRepository spectrumClusterRepository,
                                       SubmissionRepository submissionRepository) {
        this.spectrumRepository = spectrumRepository;
        this.spectrumClusterRepository = spectrumClusterRepository;
        this.submissionRepository = submissionRepository;
    }

    @Override
    @Transactional
    public List<SpectrumMatch> search(Spectrum querySpectrum, QueryParameters parameters) {
        return spectrumRepository.spectrumSearch(SearchType.SIMILARITY_SEARCH, querySpectrum, parameters);
    }

    @Override
    @Transactional
    public List<SearchResultDTO> searchConsensusSpectra(Spectrum querySpectrum, double scoreThreshold, double mzTolerance,
                                                        String species, String source, String disease) {

        Iterable<BigInteger> submissionIds = submissionRepository.findSubmissionIdsBySubmissionTags(species, source, disease);

        List<SearchResultDTO> searchResults = new ArrayList<>();
        for (SpectrumClusterView view : spectrumRepository.searchLibrarySpectra(
                querySpectrum, scoreThreshold, mzTolerance, submissionIds)) {

            SearchResultDTO searchResult = new SearchResultDTO(querySpectrum, view);

            spectrumRepository.findById(view.getId())
                    .ifPresent(c -> searchResult.setJson(ControllerUtils
                            .spectrumToJson(c)
                            .toString()));

            searchResults.add(searchResult);
        }
        return searchResults;
    }
}
