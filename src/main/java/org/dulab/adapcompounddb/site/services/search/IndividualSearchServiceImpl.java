package org.dulab.adapcompounddb.site.services.search;

import org.dulab.adapcompounddb.models.entities.Adduct;
import org.dulab.adapcompounddb.models.ontology.OntologyLevel;
import org.dulab.adapcompounddb.models.ontology.OntologySupplier;
import org.dulab.adapcompounddb.site.services.AdductService;
import org.dulab.adapcompounddb.site.services.admin.QueryParameters;
import org.dulab.adapcompounddb.models.SearchType;
import org.dulab.adapcompounddb.models.dto.SearchResultDTO;
import org.dulab.adapcompounddb.models.entities.Spectrum;
import org.dulab.adapcompounddb.models.entities.SpectrumMatch;
import org.dulab.adapcompounddb.models.entities.UserPrincipal;
import org.dulab.adapcompounddb.models.entities.views.SpectrumClusterView;
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
public class IndividualSearchServiceImpl implements IndividualSearchService {

    private final SpectrumRepository spectrumRepository;
    private final SubmissionRepository submissionRepository;
    private final AdductService adductService;
    private final JavaSpectrumSimilarityService javaSpectrumSimilarityService;

    @Autowired
    public IndividualSearchServiceImpl(SpectrumRepository spectrumRepository,
                                       SubmissionRepository submissionRepository,
                                       AdductService adductService,
                                       JavaSpectrumSimilarityService javaSpectrumSimilarityService) {

        this.spectrumRepository = spectrumRepository;
        this.submissionRepository = submissionRepository;
        this.adductService = adductService;
        this.javaSpectrumSimilarityService = javaSpectrumSimilarityService;
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
                ? parameters.getSubmissionIds()
                : new HashSet<>();

        if (submissionIds.isEmpty() || submissionIds.contains(BigInteger.ZERO)) {
            Iterable<BigInteger> publicSubmissionIds = submissionRepository.findSubmissionIdsBySubmissionTags(
                    parameters.getSpecies(), parameters.getSource(), parameters.getDisease());
            publicSubmissionIds.forEach(submissionIds::add);
            submissionIds.remove(BigInteger.ZERO);
        }

        // Check if there are ontology levels for a given chromatography type
        int[] priorities = OntologySupplier.findPrioritiesByChromatographyType(querySpectrum.getChromatographyType());
        if (priorities == null) {
            // There is no ontology levels. Perform simple search.
            return searchWithoutOntologyLevels(submissionIds, querySpectrum, parameters, user);
        } else {
            // There are ontology levels. Perform a search for each ontology level until the there any matches
            for (int priority : priorities) {
                List<SearchResultDTO> searchResults =
                        searchWithOntologyLevels(submissionIds, parameters, querySpectrum, user, priority);
                if (!searchResults.isEmpty())
                    return searchResults;
            }
        }

        return new ArrayList<>(0);
    }

    private List<SearchResultDTO> searchWithoutOntologyLevels(
            Set<BigInteger> submissionIds, Spectrum querySpectrum, SearchParameters parameters, UserPrincipal user) {

        List<SearchResultDTO> searchResults = new ArrayList<>();
//        for (SpectrumClusterView view : spectrumRepository.matchAgainstConsensusAndReferenceSpectra(
//                null, submissionIds, querySpectrum, parameters)) {
//            SearchResultDTO searchResult = new SearchResultDTO(querySpectrum, view);
//            searchResults.add(searchResult);
//        }
        for (SpectrumMatch match
                : javaSpectrumSimilarityService.searchConsensusAndReference(querySpectrum, parameters, user)) {

            SpectrumClusterView view = MappingUtils.mapSpectrumMatchToSpectrumClusterView(
                    match, parameters.getSpecies(), parameters.getSource(), parameters.getDisease());
            SearchResultDTO searchResult = new SearchResultDTO(querySpectrum, view);
            searchResults.add(searchResult);
        }

        return searchResults;
    }

    private List<SearchResultDTO> searchWithOntologyLevels(Set<BigInteger> submissionIds, SearchParameters parameters,
                                                           Spectrum spectrum, UserPrincipal user, int priority) {

        OntologyLevel[] ontologyLevels =
                OntologySupplier.findByChromatographyTypeAndPriority(spectrum.getChromatographyType(), priority);
        if (ontologyLevels == null)
            return new ArrayList<>(0);

        List<Adduct> adducts = adductService.findAdductsByChromatography(spectrum.getChromatographyType());

        List<SearchResultDTO> searchResults = new ArrayList<>();
        for (OntologyLevel ontologyLevel : ontologyLevels) {

            // Check the presence of necessary properties
            if (ontologyLevel.getMzTolerancePPM() != null && ontologyLevel.getScoreThreshold() != null && spectrum.getPeaks() == null)
                continue;
            if (ontologyLevel.getPrecursorTolerancePPM() != null && spectrum.getPrecursor() == null)
                continue;
            if (ontologyLevel.getRetTimeTolerance() != null && spectrum.getRetentionTime() == null)
                continue;

            // Modify search parameters
            SearchParameters modifiedParameters;
            try {
                modifiedParameters = parameters.clone();
            } catch (CloneNotSupportedException e) {
                throw new IllegalStateException(e.getMessage(), e);
            }
            modifiedParameters.setMzTolerance(null, ontologyLevel.getMzTolerancePPM());
            modifiedParameters.setScoreThreshold(ontologyLevel.getScoreThreshold());
            modifiedParameters.setPrecursorTolerance(null, ontologyLevel.getPrecursorTolerancePPM());
            modifiedParameters.setMassTolerance(null, ontologyLevel.getMassTolerancePPM());
            modifiedParameters.setRetTimeTolerance(ontologyLevel.getRetTimeTolerance());
            if (spectrum.getMass() == null && adducts != null && spectrum.getPrecursor() != null)
                modifiedParameters.setMasses(adducts.stream()
                        .mapToDouble(adduct -> adduct.calculateNeutralMass(spectrum.getPrecursor()))
                        .toArray());

            if (ontologyLevel.getMassTolerancePPM() != null && spectrum.getMass() == null
                    && modifiedParameters.getMasses() == null)
                continue;

            // Perform search
//            List<SearchResultDTO> results = MappingUtils.toList(
//                    spectrumRepository.matchAgainstConsensusAndReferenceSpectra(
//                            null, submissionIds, spectrum, modifiedParameters))
//                    .stream()
//                    .map(x -> new SearchResultDTO(spectrum, x))
//                    .collect(Collectors.toList());

            List<SearchResultDTO> results =
                    javaSpectrumSimilarityService.searchConsensusAndReference(spectrum, modifiedParameters, user)
                            .stream()
                            .map(match -> MappingUtils.mapSpectrumMatchToSpectrumClusterView(match,
                                    modifiedParameters.getSpecies(),
                                    modifiedParameters.getSource(),
                                    modifiedParameters.getDisease()))
                            .map(view -> new SearchResultDTO(spectrum, view))
                            .collect(Collectors.toList());

            results.forEach(r -> r.setOntologyLevel(ontologyLevel));

            searchResults.addAll(results);
        }

        return searchResults;
    }
}
