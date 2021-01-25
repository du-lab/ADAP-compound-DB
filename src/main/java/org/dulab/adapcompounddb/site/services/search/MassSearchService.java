package org.dulab.adapcompounddb.site.services.search;

import org.dulab.adapcompounddb.models.QueryParameters;
import org.dulab.adapcompounddb.models.dto.SearchResultDTO;
import org.dulab.adapcompounddb.models.entities.Spectrum;
import org.dulab.adapcompounddb.models.entities.SpectrumMatch;
import org.dulab.adapcompounddb.models.entities.UserPrincipal;
import org.dulab.adapcompounddb.models.entities.views.SpectrumClusterView;
import org.dulab.adapcompounddb.site.controllers.ControllerUtils;
import org.dulab.adapcompounddb.site.repositories.SpectrumRepository;
import org.dulab.adapcompounddb.models.entities.views.MassSearchResult;
import org.dulab.adapcompounddb.site.repositories.SubmissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Service
@Deprecated
public class MassSearchService implements IndividualSearchService {

    private final SpectrumRepository spectrumRepository;
    private final SubmissionRepository submissionRepository;

    @Autowired
    public MassSearchService(SpectrumRepository spectrumRepository, SubmissionRepository submissionRepository) {
        this.spectrumRepository = spectrumRepository;
        this.submissionRepository = submissionRepository;
    }

    @Override
    public List<SpectrumMatch> search(Spectrum spectrum, QueryParameters parameters) {
        return null;
    }

    @Override
    public List<SearchResultDTO> searchConsensusSpectra(UserPrincipal user, Spectrum querySpectrum,
                                                        SearchParameters parameters) {

        Iterable<BigInteger> submissionIds = submissionRepository.findSubmissionIdsBySubmissionTags(
                user != null ? user.getId() : null,
                parameters.getSpecies(), parameters.getSource(), parameters.getDisease());

        List<SearchResultDTO> searchResults = new ArrayList<>();
        for (SpectrumClusterView view : spectrumRepository.searchLibrarySpectra(
                submissionIds, querySpectrum, null, null, null,
                parameters.getMolecularWeightTolerance())) {

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
