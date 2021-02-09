package org.dulab.adapcompounddb.site.services.search;

import org.dulab.adapcompounddb.site.services.admin.QueryParameters;
import org.dulab.adapcompounddb.models.SearchType;
import org.dulab.adapcompounddb.models.dto.SearchResultDTO;
import org.dulab.adapcompounddb.models.entities.Spectrum;
import org.dulab.adapcompounddb.models.entities.SpectrumMatch;
import org.dulab.adapcompounddb.models.entities.UserPrincipal;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SpectrumSearchServiceImpl implements IndividualSearchService {

    private final SpectrumRepository spectrumRepository;
    private final SpectrumClusterRepository spectrumClusterRepository;
    private final SubmissionRepository submissionRepository;

    @Autowired
    public SpectrumSearchServiceImpl(SpectrumRepository spectrumRepository,
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
    public List<SearchResultDTO> searchConsensusSpectra(UserPrincipal user, Spectrum querySpectrum,
                                                        SearchParameters parameters) {

        Set<BigInteger> submissionIds = (parameters.getSubmissionIds() != null)
                ? parameters.getSubmissionIds().stream().map(BigInteger::valueOf).collect(Collectors.toSet())
                : new HashSet<>();

        if (submissionIds.isEmpty() || submissionIds.contains(BigInteger.ZERO)) {
            Iterable<BigInteger> publicSubmissionIds = submissionRepository.findSubmissionIdsBySubmissionTags(
                    parameters.getSpecies(), parameters.getSource(), parameters.getDisease());
            publicSubmissionIds.forEach(submissionIds::add);
            submissionIds.remove(BigInteger.ZERO);
        }

        List<SearchResultDTO> searchResults = new ArrayList<>();
        for (SpectrumClusterView view : spectrumRepository.matchAgainstConsensusAndReferenceSpectra(
                submissionIds, querySpectrum, parameters.getScoreThreshold(), parameters.getMzTolerance(),
                parameters.getPrecursorTolerance(), parameters.getMolecularWeightTolerance())) {

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
