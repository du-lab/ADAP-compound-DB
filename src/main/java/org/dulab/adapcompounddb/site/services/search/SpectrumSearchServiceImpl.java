package org.dulab.adapcompounddb.site.services.search;

import org.dulab.adapcompounddb.models.enums.OntologyLevel;
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
import org.dulab.adapcompounddb.site.services.utils.MappingUtils;
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
                                                        SearchParameters parameters, boolean withOntologyLevels) {

        Set<BigInteger> submissionIds = (parameters.getSubmissionIds() != null)
                ? parameters.getSubmissionIds().stream().map(BigInteger::valueOf).collect(Collectors.toSet())
                : new HashSet<>();

        if (submissionIds.isEmpty() || submissionIds.contains(BigInteger.ZERO)) {
            Iterable<BigInteger> publicSubmissionIds = submissionRepository.findSubmissionIdsBySubmissionTags(
                    parameters.getSpecies(), parameters.getSource(), parameters.getDisease());
            publicSubmissionIds.forEach(submissionIds::add);
            submissionIds.remove(BigInteger.ZERO);
        }

        List<SearchResultDTO> searchResults = withOntologyLevels
                ? searchWithOntologyLevels(submissionIds, querySpectrum)
                : searchWithoutOntologyLevels(submissionIds, querySpectrum, parameters);

//        List<SearchResultDTO> searchResults = new ArrayList<>();
//        for (SpectrumClusterView view : spectrumRepository.matchAgainstConsensusAndReferenceSpectra(
//                submissionIds, querySpectrum, parameters.getScoreThreshold(), parameters.getMzTolerance(),
//                parameters.getPrecursorTolerance(), parameters.getMassTolerance(),
//                parameters.getRetTimeTolerance())) {
//
//            SearchResultDTO searchResult = new SearchResultDTO(querySpectrum, view);
//
//            spectrumRepository.findById(view.getId())
//                    .ifPresent(c -> searchResult.setJson(ControllerUtils
//                            .spectrumToJson(c)
//                            .toString()));
//
//            searchResults.add(searchResult);
//        }
        return searchResults;
    }

    private List<SearchResultDTO> searchWithoutOntologyLevels(
            Set<BigInteger> submissionIds, Spectrum querySpectrum, SearchParameters parameters) {

        List<SearchResultDTO> searchResults = new ArrayList<>();
        for (SpectrumClusterView view : spectrumRepository.matchAgainstConsensusAndReferenceSpectra(
                submissionIds, querySpectrum, parameters.getScoreThreshold(), parameters.getMzTolerance(),
                parameters.getPrecursorTolerance(), parameters.getMassTolerance(),
                parameters.getRetTimeTolerance())) {

            SearchResultDTO searchResult = new SearchResultDTO(querySpectrum, view);

            spectrumRepository.findById(view.getId())
                    .ifPresent(c -> searchResult.setJson(ControllerUtils
                            .spectrumToJson(c)
                            .toString()));

            searchResults.add(searchResult);
        }

        return searchResults;
    }

    private List<SearchResultDTO> searchWithOntologyLevels(Set<BigInteger> submissionIds, Spectrum spectrum) {
        for (int priority : OntologyLevel.priorities()) {
            OntologyLevel[] ontologyLevels = OntologyLevel.findByPriority(priority);
            List<SearchResultDTO> searchResults = new ArrayList<>();
            for (OntologyLevel ontologyLevel : ontologyLevels) {
                SearchParameters params = ontologyLevel.getSearchParameters();
                // Check the presence of necessary properties
                if (params.getMzTolerance() != null && params.getScoreThreshold() != null && spectrum.getPeaks() == null)
                    continue;
                if (params.getPrecursorTolerance() != null && spectrum.getPrecursor() == null)
                    continue;
                if (params.getRetTimeTolerance() != null && spectrum.getRetentionTime() == null)
                    continue;
                if (params.getMassTolerance() != null && spectrum.getMolecularWeight() == null)
                    continue;

                // Perform search
                List<SearchResultDTO> results = MappingUtils.toList(
                        spectrumRepository.matchAgainstConsensusAndReferenceSpectra(
                                submissionIds, spectrum, params.getScoreThreshold(), params.getMzTolerance(),
                                params.getPrecursorTolerance(), params.getMassTolerance(), params.getRetTimeTolerance()))
                        .stream()
                        .map(x -> new SearchResultDTO(spectrum, x))
                        .collect(Collectors.toList());
                results.forEach(x -> x.setOntologyLevel(ontologyLevel));
                searchResults.addAll(results);
            }
            if (!searchResults.isEmpty())
                return searchResults;
        }

        return new ArrayList<>(0);
    }
}
